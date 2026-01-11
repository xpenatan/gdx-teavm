package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaCompiler;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) throws IOException {
        AssetFileHandle assetsPath = new AssetFileHandle("../desktop/assets");
        TeaGLFWBackend cBackend = new TeaGLFWBackend();
//        cBackend.shouldGenerateSource = false;
        new TeaCompiler()
                .addAssets(assetsPath)
                .setBackend(cBackend)
                .setObfuscated(false)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestCLauncher.class.getName())
                .setReleasePath(new File(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "test app"))
                .build(new File("build/dist"));
    }
}
