package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface XMLHttpRequestEventTargetWrapper extends EventTargetWrapper {
    @JSProperty
    void setOnprogress(EventHandlerWrapper onprogress);
}