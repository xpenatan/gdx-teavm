package com.github.xpenatan.gdx.backends.web.dom.typedarray;

/**
 * @author xpenatan
 */
public interface Int16ArrayWrapper extends ArrayBufferViewWrapper {
    // Int16Array
    public static final int BYTES_PER_ELEMENT = 2;

    public int getLength();

    public short get(int index);

    public void set(int index, short value);

    public void set(Int16ArrayWrapper array);

    public void set(Int16ArrayWrapper array, int offset);

//	public void set(ShortArray array);

//	public void set(ShortArray array, int offset);

    public Int16ArrayWrapper subarray(int start, int end);
}
