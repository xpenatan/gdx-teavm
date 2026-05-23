package com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins.substitutions;

import org.teavm.interop.Address;

public final class GdxTeaVMFloatArrayCopy {

    private static final int FLOAT_SIZE = 4;

    private GdxTeaVMFloatArrayCopy() {
    }

    public static void copy(float[] src, int srcOffset, float[] dst, int dstOffset, int count) {
        Address srcAddress = Address.ofData(src).add(srcOffset * FLOAT_SIZE);
        Address dstAddress = Address.ofData(dst).add(dstOffset * FLOAT_SIZE);
        Address.moveMemoryBlock(srcAddress, dstAddress, count * FLOAT_SIZE);
    }

}
