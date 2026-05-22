package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class TeaVMPluginClasspathTest {

    @Test
    public void getURLs_usesConfiguredClasspathBeforeClassLoaderFallback() throws Exception {
        List<String> configuredClasspath = Arrays.asList(
                "E:/deps/gdx-freetype-teavm.jar",
                "E:/classes/main"
        );

        URL ignoredUrl = new File("E:/deps/ignored.jar").toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[] { ignoredUrl });

        Assert.assertEquals(
                Arrays.asList(
                        new File(configuredClasspath.get(0)).toURI().toURL(),
                        new File(configuredClasspath.get(1)).toURI().toURL()
                ),
                TeaVMPluginClasspath.getURLs(classLoader, configuredClasspath)
        );
    }
}
