package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

/**
 * @author xpenatan
 */
public interface LongArrayWrapper {
    // LongArray
    public int getLength();

    public void setLength(int length);

    public int getElement(int index);

    public void setElement(int index, int value);
}
