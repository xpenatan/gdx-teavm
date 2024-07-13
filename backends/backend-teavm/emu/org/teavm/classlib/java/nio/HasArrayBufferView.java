package org.teavm.classlib.java.nio;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;

public interface HasArrayBufferView {
    ArrayBufferViewWrapper getArrayBufferView();
    ArrayBufferViewWrapper getOriginalArrayBufferView();
    int getElementSize();
}