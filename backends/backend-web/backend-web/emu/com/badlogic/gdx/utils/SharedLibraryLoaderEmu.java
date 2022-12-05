package com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.WebApplication;
import com.github.xpenatan.gdx.backends.web.gen.Emulate;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

@Emulate(SharedLibraryLoader.class)
public class SharedLibraryLoaderEmu {

    public void load (String libraryName) {
        WebApplication app = (WebApplication)Gdx.app;
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
