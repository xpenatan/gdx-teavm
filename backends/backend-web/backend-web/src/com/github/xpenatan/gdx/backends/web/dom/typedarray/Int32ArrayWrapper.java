package com.github.xpenatan.gdx.backends.web.dom.typedarray;

/**
 * @author xpenatan
 */
public interface Int32ArrayWrapper extends ArrayBufferViewWrapper {
    // Int32Array
    public static final int BYTES_PER_ELEMENT = 4;

    public int getLength();

    public int get(int index);

    public void set(int index, int value);

    public void set(Int32ArrayWrapper array);

    public void set(Int32ArrayWrapper array, int offset);

    public void set(LongArrayWrapper array);

    public void set(LongArrayWrapper array, int offset);

    public Int32ArrayWrapper subarray(int start, int end);
}
