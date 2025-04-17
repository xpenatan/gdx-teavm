package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

public interface LocationWrapper extends JSObject {
    @JSProperty
    String getHref();
}