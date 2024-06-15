package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSMethod;

/**
 * @author xpenatan
 */
public interface ElementWrapper extends NodeWrapper {

    int getScrollTop();

    int getScrollLeft();

    int getClientWidth();

    int getClientHeight();

    void setAttribute(String qualifiedName, String value);

    StyleWrapper getStyle();


    @JSMethod
    void remove();
}
