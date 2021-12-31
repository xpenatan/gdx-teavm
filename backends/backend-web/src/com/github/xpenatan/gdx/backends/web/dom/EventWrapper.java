package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface EventWrapper {

	String getType();

	EventTargetWrapper getTarget();

	public void preventDefault();

	public void stopPropagation();

	public float getDetail();
}
