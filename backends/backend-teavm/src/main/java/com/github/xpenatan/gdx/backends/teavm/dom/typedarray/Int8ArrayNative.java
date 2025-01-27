package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import org.teavm.jso.typedarrays.Int8Array;

/**
 * This class wrap the Int8Array object.
 * This is used when the javascript buffer need to be updated inside java ByteBuffer.
 * For example: when emscripten HEAPU8 buffer resize, the old buffer gets detached and invalidate java bytebuffer.
 * This class make sure that it will always use the updated buffer
 */
public class Int8ArrayNative {

    public Int8ArrayNativeListener listener;
    private Int8ArrayWrapper buffer;

    public Int8ArrayNative() {
    }

    public Int8ArrayNative(int length) {
        buffer = (Int8ArrayWrapper)new Int8Array(length);
    }

    public int getLength() {
        shouldRecreateBuffer();
        return buffer.getLength();
    }

    public byte get(int index) {
        shouldRecreateBuffer();
        return buffer.get(index);
    }

    public void set(int index, byte value) {
        shouldRecreateBuffer();
        buffer.set(index, value);
    }

    public void set(Int8ArrayWrapper array) {
        shouldRecreateBuffer();
        buffer.set(array);
    }

    public void set(Int8ArrayWrapper array, int offset) {
        shouldRecreateBuffer();
        buffer.set(array, offset);
    }

    public Int8ArrayWrapper subarray(int start, int end) {
        shouldRecreateBuffer();
        return buffer.subarray(start, end);
    }

    public Int8ArrayWrapper getBuffer() {
        shouldRecreateBuffer();
        return buffer;
    }

    public int getByteOffset() {
        shouldRecreateBuffer();
        return buffer.getByteOffset();
    }

    public int getByteLength() {
        shouldRecreateBuffer();
        return buffer.getByteLength();
    }

    public void shouldRecreateBuffer() {
        boolean recreate = false;
        if(buffer == null) {
            recreate = true;
        }
        else {
            ArrayBufferWrapper arrayBuffer = buffer.getBuffer();
            recreate = arrayBuffer.isDetached();
        }
        if(recreate) {
            buffer = listener.recreateBuffer();
            listener.update();
        }
    }

    public interface Int8ArrayNativeListener {
        Int8ArrayWrapper recreateBuffer();
        void update();
    }
}