package com.github.xpenatan.gdx.backends.web;

import com.github.xpenatan.gdx.backends.web.preloader.AssetFilter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author xpenatan
 */
public abstract class WebBuildConfiguration {

    public static void log(String msg) {
        String text = "| " + msg;
        logInternalNewLine(text);
    }

    public static void logInternal(String msg) {
        System.err.print(msg);
    }

    public static void logInternalNewLine(String msg) {
        logInternal(msg + "\n");
    }

    public static void logHeader(String text) {
        String msg = "";
        msg += "#################################################################\n";
        msg += "|\n| " + text + "\n|";
        msg += "\n" + "#################################################################";

        logInternalNewLine(msg);
    }

    public static void logEnd() {
        String msg = "\n#################################################################";
        logInternalNewLine(msg);
    }

    public boolean acceptClasspath(URL url) {
        return true;
    }

    public abstract String getHtmlWidth();

    public abstract String getHtmlHeight();

    public abstract String getMainClass();

    public abstract String getMainClassArgs();

    public abstract String getApplicationListenerClass();

    public abstract ArrayList<URL> getAdditionalClasspath();

    public abstract ArrayList<String> getAdditionalAssetClasspath();

    public abstract String getWebAppPath();

    public abstract void assetsClasspath(ArrayList<String> classPaths);

    /** @return True to generate a file which contains all assets patch */
    public abstract boolean assetsPath(ArrayList<File> paths);

    /** An asset filter for files we want to copy to the "assets" directory or 'null' for all. */
    public abstract AssetFilter assetFilter();

    public abstract boolean minifying();

    public abstract ArrayList<String> getReflectionInclude();

    public abstract ArrayList<String> getReflectionExclude();

    public abstract ArrayList<String> getClassesToPreserve();

    /**
     * Array of class.getName() to skip
     */
    public abstract ArrayList<String> getSkipClasses();

    public abstract boolean logClasses();
}
