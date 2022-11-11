package com.badlogic.gdx.graphics;

import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Uint8ClampedArrayWrapper;
import java.nio.ByteBuffer;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class FreeTypeUtil {

    public static ByteBuffer newDirectReadWriteByteBuffer() {
        return ByteBuffer.allocate(0);
    }

    public static ByteBuffer newDirectReadWriteByteBuffer(Uint8ClampedArrayWrapper int8ArrayWrapper) {
        int byteLength = int8ArrayWrapper.getByteLength();
        byte[] copyBytes = new byte[byteLength];
        for(int i = 0; i < byteLength; i++) {
            byte b = int8ArrayWrapper.get(i);
            copyBytes[i] = b;
        }
        return ByteBuffer.wrap(copyBytes);
    }

    public static ByteBuffer newDirectReadWriteByteBuffer(Int8ArrayWrapper int8ArrayWrapper, int capacity, int arrayOffset) {
        int byteLength = int8ArrayWrapper.getByteLength();
        byte[] copyBytes = new byte[byteLength];
        for(int i = 0; i < byteLength; i++) {
            byte b = int8ArrayWrapper.get(i);
            copyBytes[i] = b;
        }
        return ByteBuffer.wrap(copyBytes, arrayOffset, capacity);
    }

    public static ArrayBufferViewWrapper getTypedArray(ByteBuffer buffer) {
        return getTypedArray((JSObject)buffer);
    }

    @JSBody(params = {"buffer"}, script = "" +
            "var typedArray = buffer.$array0.data;" +
            "return typedArray;")
    public static native ArrayBufferViewWrapper getTypedArray(JSObject buffer);
}