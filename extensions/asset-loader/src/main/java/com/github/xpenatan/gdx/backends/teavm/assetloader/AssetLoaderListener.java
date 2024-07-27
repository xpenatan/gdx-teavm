package com.github.xpenatan.gdx.backends.teavm.assetloader;

/**
 * @author xpenatan
 */
public interface AssetLoaderListener<T> {
    default void onProgress(int total, int loaded) {}

    default void onFailure(String url) {}

    default void onSuccess(String url, T result) {}
}