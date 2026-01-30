package com.github.xpenatan.gdx.teavm.backends.psp.utils;

import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("PSPCoreApi.h")
public class PSPCoreApi {

    @Import(name = "isRunning")
    public static native boolean isRunning();

    @Import(name = "setupCallbacks")
    public static native int setupCallbacks();

}