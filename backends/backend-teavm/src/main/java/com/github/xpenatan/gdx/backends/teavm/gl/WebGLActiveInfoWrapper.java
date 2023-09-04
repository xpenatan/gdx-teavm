package com.github.xpenatan.gdx.backends.teavm.gl;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface WebGLActiveInfoWrapper extends JSObject {
    @JSProperty()
    int getSize();

    @JSProperty()
    int getType();

    @JSProperty()
    String getName();
}