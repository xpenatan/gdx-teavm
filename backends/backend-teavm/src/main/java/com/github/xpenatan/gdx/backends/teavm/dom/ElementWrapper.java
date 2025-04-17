package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSMethod;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface ElementWrapper extends NodeWrapper {

    @JSProperty
    int getScrollTop();

    @JSProperty
    int getScrollLeft();

    @JSProperty
    int getClientWidth();

    @JSProperty
    int getClientHeight();

    void setAttribute(String qualifiedName, String value);

    @JSProperty
    StyleWrapper getStyle();


    @JSMethod
    void remove();
}