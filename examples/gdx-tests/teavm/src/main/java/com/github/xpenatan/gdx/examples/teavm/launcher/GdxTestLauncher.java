package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.badlogic.gdx.tests.SoundTest;
import com.badlogic.gdx.tests.gles3.GL30Texture3DTest;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;

public class GdxTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;

//        new TeaApplication(new ImGuiTestsApp(), config);
//        new TeaApplication(new TeaVMTestWrapper(), config);
//        new TeaApplication(new SoundTest(), config);
        new TeaApplication(new GL30Texture3DTest(), config);
    }
}
