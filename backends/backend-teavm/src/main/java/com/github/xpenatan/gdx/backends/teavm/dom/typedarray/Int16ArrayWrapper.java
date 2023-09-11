package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface Int16ArrayWrapper extends ArrayBufferViewWrapper {
    // Int16Array
    static final int BYTES_PER_ELEMENT = 2;

    @JSProperty
    int getLength();

    @JSIndexer
    short get(int index);

    @JSIndexer
    void set(int index, short value);

    void set(int index, int value);

    void set(Int16ArrayWrapper array);

    void set(Int16ArrayWrapper array, int offset);

//	void set(ShortArray array);

//	void set(ShortArray array, int offset);

    Int16ArrayWrapper subarray(int start, int end);
}