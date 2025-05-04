package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface KeyboardEventWrapper extends EventWrapper {
    @JSProperty
    int getCharCode();
    @JSProperty
    int getKeyCode();
}