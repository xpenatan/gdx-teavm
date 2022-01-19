package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.WebApplication;
import com.github.xpenatan.gdx.backends.web.WebJSApplication;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.web.preloader.AssetDownloader;
import com.github.xpenatan.gdx.backends.web.preloader.Blob;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

public class TeaJSApplication implements WebJSApplication {
    boolean scriptLoaded = false;

    @Override
    public void initBulletPhysics(WebApplication application) {
        Preloader preloader = application.getPreloader();
        initBulletPhysicsWasm(preloader);
    }

    private void initBulletPhysics(Preloader preloader) {
        preloader.loadScript(false, "scripts/bullet.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                AssetDownloader.getInstance().addQueue();
                initBullet(new BulletPhysicsLoadFunction() {
                    @Override
                    public void onBulletPhysicsLoaded() {
                        AssetDownloader.getInstance().subtractQueue();
                    }
                });
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }

    private void initBulletPhysicsWasm(Preloader preloader) {
        preloader.loadScript(false, "scripts/bullet.wasm.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                scriptLoaded = true;
                return true;
            }

            @Override
            public void onFailure(String url) {
                // Fall back and try to load normal bullet physics
                initBulletPhysics(preloader);
            }
        });

        if(scriptLoaded) {
            preloader.loadBinaryAsset(true, "scripts/bullet.wasm.wasm", new AssetLoaderListener<Blob>() {
                @Override
                public boolean onSuccess(String url, Blob result) {
                    ArrayBufferWrapper response = result.getResponse();
                    AssetDownloader.getInstance().addQueue();
                    initBulletWasm(response, new BulletPhysicsLoadFunction() {
                        @Override
                        public void onBulletPhysicsLoaded() {
                            AssetDownloader.getInstance().subtractQueue();
                        }
                    });
                    return true;
                }

                @Override
                public void onFailure(String url) {
                }
            });
        }
    }

    @org.teavm.jso.JSBody(params = { "bulletFunction" }, script = "BulletLib().then(function(BulletLib){ window.Bullet = BulletLib; bulletFunction(); });")
    private static native void initBullet(BulletPhysicsLoadFunction bulletFunction);

    @org.teavm.jso.JSBody(params = { "response", "bulletFunction" }, script = "BulletLib({ wasmBinary: response, async: false }).then(function(BulletLib){ window.Bullet = BulletLib; bulletFunction(); });")
    private static native void initBulletWasm(ArrayBufferWrapper response, BulletPhysicsLoadFunction bulletFunction);

    @org.teavm.jso.JSFunctor
    public interface BulletPhysicsLoadFunction extends org.teavm.jso.JSObject {
        void onBulletPhysicsLoaded();
    }
}
