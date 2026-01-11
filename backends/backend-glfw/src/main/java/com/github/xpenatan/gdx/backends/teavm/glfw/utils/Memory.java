package com.github.xpenatan.gdx.backends.teavm.glfw.utils;

import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.Unmanaged;
import org.teavm.interop.c.Include;

public final class Memory {
    private Memory() {
    }

    @Include("stdlib.h")
    @Import(name = "malloc")
    @Unmanaged
    public static native Address malloc(int size);

    @Include("stdlib.h")
    @Import(name = "free")
    @Unmanaged
    public static native void free(Address address);

    @Include("string.h")
    @Import(name = "memcpy")
    @Unmanaged
    public static native void memcpy(Address target, Address source, int size);
}
