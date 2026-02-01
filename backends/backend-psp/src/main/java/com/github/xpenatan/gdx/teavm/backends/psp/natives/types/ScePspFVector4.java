package com.github.xpenatan.gdx.teavm.backends.psp.natives.types;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class ScePspFVector4 extends Structure {
    public float x;
    public float y;
    public float z;
    public float w;

    public static ScePspFVector4 malloc() {
        return Memory.malloc(sizeOf(ScePspFVector4.class)).toStructure();
    }
}