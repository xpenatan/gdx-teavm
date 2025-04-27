package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 */
public interface EventTargetWrapper extends JSObject {
    // EventTarget
    void addEventListener(String type, EventListenerWrapper listener);
    void addEventListener(String type, EventListenerWrapper listener, boolean capture);
    void removeEventListener(String type, EventListenerWrapper listener);
    void removeEventListener(String type, EventListenerWrapper listener, boolean capture);
    boolean dispatchEvent(EventWrapper event);
}