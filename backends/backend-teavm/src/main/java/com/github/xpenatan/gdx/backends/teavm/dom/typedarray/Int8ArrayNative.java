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
    public Int8ArrayWrapper buffer;
    private ArrayBufferWrapper arrayBuffer;

    public Int8ArrayNative() {
    }

    public Int8ArrayNative(int length) {
        buffer = (Int8ArrayWrapper)new Int8Array(length);
    }

    public int getLength() {
        return buffer.getLength();
    }

    public byte get(int index) {
        return buffer.get(index);
    }

    public void set(int index, byte value) {
        buffer.set(index, value);
    }

    public Int8ArrayWrapper getBuffer() {
        verifyDetachedBuffer();
        return buffer;
    }

    public void verifyDetachedBuffer() {
        boolean recreate = false;
        if(buffer == null) {
            recreate = true;
        }
        else {
            if(arrayBuffer == null) {
                arrayBuffer = buffer.getBuffer();
            }
            recreate = arrayBuffer.isDetached();
        }
        if(recreate) {
            buffer = listener.recreateBuffer();
            arrayBuffer = buffer.getBuffer();
            listener.update();
        }
    }

    public interface Int8ArrayNativeListener {
        Int8ArrayWrapper recreateBuffer();
        void update();
    }
}