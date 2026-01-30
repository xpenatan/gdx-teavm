package com.github.xpenatan.gdx.teavm.backends.psp.types;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class ScePspFQuaternion extends Structure {
    public float x;
    public float y;
    public float z;
    public float w;

    public static ScePspFQuaternion malloc() {
        return Memory.malloc(sizeOf(ScePspFQuaternion.class)).toStructure();
    }
}
