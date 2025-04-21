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
            var array = new byte[buffer.capacity()];
            buffer.get(0, array);
            return Int8Array.copyFromJavaArray(buffer.array());
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
            var array = new byte[buffer.capacity()];
            buffer.get(0, array);
            var typedArray = Int8Array.copyFromJavaArray(buffer.array());
            return new Uint8Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
            var array = new short[buffer.capacity()];
            buffer.get(0, array);
            return Int16Array.copyFromJavaArray(buffer.array());
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
            var array = new short[buffer.capacity()];
            buffer.get(0, array);
            var typedArray = Int16Array.copyFromJavaArray(buffer.array());
            return new Uint16Array(typedArray.getBuffer(), buffer.arrayOffset(), buffer.capacity());
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
            var array = new int[buffer.capacity()];
            buffer.get(0, array);
            return Int32Array.copyFromJavaArray(array);
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
            var array = new float[buffer.capacity()];
            buffer.get(0, array);
            return Float32Array.copyFromJavaArray(array);
        }
    }

    public static Int8Array getTypedArray(byte[] buffer) {
        return Int8Array.copyFromJavaArray(buffer);
    }

    public static void copy(Int8Array in, ByteBuffer out) {
        if(PlatformDetector.isJavaScript() || out.isDirect()) {
            Int8Array array = Int8Array.fromJavaBuffer(out);
            array.set(in);
        }
        else {
            var data = in.copyToJavaArray();
            out.put(0, data);
        }
    }
}
