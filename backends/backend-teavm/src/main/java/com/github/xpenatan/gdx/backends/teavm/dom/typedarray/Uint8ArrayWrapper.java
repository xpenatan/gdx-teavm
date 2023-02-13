package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

/**
 * @author xpenatan
 */
public interface Uint8ArrayWrapper extends ArrayBufferViewWrapper {
    // Uint8Array
    public static final int BYTES_PER_ELEMENT = 1;

    public int getLength();

    public byte get(int index);

    public void set(int index, byte value);

    public void set(Uint8ArrayWrapper array);

    public void set(Uint8ArrayWrapper array, int offset);

//	public void set(OctetArray array);

//	public void set(OctetArray array, int offset);

    public Uint8ArrayWrapper subarray(int start, int end);
}
