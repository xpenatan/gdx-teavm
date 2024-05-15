package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.java.nio.HasArrayBufferView;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.Uint8Array;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

/**
 * @author xpenatan
 */
public class TypedArrays {

    public static Float32ArrayWrapper createFloat32Array(int length) {
        Float32Array create = Float32Array.create(length);
        return (Float32ArrayWrapper)create;
    }

    public static Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Float32Array create = Float32Array.create(arrayBuffer);
        return (Float32ArrayWrapper)create;
    }

    public static Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Float32Array create = Float32Array.create(arrayBuffer, offset);
        return (Float32ArrayWrapper)create;
    }

    public static Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Float32Array create = Float32Array.create(arrayBuffer, offset, length);
        return (Float32ArrayWrapper)create;
    }

    public static Int32ArrayWrapper createInt32Array(int length) {
        Int32Array create = Int32Array.create(length);
        return (Int32ArrayWrapper)create;
    }

    public static Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int32Array create = Int32Array.create(arrayBuffer);
        return (Int32ArrayWrapper)create;
    }

    public static Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int32Array create = Int32Array.create(arrayBuffer, offset);
        return (Int32ArrayWrapper)create;
    }

    public static Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int32Array create = Int32Array.create(arrayBuffer, offset, length);
        return (Int32ArrayWrapper)create;
    }

    public static Int16ArrayWrapper createInt16Array(int length) {
        Int16Array create = Int16Array.create(length);
        return (Int16ArrayWrapper)create;
    }

    public static Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int16Array create = Int16Array.create(arrayBuffer);
        return (Int16ArrayWrapper)create;
    }

    public static Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int16Array create = Int16Array.create(arrayBuffer, offset);
        return (Int16ArrayWrapper)create;
    }

    public static Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int16Array create = Int16Array.create(arrayBuffer, offset, length);
        return (Int16ArrayWrapper)create;
    }

    public static Int8ArrayWrapper createInt8Array(int length) {
        Int8Array create = Int8Array.create(length);
        return (Int8ArrayWrapper)create;
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int8Array create = Int8Array.create(arrayBuffer);
        return (Int8ArrayWrapper)create;
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int8Array create = Int8Array.create(arrayBuffer, offset);
        return (Int8ArrayWrapper)create;
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        Int8Array create = Int8Array.create(arrayBuffer, offset, length);
        return (Int8ArrayWrapper)create;
    }

    public static Uint8ClampedArrayWrapper createUint8ClampedArray(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ClampedArrayWrapper)Uint8ClampedArray.create(arrayBuffer);
    }

    public static Uint8ClampedArrayWrapper createUint8ClampedArray(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ClampedArrayWrapper)Uint8ClampedArray.create(arrayBuffer, offset);
    }

    public static Uint8ClampedArrayWrapper createUint8ClampedArray(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ClampedArrayWrapper)Uint8ClampedArray.create(arrayBuffer, offset, length);
    }

    public static Uint8ArrayWrapper createUint8Array(int length) {
        Uint8Array create = Uint8Array.create(length);
        return (Uint8ArrayWrapper)create;
    }

    public static Uint8ArrayWrapper createUint8Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ArrayWrapper)Uint8Array.create(arrayBuffer, offset);
    }

    public static Uint8ArrayWrapper createUint8Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ArrayWrapper)Uint8Array.create(arrayBuffer, offset, length);
    }

    public static Uint32ArrayWrapper createUint32Array(int length) {
        return Uint32ArrayWrapper.create(length);
    }

    public static Uint32ArrayWrapper createUint32Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return Uint32ArrayWrapper.create(arrayBuffer, offset, length);
    }

    @JSBody(params = { "array" }, script = "return new $rt_byteArrayCls(array);")
    private static native Object toByteArrayInternal(ArrayBufferViewWrapper array);

    // Obtain the array reference from ArrayBufferView
    public static byte[] toByteArray(ArrayBufferViewWrapper array) {
        Object arrayObj = TypedArrays.toByteArrayInternal(array);
        byte[] byteArray = (byte[])arrayObj;
        return byteArray;
    }

    public static ArrayBufferViewWrapper getTypedArray(ByteBuffer buffer) {
        ArrayBufferViewWrapper bufferView = getInt8Array(buffer);
        if(bufferView != null) {
            return bufferView;
        }
        byte[] array = buffer.array();
        ArrayBufferViewWrapper arrayBuffer = getTypedArray(array);
        return arrayBuffer;
    }

    public static ArrayBufferViewWrapper getTypedArray(ShortBuffer buffer) {
        ArrayBufferViewWrapper bufferView = getInt8Array(buffer);
        if(bufferView != null) {
            return bufferView;
        }
        short[] array = buffer.array();
        ArrayBufferViewWrapper arrayBuffer = getTypedArray(array);
        return arrayBuffer;
    }

    public static ArrayBufferViewWrapper getTypedArray(IntBuffer buffer) {
        ArrayBufferViewWrapper bufferView = getInt8Array(buffer);
        if(bufferView != null) {
            return bufferView;
        }
        int[] array = buffer.array();
        ArrayBufferViewWrapper arrayBuffer = getTypedArray(array);
        return arrayBuffer;
    }

    public static ArrayBufferViewWrapper getTypedArray(FloatBuffer buffer) {
        ArrayBufferViewWrapper bufferView = getInt8Array(buffer);
        if(bufferView != null) {
            return bufferView;
        }
        float[] array = buffer.array();
        ArrayBufferViewWrapper arrayBuffer = getTypedArray(array);
        return arrayBuffer;
    }

    @JSBody(params = {"buffer"}, script = "" +
            "return buffer;")
    private static native ArrayBufferViewWrapper getTypedArray(@JSByRef() byte[] buffer);

    @JSBody(params = {"buffer"}, script = "" +
            "return buffer;")
    private static native ArrayBufferViewWrapper getTypedArray(@JSByRef() int[] buffer);

    @JSBody(params = {"buffer"}, script = "" +
            "return buffer;")
    private static native ArrayBufferViewWrapper getTypedArray(@JSByRef() float[] buffer);

    @JSBody(params = {"buffer"}, script = "" +
            "return buffer;")
    private static native ArrayBufferViewWrapper getTypedArray(@JSByRef() short[] buffer);

    @org.teavm.jso.JSBody(params = {"array"}, script = "" +
            "return array.data;")
    public static native Int8ArrayWrapper getArrayBufferView(JSObject array);

    public static Int8ArrayWrapper getInt8Array(Buffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return ((HasArrayBufferView)buffer).getTypedArray();
        }
        return null;
    }
}
