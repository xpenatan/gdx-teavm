package com.github.xpenatan.gdx.backends.teavm.config;

import java.net.URL;
import java.util.ArrayList;
import org.teavm.tooling.TeaVMTargetType;

/**
 * @author xpenatan
 */
public class TeaBuildConfiguration {

    public AssetFilter assetFilter = null;
    public ArrayList<AssetFileHandle> assetsPath = new ArrayList<>();

    /**
     * Assets within a package (FileType.Classpath) must be added here. For example: com/lib/my/asset
     */
    public ArrayList<String> assetsClasspath = new ArrayList<>();

    /**
     * The true flag will generate an assets.txt file in the dist folder to enable asset preloading.
     */
    public boolean shouldGenerateAssetFile = true;

    public String webappPath;
    public final ArrayList<URL> additionalClasspath = new ArrayList<>();

    /**
     * This list prevents a class from being removed by TeaVM if it is not used. Reflection also prevents a class from being removed during optimization.
     */
    public final ArrayList<String> classesToPreserve = new ArrayList<>();

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

    public BaseWebApp webApp;
    public TeaVMTargetType targetType;
    public String targetFileName = "app";

    /**
     * A listener to enable a class for reflection. Note that using reflection increases the size of JavaScript/WebAssembly code.
     */
    public TeaBuildReflectionListener reflectionListener;

    public TeaBuildConfiguration(TeaVMTargetType targetType) {
        this.targetType = targetType;
    }
}
