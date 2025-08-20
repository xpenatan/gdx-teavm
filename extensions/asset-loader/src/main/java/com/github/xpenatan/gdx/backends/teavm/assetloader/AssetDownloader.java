package com.github.xpenatan.gdx.backends.teavm.assetloader;

/**
 * AssetDownloader is a low level api to download any file. The full asset path should be passed.
 *
 * @author xpenatan
 */
public interface AssetDownloader {
    void load(boolean async, final String url, AssetType type, AssetLoaderListener<TeaBlob> listener);
    void loadScript(boolean async, final String url, final AssetLoaderListener<String> listener);
}