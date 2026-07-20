package com.github.xpenatan.gdx.teavm.backends.glfw.utils;

import org.teavm.interop.Import;

/**
 * Provides access to the native platform handles owned by a GLFW window.
 *
 * <p>Methods return {@code 0} when the requested handle is unavailable on the
 * current platform. Use {@link #getPlatform()} and the {@code GLFW_PLATFORM_*}
 * constants from {@link GLFW} to select the matching accessors.</p>
 */
public final class GLFWNative {

    private GLFWNative() {
    }

    /** Returns the GLFW platform currently in use. */
    @Import(name = "gdx_teavm_glfw_get_platform")
    public static native int getPlatform();

    /** Returns the Win32 {@code HWND} for a GLFW window. */
    @Import(name = "gdx_teavm_glfw_get_win32_window")
    public static native long getWin32Window(long glfwWindow);

    /** Returns the X11 {@code Display*} used by GLFW. */
    @Import(name = "gdx_teavm_glfw_get_x11_display")
    public static native long getX11Display();

    /** Returns the X11 {@code Window} for a GLFW window. */
    @Import(name = "gdx_teavm_glfw_get_x11_window")
    public static native long getX11Window(long glfwWindow);

    /** Returns the Wayland {@code wl_display*} used by GLFW. */
    @Import(name = "gdx_teavm_glfw_get_wayland_display")
    public static native long getWaylandDisplay();

    /** Returns the Wayland {@code wl_surface*} for a GLFW window. */
    @Import(name = "gdx_teavm_glfw_get_wayland_window")
    public static native long getWaylandWindow(long glfwWindow);

    /** Returns the Cocoa {@code NSWindow*} for a GLFW window. */
    @Import(name = "gdx_teavm_glfw_get_cocoa_window")
    public static native long getCocoaWindow(long glfwWindow);
}
