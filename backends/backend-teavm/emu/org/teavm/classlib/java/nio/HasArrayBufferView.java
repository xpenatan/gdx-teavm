package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;

public interface HasArrayBufferView {
    Int8ArrayWrapper getTypedArray();
    int getElementSize();
}