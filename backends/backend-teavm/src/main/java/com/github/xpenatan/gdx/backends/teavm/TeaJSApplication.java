package com.github.xpenatan.gdx.backends.teavm;

import com.github.xpenatan.gdx.backends.web.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.web.WebApplication;
import com.github.xpenatan.gdx.backends.web.WebJSApplication;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.web.preloader.AssetDownloader;
import com.github.xpenatan.gdx.backends.web.preloader.Blob;
import com.github.xpenatan.gdx.backends.web.preloader.Preloader;

public class TeaJSApplication implements WebJSApplication {
    boolean bulletScriptLoaded = false;
    boolean box2DScriptLoaded = false;

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
                bulletScriptLoaded = true;
                return true;
            }

            @Override
            public void onFailure(String url) {
                // Fall back and try to load normal bullet physics
                initBulletPhysics(preloader);
            }
        });

        if(bulletScriptLoaded) {
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

    @org.teavm.jso.JSBody(params = {"bulletFunction"}, script = "BulletLib().then(function(BulletLib){ window.Bullet = BulletLib; bulletFunction(); });")
    private static native void initBullet(BulletPhysicsLoadFunction bulletFunction);

    @org.teavm.jso.JSBody(params = {"response", "bulletFunction"}, script = "BulletLib({ wasmBinary: response, async: false }).then(function(BulletLib){ window.Bullet = BulletLib; bulletFunction(); });")
    private static native void initBulletWasm(ArrayBufferWrapper response, BulletPhysicsLoadFunction bulletFunction);

    @org.teavm.jso.JSFunctor
    public interface BulletPhysicsLoadFunction extends org.teavm.jso.JSObject {
        void onBulletPhysicsLoaded();
    }

    // Box2D

    private void initBox2DPhysics(Preloader preloader) {
        preloader.loadScript(false, "scripts/box2D.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                AssetDownloader.getInstance().addQueue();
                initBox2D(new Box2DPhysicsLoadFunction() {
                    @Override
                    public void onBox2DPhysicsLoaded() {
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

    private void initBox2DPhysicsWasm(Preloader preloader) {
        preloader.loadScript(false, "scripts/box2D.wasm.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                box2DScriptLoaded = true;
                return true;
            }

            @Override
            public void onFailure(String url) {
                // Fall back and try to load normal Box2D physics
                initBox2DPhysics(preloader);
            }
        });

        if(box2DScriptLoaded) {
            preloader.loadBinaryAsset(true, "scripts/box2D.wasm.wasm", new AssetLoaderListener<Blob>() {
                @Override
                public boolean onSuccess(String url, Blob result) {
                    ArrayBufferWrapper response = result.getResponse();
                    AssetDownloader.getInstance().addQueue();
                    initBox2DWasm(response, new Box2DPhysicsLoadFunction() {
                        @Override
                        public void onBox2DPhysicsLoaded() {
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

    @org.teavm.jso.JSBody(params = {"box2DFunction"}, script = "Box2DLib().then(function(Box2DLib){ window.Box2D = Box2DLib; box2DFunction(); });")
    private static native void initBox2D(Box2DPhysicsLoadFunction box2DFunction);

    @org.teavm.jso.JSBody(params = {"response", "box2DFunction"}, script = "Box2DLib({ wasmBinary: response, async: false }).then(function(Box2DLib){ window.Box2D = Box2DLib; box2DFunction(); });")
    private static native void initBox2DWasm(ArrayBufferWrapper response, Box2DPhysicsLoadFunction box2DFunction);

    @org.teavm.jso.JSFunctor
    public interface Box2DPhysicsLoadFunction extends org.teavm.jso.JSObject {
        void onBox2DPhysicsLoaded();
    }
}
