package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.BuildMode;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.examples.teavm.launcher.TeaVMTestLauncher;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) throws IOException {
        String reflectionPackage = "com.badlogic.gdx.math";
        TeaReflectionSupplier.addReflectionClass(reflectionPackage);

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new AssetFileHandle("../assets"));
        teaBuildConfiguration.shouldGenerateAssetFile = true;
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();
        teaBuildConfiguration.buildMode = BuildMode.WEBASSEMBLY;
        teaBuildConfiguration.obfuscated = true;

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setMainClass(TeaVMTestLauncher.class.getName());
        int size = 64 * (1 << 20);
        tool.setMaxDirectBuffersSize(size);
        TeaBuilder.build(tool);
    }
}
