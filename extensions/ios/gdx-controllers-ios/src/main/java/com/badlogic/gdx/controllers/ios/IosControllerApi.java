package com.badlogic.gdx.controllers.ios;

import org.teavm.interop.Import;

public final class IosControllerApi {
    private IosControllerApi() {
    }

    @Import(name = "gdx_teavm_ios_controller_count")
    public static native int getControllerCount();

    @Import(name = "gdx_teavm_ios_controller_handle_at")
    public static native long getControllerHandle(int index);

    @Import(name = "gdx_teavm_ios_controller_release")
    public static native void releaseController(long handle);

    @Import(name = "gdx_teavm_ios_controller_connected")
    public static native int isConnected(long handle);

    @Import(name = "gdx_teavm_ios_controller_button")
    public static native int getButton(long handle, int buttonCode);

    @Import(name = "gdx_teavm_ios_controller_axis")
    public static native float getAxis(long handle, int axisCode);
}
