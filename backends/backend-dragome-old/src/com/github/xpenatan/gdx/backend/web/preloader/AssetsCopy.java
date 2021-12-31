/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** @author xpenatan */
public class AssetsCopy {
	private static class Asset {
		FileWrapper file;
		AssetType type;

		public Asset (FileWrapper file, AssetType type) {
			this.file = file;
			this.type = type;
		}
	}

	public static void copy (Array<File> paths, Array<String> classPathFiles, String assetsOutputPath, boolean generateTextFile) {
		assetsOutputPath = assetsOutputPath.replace("\\", "/");
		FileWrapper target = new FileWrapper(assetsOutputPath);
		ArrayList<Asset> assets = new ArrayList<Asset>();
		DefaultAssetFilter defaultAssetFilter = new DefaultAssetFilter();
		for (int i = 0; i < paths.size; i++) {
			String path = paths.get(i).getAbsolutePath();
			FileWrapper source = new FileWrapper(path);
			System.out.println("Copying Assets from:");
			System.out.println(path);
			System.out.println("to:");
			System.out.println(assetsOutputPath);
			copyDirectory(source, target, defaultAssetFilter, assets);
		}

		addDirectoryClassPathFiles(classPathFiles);

		for (String classpathFile : classPathFiles) {
			if(classpathFile.startsWith("/") == false)
				classpathFile = "/" + classpathFile;
			if (defaultAssetFilter.accept(classpathFile, false)) {
				try {
					System.out.println("Copying classpath asset:");
					System.out.println(classpathFile);
					InputStream is = AssetsCopy.class.getClassLoader().getResourceAsStream(classpathFile);
					FileWrapper dest = target.child(classpathFile);
					dest.write(is, false);
					assets.add(new Asset(dest, defaultAssetFilter.getType(dest.path())));
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

	private static void addDirectoryClassPathFiles(Array<String> classPathFiles) {
		Array<String> folderFilePaths = new Array<>();
		for (int k = 0; k < classPathFiles.size; k++) {
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
							classPathFiles.removeIndex(k);
							k--;
						}
						continue;
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
				classPathFiles.removeIndex(k);
				k--;
			}
		}
		classPathFiles.addAll(folderFilePaths);
	}

	private static void copyFile (FileWrapper source, FileWrapper dest, AssetFilter filter, ArrayList<Asset> assets) {
		if (!filter.accept(dest.path(), false)) return;
		try {
			assets.add(new Asset(dest, filter.getType(dest.path())));
			dest.write(source.read(), false);
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
