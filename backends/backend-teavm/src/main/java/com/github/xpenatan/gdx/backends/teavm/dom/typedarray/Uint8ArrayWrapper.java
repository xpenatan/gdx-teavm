package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface Uint8ArrayWrapper extends ArrayBufferViewWrapper {
    // Uint8Array
    static final int BYTES_PER_ELEMENT = 1;

    @JSProperty
    int getLength();

    @JSIndexer
    byte get(int index);

    @JSIndexer
    void set(int index, byte value);

    void set(Uint8ArrayWrapper array);

    void set(Uint8ArrayWrapper array, int offset);

//	void set(OctetArray array);

//	void set(OctetArray array, int offset);

    Uint8ArrayWrapper subarray(int start, int end);
}