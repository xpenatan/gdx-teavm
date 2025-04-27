package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface WheelEventWrapper extends EventWrapper {
    @JSProperty
    float getDeltaX();
    @JSProperty
    float getDeltaY();
    @JSProperty
    float getDeltaZ();
    @JSProperty
    float getWheelDelta();
}