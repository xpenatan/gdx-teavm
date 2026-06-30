package com.github.xpenatan.gdx.teavm.backends.glfw;

import org.teavm.interop.Import;

final class GLFWNativeRuntime {
    private static final int OS_WINDOWS = 1;
    private static final int OS_LINUX = 2;
    private static final int OS_MAC_OS_X = 3;

    private GLFWNativeRuntime() {
    }

    static String getOsName() {
        int nativeOs = getNativeOs();
        if(nativeOs == OS_WINDOWS) {
            return "Windows";
        }
        if(nativeOs == OS_LINUX) {
            return "Linux";
        }
        if(nativeOs == OS_MAC_OS_X) {
            return "Mac OS X";
        }
        return null;
    }

    @Import(name = "gdx_teavm_glfw_os")
    private static native int getNativeOs();
}
