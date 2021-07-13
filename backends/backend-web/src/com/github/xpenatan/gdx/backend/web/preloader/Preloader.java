package com.github.xpenatan.gdx.backend.web.preloader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.github.xpenatan.gdx.backend.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backend.web.WebFileHandle;
import com.github.xpenatan.gdx.backend.web.dom.HTMLImageElementWrapper;

/**
 * @author xpenatan
 */
public class Preloader {
	public ObjectMap<String, Void> directories = new ObjectMap<String, Void>();
	public ObjectMap<String, HTMLImageElementWrapper> images = new ObjectMap<String, HTMLImageElementWrapper>();
	public ObjectMap<String, Blob> audio = new ObjectMap<String, Blob>();
	public ObjectMap<String, String> texts = new ObjectMap<String, String>();
	public ObjectMap<String, Blob> binaries = new ObjectMap<String, Blob>();

	public static class Asset {
		public Asset (String url, AssetType type, long size, String mimeType) {
			this.url = url;
			this.type = type;
			this.size = size;
			this.mimeType = mimeType;
		}

		public boolean succeed;
		public boolean failed;
		public long loaded;
		public final String url;
		public final AssetType type;
		public final long size;
		public final String mimeType;
	}

	public final String baseUrl;

	public Preloader (String newBaseURL) {
		baseUrl = newBaseURL;
	}

	public void preload (final String assetFileUrl) {
		AssetDownloader.getInstance().loadText(baseUrl + assetFileUrl, new AssetLoaderListener<String>() {
			@Override
			public void onProgress (double amount) {
			}

			@Override
			public void onFailure (String url) {
				System.out.println("ErrorLoading: " + assetFileUrl);
			}

			@Override
			public boolean onSuccess (String url, String result) {
				String[] lines = result.split("\n");
				Array<Asset> assets = new Array<Asset>(lines.length);
				for (String line : lines) {
					String[] tokens = line.split(":");
					if (tokens.length != 4) {
						throw new GdxRuntimeException("Invalid assets description file.");
					}
					AssetType type = AssetType.Text;
					if (tokens[0].equals("i")) type = AssetType.Image;
					if (tokens[0].equals("b")) type = AssetType.Binary;
					if (tokens[0].equals("a")) type = AssetType.Audio;
					if (tokens[0].equals("d")) type = AssetType.Directory;
					long size = Long.parseLong(tokens[2]);
					if (type == AssetType.Audio && !AssetDownloader.getInstance().isUseBrowserCache()) {
						size = 0;
					}
					assets.add(new Asset(tokens[1].trim(), type, size, tokens[3]));
				}

				for (int i = 0; i < assets.size; i++) {
					final Asset asset = assets.get(i);

					if (contains(asset.url)) {
						asset.loaded = asset.size;
						asset.succeed = true;
						continue;
					}

					loadAsset(asset.url, asset.type, asset.mimeType, new AssetLoaderListener<Object>() {

						@Override
						public void onProgress (double amount) {
						}

						@Override
						public void onFailure (String url) {
						}

						@Override
						public boolean onSuccess (String url, Object result) {
							return false;
						}
					});
				}
				return false;
			}
		});
	}

	public void loadAsset (final String url, final AssetType type, final String mimeType, final AssetLoaderListener<Object> listener) {
		AssetDownloader.getInstance().load(baseUrl + url, type, mimeType, new AssetLoaderListener<Object>() {
			@Override
			public void onProgress (double amount) {
				listener.onProgress(amount);
			}

			@Override
			public void onFailure (String urll) {
				listener.onFailure(urll);
			}

			@Override
			public boolean onSuccess (String urll, Object result) {
				putAssetInMap(type, url, result);
				listener.onSuccess(urll, result);
				return false;
			}
		});
	}

	public void loadScript(final String url,  final AssetLoaderListener<Object> listener) {
		AssetDownloader.getInstance().loadScript(baseUrl + url, listener);
	}

	protected void putAssetInMap(AssetType type, String url, Object result) {
		switch (type) {
			case Text:
				texts.put(url, (String) result);
				break;
			case Image:
				images.put(url, (HTMLImageElementWrapper) result);
				break;
			case Binary:
				binaries.put(url, (Blob) result);
				break;
			case Audio:
				audio.put(url, (Blob) result);
				break;
			case Directory:
				directories.put(url, null);
				break;
		}
	}

