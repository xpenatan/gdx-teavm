package com.github.xpenatan.gdx.backends.teavm.dom;

/**
 * @author xpenatan
 */
public interface KeyboardEventWrapper extends EventWrapper {

    public int getCharCode();

    public int getKeyCode();
}
