package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.web.WebBuildConfiguration;

import java.io.File;
import java.net.URL;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration extends WebBuildConfiguration {

    public Array<File> assetsPath = new Array<>();

    public boolean obfuscate = false;
    public String mainApplicationClass;
    public String webappPath;
    public final Array<URL> additionalClasspath = new Array<>();

    @Override
    public String getMainClass() {
        return TeaLauncher.class.getName();
    }

    @Override
    public String getApplicationListenerClass() {
        return mainApplicationClass;
    }

    @Override
    public Array<URL> getAdditionalClasspath() {
        return additionalClasspath;
    }

    @Override
    public String getWebAppPath() {
        return webappPath;
    }

    @Override
    public boolean assetsPath(Array<File> paths) {
        paths.addAll(assetsPath);
        return true;
    }

    @Override
    public boolean minifying() {
        return obfuscate;
    }

    @Override
    public void assetsClasspath(Array<String> classPaths) {
    }
}
