package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface ElementWrapper extends NodeWrapper {

    public int getScrollTop();

    public int getScrollLeft();

    public int getClientWidth();

    public int getClientHeight();

    public void setAttribute(String qualifiedName, String value);

    public StyleWrapper getStyle();
}
