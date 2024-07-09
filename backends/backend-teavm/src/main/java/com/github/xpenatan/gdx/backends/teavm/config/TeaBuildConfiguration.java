package com.github.xpenatan.gdx.backends.teavm.config;

import com.badlogic.gdx.ApplicationListener;
import com.github.xpenatan.gdx.backends.teavm.TeaLauncher;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration {

    public AssetFilter assetFilter = null;
    public ArrayList<AssetFileHandle> assetsPath = new ArrayList<>();
    public ArrayList<String> additionalAssetsClasspathFiles = new ArrayList<>();
    public boolean shouldGenerateAssetFile = true;

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

    public String htmlTitle = "gdx-teavm";
    public int htmlWidth = 800;
    public int htmlHeight = 600;

    /** True to use the default html index. False will stop overwriting html file. */
    public boolean useDefaultHtmlIndex = true;

    /** If the logo is shown while the application is loading. Requires showLoadingLogo true. */
    public boolean showLoadingLogo = true;

    /** Logo asset path. Requires showLoadingLogo true. */
    public String logoPath = "startup-logo.png";

    public String getHtmlTitle() {
        return htmlTitle;
    }

    public String getHtmlWidth() {
        return String.valueOf(htmlWidth);
    }

    public String getHtmlHeight() {
        return String.valueOf(htmlHeight);
    }

    public boolean isShowLoadingLogo() {
      return showLoadingLogo;
    }

    public boolean shouldUseDefaultHtmlIndex() {
      return useDefaultHtmlIndex;
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

    public ArrayList<AssetFileHandle> assetsPath() {
        return assetsPath;
    }

    public boolean shouldGenerateAssetFile() {
        return shouldGenerateAssetFile;
    }

    public AssetFilter assetFilter() {
        return assetFilter;
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

    public void setApplicationListener(Class<? extends ApplicationListener> applicationListener) {
        setApplicationListener(applicationListener.getName());
    }

    public void setApplicationListener(String applicationListener) {
        mainApplicationClass = applicationListener;
    }

    public boolean acceptClasspath(URL url) {
        return true;
    }

    public String getLogoPath() {
        return logoPath;
    }
}
