package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface WindowWrapper {

	public DocumentWrapper getDocument();

	public void requestAnimationFrame(Runnable runnable);

	public TimerWrapper getTimer();

	public LocationWrapper getLocation();
}
