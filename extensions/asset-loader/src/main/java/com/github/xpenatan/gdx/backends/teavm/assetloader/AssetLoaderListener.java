package com.github.xpenatan.gdx.backends.teavm.assetloader;

/**
 * @author xpenatan
 */
public class AssetLoaderListener<T> {
    public void onProgress(double amount) {
    }

    public void onFailure(String url) {
    }

    public void onSuccess(String url, T result) {
    }
}