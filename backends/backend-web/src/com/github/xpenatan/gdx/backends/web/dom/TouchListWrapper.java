package com.github.xpenatan.gdx.backends.web.dom;

/**
 * @author xpenatan
 */
public interface TouchListWrapper extends EventWrapper {

	public int getLength();

	public TouchWrapper item(int index);
}
