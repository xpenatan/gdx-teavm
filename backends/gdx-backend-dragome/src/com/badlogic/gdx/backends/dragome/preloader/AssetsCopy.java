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

package com.badlogic.gdx.backends.dragome.preloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
			System.out.println("Copying Assets from " + path + " to " + assetsOutputPath);
			copyDirectory(source, target, defaultAssetFilter, assets);
		}

		Array<String> folderFilePaths = new Array<>();
		for (int k = 0; k < classPathFiles.size; k++) {
			String classpathFile = classPathFiles.get(k);
			try {
				URL url = AssetsCopy.class.getResource(classpathFile);
				if (url != null) {
					classPathFiles.removeIndex(k);
					k--;
					File dir;
					dir = new File(url.toURI());
					for (File nextFile : dir.listFiles()) {
						boolean file = nextFile.isFile();
						String path = nextFile.getPath();
						if(file == false || path.contains(".class") || path.contains(".java"))
							continue;
						path = path.replace("/", "\\");
						classpathFile = classpathFile.replace("/", "\\");
						int i = path.lastIndexOf(classpathFile);
						String substring = path.substring(i+1);
						substring = substring.replace("\\", "/");
						folderFilePaths.add(substring);
					}
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		classPathFiles.addAll(folderFilePaths);

		for (String classpathFile : classPathFiles) {
			if (defaultAssetFilter.accept(classpathFile, false)) {
				try {
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
