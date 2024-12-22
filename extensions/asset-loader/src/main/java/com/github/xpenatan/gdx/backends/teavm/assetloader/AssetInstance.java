package com.github.xpenatan.gdx.backends.teavm.assetloader;

public class AssetInstance {

    static AssetDownloader downloaderInstance;
    static AssetLoader instance;

    public static void setInstance(AssetLoader instance) {
        AssetInstance.instance = instance;
    }

    public static void setInstance(AssetDownloader instance) {
        AssetInstance.downloaderInstance = instance;
    }

    public static AssetLoader getLoaderInstance() {
        return AssetInstance.instance;
    }

    public static AssetDownloader getDownloaderInstance() {
        return AssetInstance.downloaderInstance;
    }
}