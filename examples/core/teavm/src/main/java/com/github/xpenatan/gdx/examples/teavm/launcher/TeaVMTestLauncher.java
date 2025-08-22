package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.examples.tests.GLTFQuickStartExample;
import com.github.xpenatan.gdx.examples.tests.LoadingTest;
import com.github.xpenatan.gdx.examples.tests.ReflectionTest;
import com.github.xpenatan.gdx.examples.tests.UITest;

public class TeaVMTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration();
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
//        new TeaApplication(new LoadingTest(), config);
        new TeaApplication(new GLTFQuickStartExample(), config);
//        new TeaApplication(new ReflectionTest(), config);
//        new TeaApplication(new UITest(), config);
    }
}