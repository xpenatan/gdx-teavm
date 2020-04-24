package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface WindowWrapper {

	public HTMLDocumentWrapper getDocument();

	public void requestAnimationFrame(Runnable runnable);
}
