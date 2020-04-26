package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface TouchListWrapper extends EventWrapper {

	public int getLength();

	public TouchWrapper item(int index);
}
