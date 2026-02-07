package com.github.xpenatan.gdx.teavm.backends.web;

import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoader;

public interface WebAssetPreloadListener {
    void onPreload(AssetLoader assetLoader);
}
