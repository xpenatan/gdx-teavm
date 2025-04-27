package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface TouchEventWrapper extends EventWrapper {
    @JSProperty
    TouchListWrapper getChangedTouches();
}