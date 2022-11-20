package com.github.xpenatan.gdx.backends.web.dom.typedarray;

public interface Uint8ClampedArrayWrapper extends ArrayBufferViewWrapper {

    public int getLength();

    public byte get(int index);

    public void set(int index, byte value);

    public void set(Uint8ClampedArrayWrapper array);

    public void set(Uint8ClampedArrayWrapper array, int offset);

    public Uint8ClampedArrayWrapper subarray(int start, int end);
}
