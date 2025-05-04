package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface TouchListWrapper extends EventWrapper {
    @JSProperty
    int getLength();
    TouchWrapper item(int index);
}