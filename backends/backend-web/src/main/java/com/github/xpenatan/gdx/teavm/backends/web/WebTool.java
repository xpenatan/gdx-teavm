package com.github.xpenatan.gdx.teavm.backends.web;

/**
 * @author xpenatan
 */
public class WebTool {
    private static boolean isProd = true;
    public static boolean isProdMode() {
        return isProd;
    }

    public static void setIsProd(boolean flag) {
        isProd = flag;
    }
}
