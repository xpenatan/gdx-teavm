package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.examples.tests.GearsDemo;
import com.github.xpenatan.gdx.examples.tests.ReadPixelsTest;

public class GearsTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
//        new TeaApplication(new GearsDemo(), config);
        new TeaApplication(new ReadPixelsTest(), config);
    }
}