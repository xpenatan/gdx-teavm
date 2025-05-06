package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.events.WheelEvent;

public interface WheelEventExt extends WheelEvent {
    @JSProperty
    float getWheelDelta();
}