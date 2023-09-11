package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import org.teavm.jso.JSIndexer;

/**
 * @author xpenatan
 */
public interface Float64ArrayWrapper extends ArrayBufferViewWrapper {

    @JSIndexer
    void set(int index, double value);
}