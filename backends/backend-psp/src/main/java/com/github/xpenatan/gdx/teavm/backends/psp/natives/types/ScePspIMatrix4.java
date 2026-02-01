package com.github.xpenatan.gdx.teavm.backends.psp.natives.types;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class ScePspIMatrix4 extends Structure {
    public ScePspIVector4 x;
    public ScePspIVector4 y;
    public ScePspIVector4 z;
    public ScePspIVector4 w;

    public static ScePspIMatrix4 malloc() {
        return Memory.malloc(sizeOf(ScePspIMatrix4.class)).toStructure();
    }
}