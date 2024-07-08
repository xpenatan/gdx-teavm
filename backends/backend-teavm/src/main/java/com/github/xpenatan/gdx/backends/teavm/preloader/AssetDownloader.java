package com.github.xpenatan.gdx.backends.teavm.preloader;

import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;

/**
 * @author xpenatan
 */
public class AssetDownloader {
    private static AssetDownload instance;

    private AssetDownloader() {
    }

    public static AssetDownload getInstance() {
        return AssetDownloader.instance;
    }

    public static void setInstance(AssetDownload instance) {
        AssetDownloader.instance = instance;
    }

    public interface AssetDownload {

        void load(boolean async, final String url, AssetType type, AssetLoaderListener<?> listener);

        void loadScript(boolean async, final String url, final AssetLoaderListener<Object> listener);

        String getHostPageBaseURL();

        int getQueue();

        void subtractQueue();

        void addQueue();
    }
}