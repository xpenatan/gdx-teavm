package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import com.github.xpenatan.gdx.examples.tests.HelloTriangle;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

@SkipClass
public class BuildHelloTriangle {

    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();
        teaBuildConfiguration.obfuscate = false;
        teaBuildConfiguration.setApplicationListener(HelloTriangle.class);
        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        TeaBuilder.build(tool);
    }
}
