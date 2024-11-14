package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.imgui.example.tests.imgui.ImGuiGame;
import com.github.xpenatan.imgui.example.tests.wrapper.TeaVMTestWrapper;

public class GdxTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = false;
        config.useGL30 = true;

        config.preloadListener = assetLoader -> {
            assetLoader.loadScript("freetype.js", null);
        };

        new TeaApplication(new ImGuiGame(), config);
//        new TeaApplication(new TeaVMTestWrapper(), config);
    }
}
