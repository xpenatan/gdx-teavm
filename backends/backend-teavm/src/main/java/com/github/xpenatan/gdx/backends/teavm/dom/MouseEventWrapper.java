package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface MouseEventWrapper extends EventWrapper {
    @JSProperty
    int getClientX();
    @JSProperty
    int getClientY();
    @JSProperty
    float getMovementX();
    @JSProperty
    float getMovementY();
    @JSProperty
    short getButton();
}