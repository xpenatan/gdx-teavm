package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.examples.tests.reflection.ReflectionTest;
import java.io.File;

public class BuildReflectionTest {

    public static void main(String[] args) {
        String reflectionPackage = "com.github.xpenatan.gdx.examples.tests.reflection.models";
        TeaReflectionSupplier.addReflectionClass(reflectionPackage);

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File(".").getAbsolutePath();
        teaBuildConfiguration.obfuscate = false;
        teaBuildConfiguration.logClasses = false;
		teaBuildConfiguration.applicationListenerClass = ReflectionTest.class;
        TeaBuilder.build(teaBuildConfiguration);
    }
}
