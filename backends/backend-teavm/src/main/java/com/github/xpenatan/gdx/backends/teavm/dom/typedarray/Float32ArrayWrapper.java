package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

/**
 * @author xpenatan
 */
public interface Float32ArrayWrapper extends ArrayBufferViewWrapper {
    // Float32Array
    public static final int BYTES_PER_ELEMENT = 4;

    public int getLength();

    public float get(int index);

    public void set(int index, float value);

    public void set(Float32ArrayWrapper array);

    public void set(Float32ArrayWrapper array, int offset);

    public void set(FloatArrayWrapper array);

    public void set(FloatArrayWrapper array, int offset);

    public Float32ArrayWrapper subarray(int start, int end);
}
