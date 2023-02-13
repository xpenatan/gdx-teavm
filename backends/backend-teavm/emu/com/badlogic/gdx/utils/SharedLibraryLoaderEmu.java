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
        preloader.loadScript(false, libraryName + ".js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }
}
