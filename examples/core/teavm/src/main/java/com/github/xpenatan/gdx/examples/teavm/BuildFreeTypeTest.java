package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.examples.tests.freetype.FreeTypePackTest;
import java.io.File;
import org.teavm.tooling.TeaVMTool;

public class BuildFreeTypeTest {

    public static void main(String[] args) {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File(".").getAbsolutePath();
        teaBuildConfiguration.obfuscate = false;
        teaBuildConfiguration.logClasses = false;
//		teaBuildConfiguration.mainApplicationClass = FreeTypeTest.class;
//		teaBuildConfiguration.mainApplicationClass = FreeTypeAtlasTest.class;
//		teaBuildConfiguration.mainApplicationClass = FreeTypeMetricsTest.class;
        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        TeaBuilder.build(tool);
    }
}
