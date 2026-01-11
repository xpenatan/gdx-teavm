package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.backend.TeaJavaScriptBackend;
import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaCompiler;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) throws IOException {
        AssetFileHandle assetsPath = new AssetFileHandle("../desktop/assets");
        TeaJavaScriptBackend webBackend = new TeaJavaScriptBackend();
        new TeaCompiler()
                .addAssets(assetsPath)
                .setBackend(webBackend)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestWebLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
