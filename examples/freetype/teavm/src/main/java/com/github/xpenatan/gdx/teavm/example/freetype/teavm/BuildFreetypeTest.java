package com.github.xpenatan.gdx.teavm.example.freetype.teavm;

import com.github.xpenatan.gdx.teavm.backend.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backend.web.config.backend.TeaJavaScriptBackend;
import com.github.xpenatan.gdx.teavm.backend.shared.config.compiler.TeaCompiler;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildFreetypeTest {

    public static void main(String[] args) throws IOException {
        new TeaCompiler()
                .addAssets(new AssetFileHandle("../desktop/assets"))
                .setBackend(new TeaJavaScriptBackend())
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(FreetypeTestLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
