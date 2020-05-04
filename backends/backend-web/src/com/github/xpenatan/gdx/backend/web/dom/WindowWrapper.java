package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface WindowWrapper {

	public HTMLDocumentWrapper getDocument();

	public void requestAnimationFrame(Runnable runnable);

	public int setTimeout(Runnable run, int delay);

	public LocationWrapper getLocation();
}
