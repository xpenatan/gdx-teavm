package com.github.xpenatan.jparser.loader;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.WebApplication;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

public class JParserLibraryLoader {

    public void load (String libraryName, String dependency) {
        System.out.println();
    }

    public void load (String libraryName) {
        WebApplication app = (WebApplication)Gdx.app;
        Preloader preloader = app.getPreloader();
        preloader.loadScript(false, "scripts/" + libraryName + ".js", new AssetLoaderListener<Object>() {
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
