package com.github.xpenatan.gdx.teavm.backend.web;

/**
 * @author xpenatan
 */
public class TeaTool {
    private static boolean isProd = true;
    public static boolean isProdMode() {
        return isProd;
    }

    public static void setIsProd(boolean flag) {
        isProd = flag;
    }
}
