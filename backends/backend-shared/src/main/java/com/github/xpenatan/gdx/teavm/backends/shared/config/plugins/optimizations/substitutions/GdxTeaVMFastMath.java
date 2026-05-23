package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions;

import org.teavm.interop.StaticInit;

@StaticInit
public final class GdxTeaVMFastMath {

    public static final int SIN_BITS = 14;
    public static final int SIN_MASK = ~(-1 << SIN_BITS);
    public static final int SIN_COUNT = SIN_MASK + 1;
    public static final float DEG_TO_INDEX = SIN_COUNT / 360.0f;
    public static final float[] SIN_TABLE = new float[SIN_COUNT];

    static {
        float radiansFull = (float)Math.PI * 2.0f;
        for (int i = 0; i < SIN_COUNT; i++) {
            SIN_TABLE[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * radiansFull);
        }
        SIN_TABLE[(int)(0 * DEG_TO_INDEX) & SIN_MASK] = 0;
        SIN_TABLE[(int)(90 * DEG_TO_INDEX) & SIN_MASK] = 1;
        SIN_TABLE[(int)(180 * DEG_TO_INDEX) & SIN_MASK] = 0;
        SIN_TABLE[(int)(270 * DEG_TO_INDEX) & SIN_MASK] = -1;
    }

    private GdxTeaVMFastMath() {
    }
}
