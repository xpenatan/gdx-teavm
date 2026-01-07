package com.github.xpenatan.gdx.backends.teavm.glfw;

public class TeaNativeCallbacks {
    public static void onError(int error, String description) {
        System.err.println("GLFW error: " + error + ": " + description);
    }
}
