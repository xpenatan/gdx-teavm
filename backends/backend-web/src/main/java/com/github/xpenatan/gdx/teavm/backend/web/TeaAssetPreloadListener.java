package com.github.xpenatan.gdx.teavm.backend.web;

import com.github.xpenatan.gdx.teavm.backend.web.assetloader.AssetLoader;

public interface TeaAssetPreloadListener {
    void onPreload(AssetLoader assetLoader);
}