	public InputStream read (String url) {
		if (texts.containsKey(url)) {
			try {
				return new ByteArrayInputStream(texts.get(url).getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		if (images.containsKey(url)) {
			return new ByteArrayInputStream(new byte[1]); // FIXME, sensible?
		}
		if (binaries.containsKey(url)) {
			return binaries.get(url).read();
		}
		if (audio.containsKey(url)) {
			return new ByteArrayInputStream(new byte[1]); // FIXME, sensible?
		}
		return null;
	}

	public boolean contains (String url) {
		return texts.containsKey(url) || images.containsKey(url) || binaries.containsKey(url) || audio.containsKey(url)
			|| directories.containsKey(url);
	}

	public boolean isText (String url) {
		return texts.containsKey(url);
	}

	public boolean isImage (String url) {
		return images.containsKey(url);
	}

	public boolean isBinary (String url) {
		return binaries.containsKey(url);
	}

	public boolean isAudio (String url) {
		return audio.containsKey(url);
	}

	public boolean isDirectory (String url) {
		return directories.containsKey(url);
	}

	private boolean isChild (String path, String url) {
		return path.startsWith(url) && (path.indexOf('/', url.length() + 1) < 0);
	}

	public FileHandle[] list (String url) {
		Array<FileHandle> files = new Array<FileHandle>();
		for (String path : texts.keys()) {
			if (isChild(path, url)) {
				files.add(new WebFileHandle(this, path, FileType.Internal));
			}
		}
		FileHandle[] list = new FileHandle[files.size];
		System.arraycopy(files.items, 0, list, 0, list.length);
		return list;
	}

	public FileHandle[] list (String url, FileFilter filter) {
		Array<FileHandle> files = new Array<FileHandle>();
		for (String path : texts.keys()) {
			if (isChild(path, url) && filter.accept(new File(path))) {
				files.add(new WebFileHandle(this, path, FileType.Internal));
			}
		}
		FileHandle[] list = new FileHandle[files.size];
		System.arraycopy(files.items, 0, list, 0, list.length);
		return list;
	}

	public FileHandle[] list (String url, FilenameFilter filter) {
		Array<FileHandle> files = new Array<FileHandle>();
		for (String path : texts.keys()) {
			if (isChild(path, url) && filter.accept(new File(url), path.substring(url.length() + 1))) {
				files.add(new WebFileHandle(this, path, FileType.Internal));
			}
		}
		FileHandle[] list = new FileHandle[files.size];
		System.arraycopy(files.items, 0, list, 0, list.length);
		return list;
	}

	public FileHandle[] list (String url, String suffix) {
		Array<FileHandle> files = new Array<FileHandle>();
		for (String path : texts.keys()) {
			if (isChild(path, url) && path.endsWith(suffix)) {
				files.add(new WebFileHandle(this, path, FileType.Internal));
			}
		}
		FileHandle[] list = new FileHandle[files.size];
		System.arraycopy(files.items, 0, list, 0, list.length);
		return list;
	}

	public long length (String url) {
		if (texts.containsKey(url)) {
			try {
				return texts.get(url).getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				return texts.get(url).getBytes().length;
			}
		}
		if (images.containsKey(url)) {
			return 1; // FIXME, sensible?
		}
		if (binaries.containsKey(url)) {
			return binaries.get(url).length();
		}
		if (audio.containsKey(url)) {
			return 1; // FIXME sensible?
		}
		return 0;
	}

	public void printLoadedAssets() {
		Entries<String,HTMLImageElementWrapper> iterator = images.iterator();
		System.out.println("### Text Assets: ");
		printKeys(texts);
		System.out.println("##########");
		System.out.println("### Image Assets: ");
		printKeys(images);
		System.out.println("##########");
	}


	private void printKeys(ObjectMap<String, ?> map) {
		Entries<String, ?> iterator = map.iterator();

		while(iterator.hasNext) {
			Entry<String, ?> next = iterator.next();
			String key = next.key;
			System.out.println("Key: " + key);
		}
	}

}
