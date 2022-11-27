package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.WebApplication;
import com.github.xpenatan.gdx.backends.web.WebJSApplication;
import com.github.xpenatan.gdx.backends.web.preloader.Blob;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

public class TeaJSApplication implements WebJSApplication {

    @Override
    public void initBulletPhysics(WebApplication application) {
        Preloader preloader = application.getPreloader();
        initBulletPhysicsWasm(preloader);
    }

    @Override
    public void initBox2dPhysics(WebApplication application) {
        Preloader preloader = application.getPreloader();
        initBox2DPhysicsWasm(preloader);
    }

    private void initBulletPhysicsWasm(Preloader preloader) {
        preloader.loadScript(false, "bullet.wasm.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }

    // Box2D

    private void initBox2DPhysicsWasm(Preloader preloader) {
        preloader.loadBinaryAsset(false, "box2D.wasm.wasm", new AssetLoaderListener<Blob>() {
            @Override
            public boolean onSuccess(String url, Blob result) {
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }
}
