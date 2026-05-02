package com.github.xpenatan.gdx.teavm.backends.psp.natives.types;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class ScePspIVector4 extends Structure {
    public int x;
    public int y;
    public int z;
    public int w;

    public static ScePspIVector4 malloc() {
        return Memory.malloc(sizeOf(ScePspIVector4.class)).toStructure();
    }
}