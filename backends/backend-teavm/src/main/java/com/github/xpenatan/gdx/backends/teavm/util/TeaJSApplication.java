package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;

@Deprecated
public class TeaJSApplication implements WebJSApplication {

    @Override
    public void initBulletPhysics(TeaApplication application) {
        Preloader preloader = application.getPreloader();
        initBulletPhysicsWasm(preloader);
    }

    @Override
    public void initBox2dPhysics(TeaApplication application) {
        Preloader preloader = application.getPreloader();
        initBox2DPhysicsWasm(preloader);
    }

    @Override
    public void initImGui(TeaApplication application) {
        //TODO script loading should be inside lib and not application
        Preloader preloader = application.getPreloader();
        initImGuiWasm(preloader);
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

    private void initImGuiWasm(Preloader preloader) {
        preloader.loadScript(false, "imgui.js", new AssetLoaderListener<Object>() {
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
        preloader.loadScript(false, "box2D.wasm.js", new AssetLoaderListener<Object>() {
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
