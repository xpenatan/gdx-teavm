package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.badlogic.gdx.tests.SoundTest;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;

public class GdxTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;

//        new TeaApplication(new ImGuiTestsApp(), config);
//        new TeaApplication(new TeaVMTestWrapper(), config);
        new TeaApplication(new SoundTest(), config);
    }
}
