package com.github.xpenatan.gdx.teavm.backends.glfw.utils;

import org.teavm.interop.Import;

/** Provides access to GLFW's native Win32 window handle. */
public final class GLFWNativeWin32 {

    private GLFWNativeWin32() {
    }

    /**
     * Returns the Win32 {@code HWND} for a GLFW window, or {@code 0} when unavailable.
     *
     * @param glfwWindow address of a {@code GLFWwindow}
     */
    @Import(name = "gdx_teavm_glfw_get_win32_window")
    public static native long getWin32Window(long glfwWindow);
}
