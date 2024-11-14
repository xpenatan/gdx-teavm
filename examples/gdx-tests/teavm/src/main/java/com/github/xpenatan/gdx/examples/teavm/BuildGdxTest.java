package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

@SkipClass
public class BuildGdxTest {

    public static void main(String[] args) throws IOException {
        String reflectionPackage = "com.badlogic.gdx.math";
        TeaReflectionSupplier.addReflectionClass(reflectionPackage);

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        String gdxAssetsPath = args[0];
        System.out.println("gdxAssetsPath: " + gdxAssetsPath);
        teaBuildConfiguration.assetsPath.add(new AssetFileHandle(gdxAssetsPath));
        teaBuildConfiguration.assetsPath.add(new AssetFileHandle("../../core/assets"));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setObfuscated(false);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
        tool.setMainClass(GdxTestLauncher.class.getName());
        TeaBuilder.build(tool);
    }
}
