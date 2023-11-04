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
import com.github.xpenatan.imgui.example.tests.imgui.ImGuiGame;
import com.github.xpenatan.imgui.example.tests.wrapper.TeaVMTestWrapper;

public class GdxTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
        config.useGLArrayBuffer = true;
//        new TeaApplication(new ImGuiGame(), config);
        new TeaApplication(new TeaVMTestWrapper(), config);
    }
}
