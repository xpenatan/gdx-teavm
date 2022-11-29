package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.ApplicationListener;
import com.github.xpenatan.gdx.backends.web.WebBuildConfiguration;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration extends WebBuildConfiguration {

    public ArrayList<File> assetsPath = new ArrayList<>();
    public ArrayList<String> additionalAssetsClasspathFiles = new ArrayList<>();

    public boolean logClasses = false;
    public boolean obfuscate = false;
    public Class<? extends ApplicationListener> applicationListenerClass;
    public String webappPath;
    public final ArrayList<URL> additionalClasspath = new ArrayList<>();
    public final ArrayList<String> reflectionInclude = new ArrayList<>();
    public final ArrayList<String> reflectionExclude = new ArrayList<>();
    public final ArrayList<String> classesToPreserve = new ArrayList<>();

    @Override
    public String getMainClass() {
        return TeaLauncher.class.getName();
    }

    @Override
    public String getApplicationListenerClass() {
        return applicationListenerClass.getName();
    }

    @Override
    public ArrayList<URL> getAdditionalClasspath() {
        return additionalClasspath;
    }

    @Override
    public ArrayList<String> getAdditionalAssetClasspath() {
        return additionalAssetsClasspathFiles;
    }

    @Override
    public String getWebAppPath() {
        return webappPath;
    }

    @Override
    public boolean assetsPath(ArrayList<File> paths) {
        paths.addAll(assetsPath);
        return true;
    }

    @Override
    public boolean minifying() {
        return obfuscate;
    }

    @Override
    public ArrayList<String> getReflectionInclude() {
        return reflectionInclude;
    }

    @Override
    public ArrayList<String> getReflectionExclude() {
        return reflectionExclude;
    }

    @Override
    public ArrayList<String> getClassesToPreserve() {
        return classesToPreserve;
    }

    @Override
    public boolean logClasses() {
        return logClasses;
    }

    @Override
    public void assetsClasspath(ArrayList<String> classPaths) {
    }
}
