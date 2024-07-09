package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface ArrayBufferWrapper extends JSObject {
    @JSProperty
    int getByteLength();
    @JSProperty
    boolean isDetached();
}