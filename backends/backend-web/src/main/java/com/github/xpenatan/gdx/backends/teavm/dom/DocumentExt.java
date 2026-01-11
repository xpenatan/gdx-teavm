package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.html.HTMLDocument;

public abstract class DocumentExt extends HTMLDocument {

    @JSProperty
    public abstract String getCompatMode();
}