package com.github.xpenatan.gdx.backends.teavm;

/**
 * @author xpenatan
 */
public class TeaTool {
    private static boolean isProd = true;
    private static boolean useGLArrayBuffer;
    public static boolean isProdMode() {
        return isProd;
    }

    public static void setIsProd(boolean flag) {
        isProd = flag;
    }

    public static boolean useGLArrayBuffer() {
        return useGLArrayBuffer;
    }

    public static void setGLArrayBuffer(boolean flag) {
        useGLArrayBuffer = flag;
    }
}
