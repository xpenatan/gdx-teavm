package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface EventWrapper extends JSObject {

    @JSProperty
    String getType();

    @JSProperty(value = "target")
    EventTargetWrapper getTarget();

    @JSProperty
    float getDetail();

    void preventDefault();

    void stopPropagation();
}
