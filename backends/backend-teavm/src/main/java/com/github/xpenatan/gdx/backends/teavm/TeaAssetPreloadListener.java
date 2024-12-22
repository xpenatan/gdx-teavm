package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoader;

public interface TeaAssetPreloadListener {
    void onPreload(AssetLoader assetLoader);
}
