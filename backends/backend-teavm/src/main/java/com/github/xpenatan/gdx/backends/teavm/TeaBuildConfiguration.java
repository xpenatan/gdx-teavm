package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.ApplicationListener;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetFilter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration extends WebBuildConfiguration {

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

    @Override
    public String getHtmlTitle() {
      return htmlTitle;
    }

  @Override
    public String getHtmlWidth() {
        return String.valueOf(htmlWidth);
    }

    @Override
    public String getHtmlHeight() {
        return String.valueOf(htmlHeight);
    }

    @Override
    public String getMainClass() {
        return mainClass;
    }

    @Override
    public String getMainClassArgs() {
        return mainClassArgs;
    }

    @Override
    public String getApplicationListenerClass() {
        return mainApplicationClass;
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
    public AssetFilter assetFilter() {
      return assetFilter;
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
    public ArrayList<String> getSkipClasses() {
        return classesToSkip;
    }

    @Override
    public boolean logClasses() {
        return false;
    }

    @Override
    public void assetsClasspath(ArrayList<String> classPaths) {
    }

    public void setApplicationListener(Class<? extends ApplicationListener> applicationListener) {
        setApplicationListener(applicationListener.getName());
    }

    public void setApplicationListener(String applicationListener) {
        mainApplicationClass = applicationListener;
    }
}
