package com.github.xpenatan.gdx.teavm.backends.android;

public final class AndroidInputBridge {
    private static OnscreenKeyboardController onscreenKeyboardController;

    private AndroidInputBridge() {
    }

    public static void setOnscreenKeyboardController(OnscreenKeyboardController controller) {
        onscreenKeyboardController = controller;
    }

    public static void clearOnscreenKeyboardController(OnscreenKeyboardController controller) {
        if(onscreenKeyboardController == controller) {
            onscreenKeyboardController = null;
        }
    }

    public static void setOnscreenKeyboardVisible(boolean visible) {
        OnscreenKeyboardController controller = onscreenKeyboardController;
        if(controller != null) {
            controller.setVisible(visible);
        }
    }

    public interface OnscreenKeyboardController {
        void setVisible(boolean visible);
    }
}
