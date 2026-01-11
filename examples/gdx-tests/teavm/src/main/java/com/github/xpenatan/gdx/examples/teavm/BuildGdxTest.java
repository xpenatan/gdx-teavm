package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.backend.TeaWebAssemblyBackend;
import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaCompiler;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildGdxTest {

    public static void main(String[] args) throws IOException {
        String gdxAssetsPath = args[0];
        System.out.println("gdxAssetsPath: " + gdxAssetsPath);

        new TeaCompiler()
                .addAssets(new AssetFileHandle(gdxAssetsPath))
                .addAssets(new AssetFileHandle("../../basic/assets"))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(GdxTestLauncher.class.getName())
                .setObfuscated(false)
                .setBackend(new TeaWebAssemblyBackend())
                .build(new File("build/dist"));
    }
}
