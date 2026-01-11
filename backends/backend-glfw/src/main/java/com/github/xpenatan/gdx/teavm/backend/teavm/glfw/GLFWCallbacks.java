package com.github.xpenatan.gdx.teavm.backend.teavm.glfw;

public class GLFWCallbacks {
    public static void onError(int error, String description) {
        System.err.println("GLFW error: " + error + ": " + description);
    }
}
