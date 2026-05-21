package com.github.xpenatan.gdx.teavm.backends.web.dom;

import org.teavm.jso.JSClass;
import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.html.HTMLElement;

@JSClass(transparent = true)
public abstract class HTMLElementExt extends HTMLElement {

    @JSProperty
    public abstract HTMLElement getOffsetParent();
}
