package com.github.xpenatan.gdx.backends.teavm.dom;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Uint8ClampedArrayWrapper;

/**
 * @author xpenatan
 */
public interface ImageDataWrapper {
    // ImageData
    public int getWidth();

    public int getHeight();

    public Uint8ClampedArrayWrapper getData();
}
