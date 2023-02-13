package com.github.xpenatan.gdx.backends.teavm;

/**
 * @author xpenatan
 */
public class AssetLoaderListener<T> {
    public void onProgress(double amount) {
    }

    public void onFailure(String url) {
    }

    public boolean onSuccess(String url, T result) {
        return false;
    }
}