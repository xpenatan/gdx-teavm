package com.github.xpenatan.gdx.teavm.backends.psp.natives;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.Unmanaged;
import org.teavm.interop.c.Include;

public class PSPMemory {

    private PSPMemory() {
    }

    public static Address malloc(int size) {
        return Memory.malloc(size);
    }

    public static void free(Address address) {
        Memory.free(address);
    }

    public static void memcpy(Address target, Address source, int size) {
        Memory.memcpy(target, source, size);
    }

    @Include("malloc.h")
    @Import(name = "memalign")
    @Unmanaged
    public static native Address memalign(int alignment, int size);

}