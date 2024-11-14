package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.examples.tests.FreetypeDemo;

public class FreetypeTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;

        config.preloadListener = assetLoader -> {
            assetLoader.loadScript("freetype.js", null);
        };

        new TeaApplication(new FreetypeDemo(), config);
    }
}