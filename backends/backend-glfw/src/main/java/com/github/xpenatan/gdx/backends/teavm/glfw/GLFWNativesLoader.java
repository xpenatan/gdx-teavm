package com.github.xpenatan.gdx.backends.teavm.glfw;

public final class GLFWNativesLoader {
    static {
        System.setProperty("org.lwjgl.input.Mouse.allowNegativeMouseCoords", "true");
    }

    static public void load() {
        // Do not load
    }
}
