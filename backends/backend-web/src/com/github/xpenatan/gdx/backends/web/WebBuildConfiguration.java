package com.github.xpenatan.gdx.backends.web;

import java.io.File;
import java.net.URL;

import com.badlogic.gdx.utils.Array;

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

    public abstract String getMainClass();

    public abstract String getApplicationListenerClass();

    public abstract Array<URL> getAdditionalClasspath();

    public abstract String getWebAppPath();

    public abstract void assetsClasspath(Array<String> classPaths);

    /**
     * @param paths
     * @return true to generate a file which contains all assets patch
     */
    public abstract boolean assetsPath(Array<File> paths);

    public abstract boolean minifying();

    public abstract Array<String> getReflectionInclude();
    public abstract Array<String> getReflectionExclude();
}
