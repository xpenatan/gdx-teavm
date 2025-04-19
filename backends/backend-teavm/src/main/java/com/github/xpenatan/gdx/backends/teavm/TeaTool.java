package com.github.xpenatan.gdx.backends.teavm;

/**
 * @author xpenatan
 */
public class TeaTool {
    private static boolean isProd = false;
    public static boolean isProdMode() {
        return isProd;
    }

    public static void setIsProd(boolean flag) {
        isProd = flag;
    }
}
