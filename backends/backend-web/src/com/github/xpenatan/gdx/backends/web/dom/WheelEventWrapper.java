package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface WheelEventWrapper extends EventWrapper {
	public float getDeltaX();

	public float getDeltaY();

	public float getDeltaZ();

	public float getWheelDelta();
}
