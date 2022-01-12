package com.github.xpenatan.gdx.backends.web.preloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Stream;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.web.WebBuildConfiguration;
import com.github.xpenatan.gdx.backends.web.WebClassLoader;

/**
 * @author xpenatan
 */
public class AssetsCopy {
	private static class Asset {
		FileWrapper file;
		AssetType type;

		public Asset (FileWrapper file, AssetType type) {
			this.file = file;
			this.type = type;
		}
	}

	public static void copy (ArrayList<File> assetsPaths, String assetsOutputPath, boolean generateTextFile) {
		copy(null, null, assetsPaths, assetsOutputPath, generateTextFile);
	}

	public static void copy (WebClassLoader classloader, ArrayList<String> classPathAssetsFiles, ArrayList<File> assetsPaths, String assetsOutputPath, boolean generateTextFile) {
		assetsOutputPath = assetsOutputPath.replace("\\", "/");
		FileWrapper target = new FileWrapper(assetsOutputPath);
		ArrayList<Asset> assets = new ArrayList<Asset>();
		DefaultAssetFilter defaultAssetFilter = new DefaultAssetFilter();
		WebBuildConfiguration.log("");
		WebBuildConfiguration.log("Copying assets from:");
		for (int i = 0; i < assetsPaths.size(); i++) {
			String path = assetsPaths.get(i).getAbsolutePath();
			FileWrapper source = new FileWrapper(path);
			WebBuildConfiguration.log(path);
			copyDirectory(source, target, defaultAssetFilter, assets);
		}

		if(classloader != null && classPathAssetsFiles != null) {
			// Copy assets from class package directory

			addDirectoryClassPathFiles(classPathAssetsFiles);
			WebBuildConfiguration.log("");
			WebBuildConfiguration.log("Copying classpath asset from:");
			for (String classpathFile : classPathAssetsFiles) {
				String path = classpathFile;
				if(path.startsWith("/") == false) {
					path = "/" + path;
				}
				else {
					classpathFile = classpathFile.replaceFirst("/", "");
				}
				if (defaultAssetFilter.accept(path, false)) {
					try {
						WebBuildConfiguration.log(classpathFile);
						InputStream is = classloader.getResourceAsStream(classpathFile);
						FileWrapper dest = target.child(classpathFile);
						dest.write(is, false);
						String destPath = dest.path();
						if(!destPath.endsWith(".js") && !destPath.endsWith(".wasm")) {
							assets.add(new Asset(dest, defaultAssetFilter.getType(destPath)));
						}
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		WebBuildConfiguration.log("");
		WebBuildConfiguration.log("to:");
		WebBuildConfiguration.log(assetsOutputPath);
		if (generateTextFile == false) return;

		HashMap<String, ArrayList<Asset>> bundles = new HashMap<String, ArrayList<Asset>>();
		for (Asset asset : assets) {
			String bundleName = defaultAssetFilter.getBundleName(asset.file.path());
			if (bundleName == null) {
				bundleName = "assets";
			}
			ArrayList<Asset> bundleAssets = bundles.get(bundleName);
			if (bundleAssets == null) {
				bundleAssets = new ArrayList<Asset>();
				bundles.put(bundleName, bundleAssets);
			}
			bundleAssets.add(asset);
		}

		for (Entry<String, ArrayList<Asset>> bundle : bundles.entrySet()) {
			StringBuffer buffer = new StringBuffer();
			for (Asset asset : bundle.getValue()) {
				String path = asset.file.path().replace('\\', '/').replace(assetsOutputPath, "").replaceFirst("assets/", "");
				if (path.startsWith("/")) path = path.substring(1);
				buffer.append(asset.type.code);
				buffer.append(":");
				buffer.append(path);
				buffer.append(":");
				buffer.append(asset.file.isDirectory() ? 0 : asset.file.length());
				buffer.append(":");
				String mimetype = URLConnection.guessContentTypeFromName(asset.file.name());
				buffer.append(mimetype == null ? "application/unknown" : mimetype);
				buffer.append("\n");
			}
			target.child(bundle.getKey() + ".txt").writeString(buffer.toString(), false);
		}
	}

	private static void addDirectoryClassPathFiles(ArrayList<String> classPathFiles) {
		ArrayList<String> folderFilePaths = new ArrayList<>();
		for (int k = 0; k < classPathFiles.size(); k++) {
			String classpathFile = classPathFiles.get(k);
			classpathFile = classpathFile.replace("\\", "/");
			if(classpathFile.startsWith("/") == false)
				classpathFile = "/" + classpathFile;
			URL resource = AssetsCopy.class.getResource(classpathFile);
			if (resource != null) {
				URI uri = null;
				try {
					uri = resource.toURI();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
				if(uri == null)
					continue;

				// solution to get files inside jar/folder
				Path myPath = null;
				String scheme = uri.getScheme();
				FileSystem fileSystem = null;
				if (scheme.equals("jar")) {
					try {
						fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object> emptyMap());
					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}
					myPath = fileSystem.getPath(classpathFile);
				} else {
					myPath = Paths.get(uri);
				}
				Stream<Path> walk = null;
				try {
					walk = Files.walk(myPath, 1);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
				boolean first = true;
				for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
					Path next = it.next();
					String path = next.toString();
					boolean directory = Files.isDirectory(next);
					path = path.replace("\\", "/");
					int i = path.lastIndexOf(classpathFile); // remove absolute path
					path = path.substring(i+1);
					if(path.startsWith("/") == false)
						path = "/" + path;
					if(path.contains(".class") || path.contains(".java"))
						continue;
					if(directory) {
						if(first) {
							first = false;
							classPathFiles.remove(k);
							k--;
							continue;
						}
						else {
							classPathFiles.add(path);
							continue;
						}
					}
					folderFilePaths.add(path);
				}
				walk.close();
				if(fileSystem != null) {
					try {
						fileSystem.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			else {// remove if it dont exist
				classPathFiles.remove(k);
				k--;
			}
		}
		classPathFiles.addAll(folderFilePaths);
	}

	private static void copyFile (FileWrapper source, FileWrapper dest, AssetFilter filter, ArrayList<Asset> assets) {
		if (!filter.accept(dest.path(), false)) return;
		try {
			assets.add(new Asset(dest, filter.getType(dest.path())));
			InputStream read = source.read();
			dest.write(read, false);
			read.close();
		} catch (Exception ex) {
			throw new GdxRuntimeException("Error copying source file: " + source + "\n" //
				+ "To destination: " + dest, ex);
		}
	}

	private static void copyDirectory (FileWrapper sourceDir, FileWrapper destDir, AssetFilter filter, ArrayList<Asset> assets) {
		if (!filter.accept(destDir.path(), true)) return;
		// assets.add(new Asset(destDir, AssetType.Directory));
		destDir.mkdirs();
		FileWrapper[] files = sourceDir.list();
		for (int i = 0, n = files.length; i < n; i++) {
			FileWrapper srcFile = files[i];
			FileWrapper destFile = destDir.child(srcFile.name());
			if (srcFile.isDirectory())
				copyDirectory(srcFile, destFile, filter, assets);
			else
				copyFile(srcFile, destFile, filter, assets);
		}
	}
}
