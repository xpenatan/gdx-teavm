package com.github.xpenatan.gdx.teavm.backends.web.dom;

import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.html.HTMLDocument;

public abstract class HTMLDocumentExt extends HTMLDocument {

    @JSProperty
    public abstract String getVisibilityState();
}