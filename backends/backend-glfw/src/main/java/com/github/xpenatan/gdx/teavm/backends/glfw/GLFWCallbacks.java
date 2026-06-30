package com.github.xpenatan.gdx.teavm.backends.glfw;

import org.teavm.interop.Address;
import org.teavm.interop.Strings;

public class GLFWCallbacks {
    public static void onError(int error, Address description) {
        String text = description == null ? "" : Strings.fromC(description);
        System.err.println("GLFW error: " + error + ": " + text);
    }
}
