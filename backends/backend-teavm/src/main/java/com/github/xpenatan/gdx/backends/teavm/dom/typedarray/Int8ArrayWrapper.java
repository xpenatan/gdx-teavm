package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import com.badlogic.gdx.utils.ByteArray;
import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSProperty;

/**
 * @author xpenatan
 */
public interface Int8ArrayWrapper extends ArrayBufferViewWrapper {
    // Int8Array
    static final int BYTES_PER_ELEMENT = 1;

    @JSProperty
    int getLength();

    @JSIndexer
    byte get(int index);

    @JSIndexer
    void set(int index, byte value);

    void set(Int8ArrayWrapper array);

    void set(Int8ArrayWrapper array, int offset);

    void set(ByteArray array);

    void set(ByteArray array, int offset);

    Int8ArrayWrapper subarray(int start, int end);
}