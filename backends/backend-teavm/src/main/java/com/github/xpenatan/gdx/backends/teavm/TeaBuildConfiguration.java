package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backend.web.WebBuildConfiguration;

import java.io.File;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration extends WebBuildConfiguration {

    public Array<File> assetsPath = new Array<>();

    public boolean obfuscate = false;
    public Class<?> mainClass;
    public String webappPath;

    @Override
    public Class getMainClass() {
        return mainClass;
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
