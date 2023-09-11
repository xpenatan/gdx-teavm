package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface ArrayBufferViewWrapper extends JSObject {
    // ArrayBufferView
    @JSProperty
    ArrayBufferWrapper getBuffer();

    @JSProperty
    int getByteOffset();

    @JSProperty
    int getByteLength();
}