package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import com.github.xpenatan.gdx.examples.teavm.launcher.GdxTestLauncher;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

@SkipClass
public class BuildGdxTest {

    public static void main(String[] args) throws IOException {
        String reflectionPackage = "com.badlogic.gdx.math";
        TeaReflectionSupplier.addReflectionClass(reflectionPackage);

        // Change to your source asset directory
        String libgdxTestAssetsPath = "E:\\Dev\\Projects\\java\\libgdx\\tests\\gdx-tests-android\\assets\\";

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File(libgdxTestAssetsPath));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setObfuscated(false);
        tool.setMainClass(GdxTestLauncher.class.getName());
        TeaBuilder.build(tool);
    }
}
