package com.github.xpenatan.gdx.teavm.backends.web;

import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoader;

public interface TeaAssetPreloadListener {
    void onPreload(AssetLoader assetLoader);
}
