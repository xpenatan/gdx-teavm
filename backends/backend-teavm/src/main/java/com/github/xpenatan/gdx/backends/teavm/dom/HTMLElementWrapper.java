package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface HTMLElementWrapper extends ElementWrapper {

    HTMLElementWrapper getOffsetParent();

    int getOffsetTop();

    int getOffsetLeft();
}
