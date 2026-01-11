package com.github.xpenatan.gdx.teavm.backend.web.dom;

import org.teavm.jso.JSProperty;
import org.teavm.jso.dom.xml.Element;

public interface ElementExt extends Element {

    @JSProperty
    int getScrollTop();

    @JSProperty
    int getScrollLeft();
}