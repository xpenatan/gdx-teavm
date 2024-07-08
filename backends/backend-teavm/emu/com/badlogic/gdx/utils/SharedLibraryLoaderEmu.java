package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;

@Emulate(SharedLibraryLoader.class)
public class SharedLibraryLoaderEmu {

    public void load (String libraryName) {
        TeaApplication app = (TeaApplication)Gdx.app;
        Preloader preloader = app.getPreloader();
        preloader.loadScript(false, libraryName + ".js", new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, String result) {
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }
}
