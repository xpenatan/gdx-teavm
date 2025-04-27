package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 */
@JSFunctor
public interface EventListenerWrapper extends JSObject {
    void handleEvent(EventWrapper evt);
}