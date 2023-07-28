package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import com.github.xpenatan.gdx.examples.teavm.launcher.LoadingTestLauncher;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMTool;

@SkipClass
public class BuildLoadingTest {

    public static void main(String[] args) throws IOException {
        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("../desktop/assets"));
        teaBuildConfiguration.webappPath = new File("build/dist").getCanonicalPath();

        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration);
        tool.setObfuscated(false);
        tool.setMainClass(LoadingTestLauncher.class.getName());
        TeaBuilder.build(tool);
    }
}
