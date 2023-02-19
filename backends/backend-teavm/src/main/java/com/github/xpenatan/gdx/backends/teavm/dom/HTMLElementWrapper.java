package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface HTMLElementWrapper extends ElementWrapper {

    public HTMLElementWrapper getOffsetParent();

    public int getOffsetTop();

    public int getOffsetLeft();
}
