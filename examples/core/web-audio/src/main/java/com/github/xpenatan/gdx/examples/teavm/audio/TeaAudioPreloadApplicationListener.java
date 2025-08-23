package com.github.xpenatan.gdx.examples.teavm.audio;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaPreloadApplicationListener;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoaderListener;

/**
 * To keep using Audio, You need to add howler.js to root of resources and add META-INF/gdx-teavm.properties.
 */
public class TeaAudioPreloadApplicationListener extends TeaPreloadApplicationListener {

    @Override
    protected void setupPreloadAssets() {
        super.setupPreloadAssets();
        addQueue();
        assetLoader.loadScript("howler.js", new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, String result) {
                subtractQueue();
                Gdx.audio = new DefaultTeaAudio();
            }
        });
    }
}