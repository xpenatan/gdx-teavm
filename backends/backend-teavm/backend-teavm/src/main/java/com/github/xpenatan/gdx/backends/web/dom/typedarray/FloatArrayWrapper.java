package com.github.xpenatan.gdx.backends.web.dom.typedarray;

/**
 * @author xpenatan
 */
public interface FloatArrayWrapper {
    // FloatArray
    public int getLength();

    public void setLength(int length);

    public float getElement(int index);

    public void setElement(int index, float value);
}
