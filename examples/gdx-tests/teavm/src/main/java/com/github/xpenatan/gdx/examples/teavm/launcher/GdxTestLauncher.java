package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.badlogic.gdx.tests.FloatTextureTest;
import com.badlogic.gdx.tests.SoundTest;
import com.badlogic.gdx.tests.TextureDataTest;
import com.badlogic.gdx.tests.g3d.MultipleRenderTargetTest;
import com.badlogic.gdx.tests.g3d.TextureArrayTest;
import com.badlogic.gdx.tests.g3d.TextureRegion3DTest;
import com.badlogic.gdx.tests.gles3.GL30Texture3DTest;
import com.badlogic.gdx.tests.gles3.InstancedRenderingTest;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;

public class GdxTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
        config.useGLArrayBuffer = false;
//        new TeaApplication(new ImGuiTestsApp(), config);
//        new TeaApplication(new TeaVMTestWrapper(), config);
//        new TeaApplication(new SoundTest(), config);
//        new TeaApplication(new TextureDataTest(), config);
//        new TeaApplication(new FloatTextureTest(), config);
//        new TeaApplication(new TextureRegion3DTest(), config);
//        new TeaApplication(new GL30Texture3DTest(), config);
//        new TeaApplication(new InstancedRenderingTest(), config);
//        new TeaApplication(new TextureArrayTest(), config);
        new TeaApplication(new MultipleRenderTargetTest(), config);
    }
}
