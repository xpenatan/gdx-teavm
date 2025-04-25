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

    public String mainApplicationClass;

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
    public BuildMode buildMode = BuildMode.JAVASCRIPT;

    public int htmlWidth = 800;
    public int htmlHeight = 600;

    /** True to use the default html index. False will stop overwriting html file. */
    public boolean useDefaultHtmlIndex = true;

    /** If the logo is shown while the application is loading. Requires showLoadingLogo true. */
    public boolean showLoadingLogo = true;

    /** Logo asset path. Requires showLoadingLogo true. */
    public String logoPath = "startup-logo.png";

    public boolean obfuscated = true;
}
