package com.github.xpenatan.gdx.teavm.example.basic.teavm;

import com.github.xpenatan.gdx.teavm.backend.web.TeaApplication;
import com.github.xpenatan.gdx.teavm.backend.web.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.example.basic.tests.GearsDemo;

public class TestWebLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration();
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
//        new TeaApplication(new LoadingTest(), config);
//        new TeaApplication(new GLTFQuickStartExample(), config);
//        new TeaApplication(new ReflectionTest(), config);
//        new TeaApplication(new UITest(), config);

        // Audio is no longer supported in gdx-teavm. You can use this solution to keep using Howler.js
        new TeaApplication(new GearsDemo(), config);
    }
}