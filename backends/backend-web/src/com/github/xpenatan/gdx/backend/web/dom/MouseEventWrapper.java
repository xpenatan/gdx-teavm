package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface MouseEventWrapper extends EventWrapper {
	int getClientX();

	int getClientY();

	float getMovementX();

	float getMovementY();

	short getButton();
}
