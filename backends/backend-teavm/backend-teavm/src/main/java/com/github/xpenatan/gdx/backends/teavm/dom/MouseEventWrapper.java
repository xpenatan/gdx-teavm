package com.github.xpenatan.gdx.backends.teavm.dom;

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
