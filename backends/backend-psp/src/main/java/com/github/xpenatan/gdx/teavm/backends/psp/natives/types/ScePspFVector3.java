package com.github.xpenatan.gdx.teavm.backends.psp.natives.types;

import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class ScePspFVector3 extends Structure {
    public float x;
    public float y;
    public float z;

    private ScePspFVector3() {}

    public static ScePspFVector3 malloc() {
        return Memory.malloc(sizeOf(ScePspFVector3.class)).toStructure();
    }

    public static void set(ScePspFVector3 obj, float x, float y, float z) {
        obj.x = x;
        obj.y = y;
        obj.z = z;
    }
}