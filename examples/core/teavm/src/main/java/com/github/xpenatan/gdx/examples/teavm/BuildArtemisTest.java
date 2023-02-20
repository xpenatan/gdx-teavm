package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import com.github.xpenatan.gdx.examples.tests.ArtemisTest;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

@SkipClass
public class BuildArtemisTest {

    public static void main(String[] args) throws IOException {
        String reflectionPackage = "com.badlogic.gdx.math";
        TeaReflectionSupplier.addReflectionClass(reflectionPackage);
        TeaReflectionSupplier.addReflectionClass("com.github.xpenatan.gdx.examples.tests");

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();
        teaBuildConfiguration.setApplicationListener(ArtemisTest.class);
        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setObfuscated(false);
        TeaBuilder.build(tool);
    }
}