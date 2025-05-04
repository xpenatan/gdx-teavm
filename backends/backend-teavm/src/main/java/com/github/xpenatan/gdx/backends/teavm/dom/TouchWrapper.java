package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface TouchWrapper extends JSObject {
    @JSProperty
    int getIdentifier();
    @JSProperty
    int getClientX();
    @JSProperty
    int getClientY();
}