package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface HTMLElementWrapper extends ElementWrapper {

    @JSProperty
    HTMLElementWrapper getOffsetParent();

    @JSProperty
    int getOffsetTop();

    @JSProperty
    int getOffsetLeft();
}
