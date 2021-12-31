package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface EventTargetWrapper {
	// EventTarget
	public void addEventListener(String type, EventListenerWrapper listener);

	public void addEventListener(String type, EventListenerWrapper listener, boolean capture);

	public void removeEventListener(String type, EventListenerWrapper listener);

	public void removeEventListener(String type, EventListenerWrapper listener, boolean capture);

	public boolean dispatchEvent(EventWrapper event);
}
