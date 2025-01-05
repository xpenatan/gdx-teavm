package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.Uint16Array;
import org.teavm.jso.typedarrays.Uint8Array;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

/**
 * @author xpenatan
 */
public class TypedArrays {

    public static Float32ArrayWrapper createFloat32Array(int length) {
        return (Float32ArrayWrapper)new Float32Array(length);
    }

    public static Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Float32ArrayWrapper)new Float32Array(arrayBuffer);
    }

    public static Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Float32ArrayWrapper)new Float32Array(arrayBuffer, offset);
    }

    public static Float32ArrayWrapper createFloat32Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Float32ArrayWrapper)new Float32Array(arrayBuffer, offset, length);
    }

    public static Int32ArrayWrapper createInt32Array(int length) {
        return (Int32ArrayWrapper)new Int32Array(length);
    }

    public static Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int32ArrayWrapper)new Int32Array(arrayBuffer);
    }

    public static Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int32ArrayWrapper)new Int32Array(arrayBuffer, offset);
    }

    public static Int32ArrayWrapper createInt32Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int32ArrayWrapper)new Int32Array(arrayBuffer, offset, length);
    }

    public static Int16ArrayWrapper createInt16Array(int length) {
        return (Int16ArrayWrapper)new Int16Array(length);
    }

    public static Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int16ArrayWrapper)new Int16Array(arrayBuffer);
    }

    public static Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int16ArrayWrapper)new Int16Array(arrayBuffer, offset);
    }

    public static Int16ArrayWrapper createInt16Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int16ArrayWrapper)new Int16Array(arrayBuffer, offset, length);
    }

    public static Int8ArrayWrapper createInt8Array(int length) {
        return (Int8ArrayWrapper)new Int8Array(length);
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int8ArrayWrapper)new Int8Array(arrayBuffer);
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int8ArrayWrapper)new Int8Array(arrayBuffer, offset);
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Int8ArrayWrapper)new Int8Array(arrayBuffer, offset, length);
    }

    public static Int8ArrayWrapper createInt8Array(ArrayBufferViewWrapper buffer) {
        ArrayBufferView arrayBuffer = (ArrayBufferView)buffer;
        return (Int8ArrayWrapper)new Int8Array(arrayBuffer);
    }

    public static Uint8ClampedArrayWrapper createUint8ClampedArray(ArrayBufferWrapper buffer) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ClampedArrayWrapper)new Uint8ClampedArray(arrayBuffer);
    }

    public static Uint8ClampedArrayWrapper createUint8ClampedArray(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ClampedArrayWrapper)new Uint8ClampedArray(arrayBuffer, offset);
    }

    public static Uint8ClampedArrayWrapper createUint8ClampedArray(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ClampedArrayWrapper)new Uint8ClampedArray(arrayBuffer, offset, length);
    }

    public static Uint8ArrayWrapper createUint8Array(int length) {
        return (Uint8ArrayWrapper)new Uint8Array(length);
    }

    public static Uint8ArrayWrapper createUint8Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ArrayWrapper)new Uint8Array(arrayBuffer, offset);
    }

    public static Uint8ArrayWrapper createUint8Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ArrayWrapper)new Uint8Array(arrayBuffer, offset, length);
    }

    public static Uint16ArrayWrapper createUint16Array(int length) {
        return (Uint16ArrayWrapper)new Uint16Array(length);
    }

    public static Uint8ArrayWrapper createUint16Array(ArrayBufferWrapper buffer, int offset) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint8ArrayWrapper)new Uint16Array(arrayBuffer, offset);
    }

    public static Uint16ArrayWrapper createUint16Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return (Uint16ArrayWrapper)new Uint16Array(arrayBuffer, offset, length);
    }

    public static Uint32ArrayWrapper createUint32Array(int length) {
        return Uint32ArrayWrapper.create(length);
    }

    public static Uint32ArrayWrapper createUFloat32Array(int length) {
        return Uint32ArrayWrapper.create(length);
    }

    public static Uint32ArrayWrapper createUint32Array(ArrayBufferWrapper buffer, int offset, int length) {
        ArrayBuffer arrayBuffer = (ArrayBuffer)buffer;
        return Uint32ArrayWrapper.create(arrayBuffer, offset, length);
    }


    // Obtain the array reference from ArrayBufferView
    public static byte[] toByteArray(ArrayBufferViewWrapper array) {
        Int8Array intArray = new Int8Array((ArrayBufferView)array);
        int length = intArray.getLength();
        byte[] newByte = new byte[length];

        for(int i = 0; i < length; i++) {
            newByte[i] = intArray.get(i);
        }
        return newByte;
    }

    public static ArrayBufferViewWrapper getTypedArray(boolean isUnsigned, Buffer buffer) {
        if(buffer instanceof ByteBuffer) {
            if(isUnsigned) {
                return getUTypedArray((ByteBuffer)buffer);
            }
            else {
                return getTypedArray((ByteBuffer)buffer);
            }
        }
        else if(buffer instanceof ShortBuffer) {
            if(isUnsigned) {
                return getUTypedArray((ShortBuffer)buffer);
            }
            else {
                return getTypedArray((ShortBuffer)buffer);
            }
        }
        else if(buffer instanceof IntBuffer) {
            return getTypedArray((IntBuffer)buffer);
        }
        else if(buffer instanceof FloatBuffer) {
            return getTypedArray((FloatBuffer)buffer);
        }
        throw new GdxRuntimeException("No support for buffer " + buffer.getClass());
    }

    public static Int8ArrayWrapper getTypedArray(ByteBuffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return (Int8ArrayWrapper)((HasArrayBufferView)buffer).getArrayBufferView();
        }
        else {
            throw new GdxRuntimeException("Buffer should have ArrayBufferView interface");
        }
    }

    public static Uint8ArrayWrapper getUTypedArray(ByteBuffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return (Uint8ArrayWrapper)((HasArrayBufferView)buffer).getArrayBufferView();
        }
        else {
            throw new GdxRuntimeException("Buffer should have ArrayBufferView interface");
        }
    }

    public static Int16ArrayWrapper getTypedArray(ShortBuffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return (Int16ArrayWrapper)((HasArrayBufferView)buffer).getArrayBufferView();
        }
        else {
            throw new GdxRuntimeException("Buffer should have ArrayBufferView interface");
        }
    }

    public static Uint16ArrayWrapper getUTypedArray(ShortBuffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return (Uint16ArrayWrapper)((HasArrayBufferView)buffer).getArrayBufferView();
        }
        else {
            throw new GdxRuntimeException("Buffer should have ArrayBufferView interface");
        }
    }

    public static Int32ArrayWrapper getTypedArray(IntBuffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return (Int32ArrayWrapper)((HasArrayBufferView)buffer).getArrayBufferView();
        }
        else {
            throw new GdxRuntimeException("Buffer should have ArrayBufferView interface");
        }
    }

    public static Float32ArrayWrapper getTypedArray(FloatBuffer buffer) {
        if(buffer instanceof HasArrayBufferView) {
            return (Float32ArrayWrapper)((HasArrayBufferView)buffer).getArrayBufferView();
        }
        else {
            throw new GdxRuntimeException("Buffer should have ArrayBufferView interface");
        }
    }

    @JSBody(params = {"buffer"}, script = "" +
            "return buffer;")
    public static native Int8Array getTypedByteArray(byte[] buffer);
}
