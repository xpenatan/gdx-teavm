package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface TouchEventWrapper extends EventWrapper {

    public TouchListWrapper getChangedTouches();
}
