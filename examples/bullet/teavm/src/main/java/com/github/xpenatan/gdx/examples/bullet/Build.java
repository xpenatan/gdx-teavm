package com.github.xpenatan.gdx.examples.bullet;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

public class Build {

    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File(".").getCanonicalPath();
        teaBuildConfiguration.obfuscate = false;
        teaBuildConfiguration.setApplicationListener( BulletTest.class);
        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        TeaBuilder.build(tool);
    }
}
