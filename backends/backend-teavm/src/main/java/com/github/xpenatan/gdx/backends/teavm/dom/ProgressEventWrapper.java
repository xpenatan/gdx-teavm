package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface ProgressEventWrapper extends EventWrapper {
    @JSProperty
    int getLoaded();
}
