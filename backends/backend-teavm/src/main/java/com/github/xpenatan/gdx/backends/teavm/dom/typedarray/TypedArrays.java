package com.github.xpenatan.gdx.backends.teavm.dom.typedarray;

import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.PlatformDetector;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int16Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Int8Array;
import org.teavm.jso.typedarrays.TypedArray;
import org.teavm.jso.typedarrays.Uint16Array;
import org.teavm.jso.typedarrays.Uint8Array;

/**
 * @author xpenatan
 */
public class TypedArrays {

    // Obtain the array reference from ArrayBufferView
    public static byte[] toByteArray(TypedArray array) {
        Int8Array intArray = new Int8Array(array);
        return intArray.copyToJavaArray();
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
        else if(buffer.hasArray()) {
            var typedArray = Int8Array.copyFromJavaArray(buffer.array());
            return new Int8Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
        else if(buffer.hasArray()) {
            var typedArray = Int8Array.copyFromJavaArray(buffer.array());
            return new Uint8Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
        else if(buffer.hasArray()) {
            var typedArray = Int16Array.copyFromJavaArray(buffer.array());
            return new Int16Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
        else if(buffer.hasArray()) {
            var typedArray = Int16Array.copyFromJavaArray(buffer.array());
            return new Uint16Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
        else if(buffer.hasArray()) {
            var typedArray = Int32Array.copyFromJavaArray(buffer.array());
            return new Int32Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
        else if(buffer.hasArray()) {
            var typedArray = Float32Array.copyFromJavaArray(buffer.array());
            return new Float32Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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

    public static void copy(Int8Array in, ByteBuffer out) {
        int length = in.getLength();
        for(int i = 0; i < length; i++) {
            byte value = in.get(i);
            out.put(i, value);
        }
    }
}
