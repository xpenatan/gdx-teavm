package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class HyperLap2DTest {

    public static void main(String[] args) {
        URL appJarAppUrl = null;
        try {
            appJarAppUrl = new File("/home/kalculon/Games/HyperRunner2/lwjgl3/build/libs/HyperRunner-0.0.1.jar").toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
        teaBuildConfiguration.assetsPath.add(new File("/home/kalculon/Games/HyperRunner2/assets"));
        teaBuildConfiguration.webappPath = new File("/opt/lampp/htdocs/teavm").getAbsolutePath();
        teaBuildConfiguration.obfuscate = false;
        teaBuildConfiguration.mainApplicationClass = "games.rednblack.hyperrunner.HyperRunner";
        teaBuildConfiguration.additionalClasspath.add(appJarAppUrl);

        teaBuildConfiguration.reflectionInclude.add("games.rednblack.editor.renderer");
        teaBuildConfiguration.reflectionInclude.add("games.rednblack.h2d.extension.spine");
        teaBuildConfiguration.reflectionInclude.add("games.rednblack.h2d.extension.talos");
        teaBuildConfiguration.reflectionInclude.add("games.rednblack.hyperrunner");
        teaBuildConfiguration.reflectionInclude.add("com.artemis");
        teaBuildConfiguration.reflectionInclude.add("com.talosvfx.talos.runtime");

        teaBuildConfiguration.reflectionExclude.add("com.artemis.annotation");
        teaBuildConfiguration.reflectionExclude.add("com.artemis.utils.reflect");
        TeaBuilder.build(teaBuildConfiguration);
    }
}
