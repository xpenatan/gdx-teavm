package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.html.HTMLElement;

public abstract class HTMLElementExt extends HTMLElement {

    @JSProperty
    public abstract HTMLElement getOffsetParent();
}