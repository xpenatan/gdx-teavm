package com.github.xpenatan.gdx.backends.dragome;

import com.github.xpenatan.gdx.backend.web.preloader.AssetDownloader;
import com.github.xpenatan.gdx.backend.web.utils.Storage;
import com.github.xpenatan.gdx.backends.dragome.preloader.AssetDownloaderImpl;
import com.github.xpenatan.gdx.backends.dragome.utils.StorageImpl;

public class PlatformInitializer {

	public static void init() {
		initAssetDownloader();
		initStorage();
	}

	public static void initAssetDownloader() {
		AssetDownloader.setInstance(new AssetDownloaderImpl());
	}

	public static void initStorage() {
		Storage.setInstance(new StorageImpl());
	}
}
