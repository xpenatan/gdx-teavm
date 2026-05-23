package com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins.substitutions;

import org.teavm.interop.Address;

public final class GdxTeaVMArrayFastPaths {

    private GdxTeaVMArrayFastPaths() {
    }

    public static char getChar(char[] array, int index) {
        return (char)Address.ofData(array).add(index << 1).getShort();
    }

    public static void putChar(char[] array, int index, char value) {
        Address.ofData(array).add(index << 1).putShort((short)value);
    }

    public static short getShort(short[] array, int index) {
        return Address.ofData(array).add(index << 1).getShort();
    }

    public static void putShort(short[] array, int index, short value) {
        Address.ofData(array).add(index << 1).putShort(value);
    }

    public static int getInt(int[] array, int index) {
        return Address.ofData(array).add(index << 2).getInt();
    }

    public static void putInt(int[] array, int index, int value) {
        Address.ofData(array).add(index << 2).putInt(value);
    }

    public static long getLong(long[] array, int index) {
        return Address.ofData(array).add(index << 3).getLong();
    }

    public static void putLong(long[] array, int index, long value) {
        Address.ofData(array).add(index << 3).putLong(value);
    }

    public static float getFloat(float[] array, int index) {
        return Address.ofData(array).add(index << 2).getFloat();
    }

    public static void putFloat(float[] array, int index, float value) {
        Address.ofData(array).add(index << 2).putFloat(value);
    }

    public static double getDouble(double[] array, int index) {
        return Address.ofData(array).add(index << 3).getDouble();
    }

    public static void putDouble(double[] array, int index, double value) {
        Address.ofData(array).add(index << 3).putDouble(value);
    }
}
