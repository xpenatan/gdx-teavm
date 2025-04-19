package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.PlatformDetector;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.TypedArray;
import org.teavm.jso.typedarrays.Uint16Array;
import org.teavm.jso.typedarrays.Uint8Array;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

/**
 * @author xpenatan
 */
public class TypedArrays {

    public static Float32Array createFloat32Array(int length) {
        return new Float32Array(length);
    }

    public static Float32Array createFloat32Array(ArrayBuffer buffer) {
        return new Float32Array(buffer);
    }

    public static Float32Array createFloat32Array(ArrayBuffer buffer, int offset) {
        return new Float32Array(buffer, offset);
    }

    public static Float32Array createFloat32Array(ArrayBuffer buffer, int offset, int length) {
        return new Float32Array(buffer, offset, length);
    }

    public static Int32Array createInt32Array(int length) {
        return new Int32Array(length);
    }

    public static Int32Array createInt32Array(ArrayBuffer buffer) {
        return new Int32Array(buffer);
    }

    public static Int32Array createInt32Array(ArrayBuffer buffer, int offset) {
        return new Int32Array(buffer, offset);
    }

    public static Int32Array createInt32Array(ArrayBuffer buffer, int offset, int length) {
        return new Int32Array(buffer, offset, length);
    }

    public static Int16Array createInt16Array(int length) {
        return new Int16Array(length);
    }

    public static Int16Array createInt16Array(ArrayBuffer buffer) {
        return new Int16Array(buffer);
    }

    public static Int16Array createInt16Array(ArrayBuffer buffer, int offset) {
        return new Int16Array(buffer, offset);
    }

    public static Int16Array createInt16Array(ArrayBuffer buffer, int offset, int length) {
        return new Int16Array(buffer, offset, length);
    }

    public static Int8Array createInt8Array(int length) {
        return new Int8Array(length);
    }

    public static Int8Array createInt8Array(ArrayBuffer buffer) {
        return new Int8Array(buffer);
    }

    public static Int8Array createInt8Array(ArrayBuffer buffer, int offset) {
        return new Int8Array(buffer, offset);
    }

    public static Int8Array createInt8Array(ArrayBuffer buffer, int offset, int length) {
        return new Int8Array(buffer, offset, length);
    }

    public static Int8Array createInt8Array(TypedArray buffer) {
        return new Int8Array(buffer);
    }

    public static Uint8ClampedArray createUint8ClampedArray(ArrayBuffer buffer) {
        return new Uint8ClampedArray(buffer);
    }

    public static Uint8ClampedArray createUint8ClampedArray(ArrayBuffer buffer, int offset) {
        return new Uint8ClampedArray(buffer, offset);
    }

    public static Uint8ClampedArray createUint8ClampedArray(ArrayBuffer buffer, int offset, int length) {
        return new Uint8ClampedArray(buffer, offset, length);
    }

    public static Uint8Array createUint8Array(int length) {
        return new Uint8Array(length);
    }

    public static Uint8Array createUint8Array(ArrayBuffer buffer, int offset) {
        return new Uint8Array(buffer, offset);
    }

    public static Uint8Array createUint8Array(ArrayBuffer buffer, int offset, int length) {
        return new Uint8Array(buffer, offset, length);
    }

    public static Uint16Array createUint16Array(int length) {
        return new Uint16Array(length);
    }

    public static Uint16Array createUint16Array(ArrayBuffer buffer, int offset) {
        return new Uint16Array(buffer, offset);
    }

    public static Uint16Array createUint16Array(ArrayBuffer buffer, int offset, int length) {
        return new Uint16Array(buffer, offset, length);
    }

    // Obtain the array reference from ArrayBufferView
    public static byte[] toByteArray(TypedArray array) {
        int length = array.getLength();
        byte[] newByte = new byte[length];
        if(array instanceof Int8Array) {
            Int8Array arr = (Int8Array)array;
            for(int i = 0; i < length; i++) {
                byte value = arr.get(i);
                newByte[i] = value;
            }
        }
        else {
            Int8Array arr = new Int8Array(array);
            for(int i = 0; i < length; i++) {
                byte value = arr.get(i);
                newByte[i] = value;
            }
        }
        return newByte;
    }

    public static ArrayBufferView getTypedArray(Buffer buffer) {
        return getTypedArray(false, buffer);
    }

    public static ArrayBufferView getTypedArray(boolean isUnsigned, Buffer buffer) {
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

    public static Int8Array getTypedArray(ByteBuffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect()) {
            return Int8Array.fromJavaBuffer(buffer);
        }
        else {
            Int8Array array = new Int8Array(buffer.limit());
            for(int i = buffer.position(), j = 0; i < buffer.limit(); i++, j++) {
                array.set(j, buffer.get(i));
            }
            return array;
        }
    }

    public static Uint8Array getUTypedArray(ByteBuffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect()) {
            return Uint8Array.fromJavaBuffer(buffer);
        }
        else {
            Uint8Array array = new Uint8Array(buffer.limit());
            for(int i = buffer.position(), j = 0; i < buffer.limit(); i++, j++) {
                array.set(j, buffer.get(i));
            }
            return array;
        }
    }

    public static Int16Array getTypedArray(ShortBuffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect()) {
            return Int16Array.fromJavaBuffer(buffer);
        }
        else {
            Int16Array array = new Int16Array(buffer.limit());
            for(int i = buffer.position(), j = 0; i < buffer.limit(); i++, j++) {
                array.set(j, buffer.get(i));
            }
            return array;
        }
    }

    public static Uint16Array getUTypedArray(ShortBuffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect()) {
            return Uint16Array.fromJavaBuffer(buffer);
        }
        else {
            Uint16Array array = new Uint16Array(buffer.limit());
            for(int i = buffer.position(), j = 0; i < buffer.limit(); i++, j++) {
                array.set(j, buffer.get(i));
            }
            return array;
        }
    }

    public static Int32Array getTypedArray(IntBuffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect()) {
            return Int32Array.fromJavaBuffer(buffer);
        }
        else {
            Int32Array array = new Int32Array(buffer.limit());
            for(int i = buffer.position(), j = 0; i < buffer.limit(); i++, j++) {
                array.set(j, buffer.get(i));
            }
            return array;
        }
    }

    public static Float32Array getTypedArray(FloatBuffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect()) {
            return Float32Array.fromJavaBuffer(buffer);
        }
        else {
            Float32Array array = new Float32Array(buffer.limit());
            for(int i = buffer.position(), j = 0; i < buffer.limit(); i++, j++) {
                array.set(j, buffer.get(i));
            }
            return array;
        }
    }

    public static Int8Array getTypedArray(byte[] buffer) {
        return Int8Array.copyFromJavaArray(buffer);
    }

    @JSBody(
            params = {"array"},
            script = "return array;"
    )
    private static native Uint16Array copyFromJavaArray(@JSByRef(optional = true) short[] var0);

    @JSBody(
            params = {"typedArray"},
            script = "return typedArray.buffer.detached;"
    )
    public static native boolean isDetached(TypedArray typedArray);
}
