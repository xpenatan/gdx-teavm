package com.github.xpenatan.gdx.backends.teavm.assetloader;

/**
 *
 * AssetDownloader is a low level api to download any file. The full asset path should be passed.
 * Queue counter is used when download starts and finish with error or success.
 *
 * @author xpenatan
 */
public interface AssetDownloader {
    void load(boolean async, final String url, AssetType type, AssetLoaderListener<Blob> listener);
    void loadScript(boolean async, final String url, final AssetLoaderListener<String> listener);
    int getQueue();
    void subtractQueue();
    void addQueue();
}