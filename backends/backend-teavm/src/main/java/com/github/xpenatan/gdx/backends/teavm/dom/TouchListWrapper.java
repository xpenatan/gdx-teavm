package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface TouchListWrapper extends EventWrapper {

    public int getLength();

    public TouchWrapper item(int index);
}
