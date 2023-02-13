package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.ApplicationListener;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetFilter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration {

    public AssetFilter assetFilter = null;
    public ArrayList<File> assetsPath = new ArrayList<>();
    public ArrayList<String> additionalAssetsClasspathFiles = new ArrayList<>();

    public boolean obfuscate = false;
    private String mainApplicationClass;

    public String webappPath;
    public final ArrayList<URL> additionalClasspath = new ArrayList<>();
    public final ArrayList<String> reflectionInclude = new ArrayList<>();
    public final ArrayList<String> reflectionExclude = new ArrayList<>();
    public final ArrayList<String> classesToPreserve = new ArrayList<>();

    /**
     * Array of class.getName() to skip
     */
    public final ArrayList<String> classesToSkip = new ArrayList<>();

    public String mainClass = TeaLauncher.class.getName();
    public String mainClassArgs = "";

    public String htmlTitle = "TeaVMCoreExample";
    public int htmlWidth = 800;
    public int htmlHeight = 600;

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public String getHtmlWidth() {
        return String.valueOf(htmlWidth);
    }

    public String getHtmlHeight() {
        return String.valueOf(htmlHeight);
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getMainClassArgs() {
        return mainClassArgs;
    }

    public String getApplicationListenerClass() {
        return mainApplicationClass;
    }

    public ArrayList<URL> getAdditionalClasspath() {
        return additionalClasspath;
    }

    public ArrayList<String> getAdditionalAssetClasspath() {
        return additionalAssetsClasspathFiles;
    }

    public String getWebAppPath() {
        return webappPath;
    }

    public boolean assetsPath(ArrayList<File> paths) {
        paths.addAll(assetsPath);
        return true;
    }

    public AssetFilter assetFilter() {
        return assetFilter;
    }

    public boolean minifying() {
        return obfuscate;
    }

    public ArrayList<String> getReflectionInclude() {
        return reflectionInclude;
    }

    public ArrayList<String> getReflectionExclude() {
        return reflectionExclude;
    }

    public ArrayList<String> getClassesToPreserve() {
        return classesToPreserve;
    }

    public ArrayList<String> getSkipClasses() {
        return classesToSkip;
    }

    public boolean logClasses() {
        return false;
    }

    public void assetsClasspath(ArrayList<String> classPaths) {
    }

    public void setApplicationListener(Class<? extends ApplicationListener> applicationListener) {
        setApplicationListener(applicationListener.getName());
    }

    public void setApplicationListener(String applicationListener) {
        mainApplicationClass = applicationListener;
    }

    public boolean acceptClasspath(URL url) {
        return true;
    }
}
