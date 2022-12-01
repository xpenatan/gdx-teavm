package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.examples.teavm.launcher.UITestLauncher;
import java.io.File;
import org.teavm.tooling.TeaVMTool;

public class BuildUITest {

    public static void main(String[] args) {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File(".").getAbsolutePath();
        teaBuildConfiguration.obfuscate = false;

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setMainClass(UITestLauncher.class.getName());
        TeaBuilder.build(tool);
    }
}
