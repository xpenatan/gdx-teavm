package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface HTMLElementWrapper extends ElementWrapper {

	public HTMLElementWrapper getOffsetParent();

	public int getOffsetTop();

	public int getOffsetLeft();
}
