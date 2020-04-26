package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface HTMLElementWrapper extends ElementWrapper {

	public HTMLElementWrapper getOffsetParent();

	public int getOffsetTop();

	public int getOffsetLeft();
}
