package com.github.xpenatan.gdx.backends.web.dom.typedarray;

/**
 * @author xpenatan
 */
public interface ArrayBufferViewWrapper {
    // ArrayBufferView
    public ArrayBufferWrapper getBuffer();

    public int getByteOffset();

    public int getByteLength();
}
