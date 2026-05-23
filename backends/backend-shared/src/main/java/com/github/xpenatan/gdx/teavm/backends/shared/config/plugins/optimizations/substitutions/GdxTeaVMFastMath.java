package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions;

import org.teavm.interop.Import;

public final class GdxTeaVMFastMath {

    public static float sinDeg(float degrees) {
        return sinDegNative(degrees);
    }

    public static float cosDeg(float degrees) {
        return cosDegNative(degrees);
    }

    private GdxTeaVMFastMath() {
    }

    @Import(name = "teavm_fastmath_sin_deg")
    private static native float sinDegNative(float degrees);

    @Import(name = "teavm_fastmath_cos_deg")
    private static native float cosDegNative(float degrees);
}
