package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

public interface HasArrayBufferView {
    Int8ArrayWrapper getArrayBufferView();
    void setInt8ArrayNative(Int8ArrayNative array);
}