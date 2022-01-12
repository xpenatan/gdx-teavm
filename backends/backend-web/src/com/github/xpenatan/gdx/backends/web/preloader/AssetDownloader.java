package com.github.xpenatan.gdx.backends.web.preloader;

import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;

/**
 * @author xpenatan
 */
public class AssetDownloader
{
	private static AssetDownload instance;

	private AssetDownloader(){}

	public static AssetDownload getInstance() {
		return AssetDownloader.instance;
	}

	public static void setInstance(AssetDownload instance) {
		AssetDownloader.instance = instance;
	}


	static public interface AssetDownload {
		public void load(boolean async, final String url, AssetType type, String mimeType, AssetLoaderListener<?> listener);
		public void loadText(boolean async, final String url, final AssetLoaderListener<String> listener);
		public void loadScript(boolean async, final String url, final AssetLoaderListener<Object> listener);
		public boolean isUseBrowserCache();
		public String getHostPageBaseURL();
		public int getQueue();
		public void subtractQueue();
	}

}
