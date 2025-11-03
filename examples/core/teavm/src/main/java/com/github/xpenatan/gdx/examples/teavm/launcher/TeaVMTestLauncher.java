package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.examples.tests.AudioTest;

public class TeaVMTestLauncher {

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
        new TeaApplication(new AudioTest(), config);
    }
}