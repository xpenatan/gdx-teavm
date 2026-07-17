package com.badlogic.gdx.controllers.android;

public final class AndroidControllers extends AndroidControllerManager {

    private static final class InstanceHolder {
        static final AndroidControllers instance = new AndroidControllers();
    }

    public AndroidControllers() {
    }

    public static AndroidControllers getInstance() {
        return InstanceHolder.instance;
    }
}
