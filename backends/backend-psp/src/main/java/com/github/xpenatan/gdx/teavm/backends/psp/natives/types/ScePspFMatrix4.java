package com.github.xpenatan.gdx.teavm.backends.psp.natives.types;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class ScePspFMatrix4 extends Structure {
    public ScePspFVector4 x;
    public ScePspFVector4 y;
    public ScePspFVector4 z;
    public ScePspFVector4 w;

    public static ScePspFMatrix4 malloc() {
        return Memory.malloc(sizeOf(ScePspFMatrix4.class)).toStructure();
    }
}