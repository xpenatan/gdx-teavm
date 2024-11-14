package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoader;

@Emulate(SharedLibraryLoader.class)
public class SharedLibraryLoaderEmu {

    public void load (String libraryName) {
        TeaApplication app = (TeaApplication)Gdx.app;
        AssetLoader assetLoader = AssetLoader.getInstance();
        assetLoader.loadScript(false, libraryName + ".js", new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, String result) {
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }
}
