package com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray;

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
                return getUint8Array(buffer);
            }
            else {
                return getInt8Array(buffer);
            }
        }
        else if(buffer instanceof ShortBuffer) {
            if(isUnsigned) {
                return getUint16Array(buffer);
            }
            else {
                return getInt16Array(buffer);
            }
        }
        else if(buffer instanceof IntBuffer) {
            return getInt32Array(buffer);
        }
        else if(buffer instanceof FloatBuffer) {
            return getFloat32Array(buffer);
        }
        throw new GdxRuntimeException("No support for buffer " + buffer.getClass());
    }

    public static Int8Array getInt8Array(Buffer buff) {
        if(PlatformDetector.isJavaScript() || buff.isDirect()) {
            return Int8Array.fromJavaBuffer(buff);
        }
        else {
            if(buff instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer)buff;
                if(buffer.hasArray()) {
                    return Int8Array.copyFromJavaArray(buffer.array());
                }
                else {
                    int position = buffer.position();
                    int limit = buffer.limit();
                    int capacity = buffer.capacity();
                    buffer.position(0);
                    buffer.limit(capacity);
                    var array = new byte[capacity];
                    buffer.get(array);
                    buffer.position(position);
                    buffer.limit(limit);
                    return Int8Array.copyFromJavaArray(array);
                }
            }
            else {
                ArrayBufferView typedArray = getTypedArray(false, buff);
                return new Int8Array(typedArray.getBuffer());
            }
        }
    }

    public static Uint8Array getUint8Array(Buffer buff) {
        if(PlatformDetector.isJavaScript() || buff.isDirect()) {
            return Uint8Array.fromJavaBuffer(buff);
        }
        else {
            if(buff instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer)buff;
                 if(buffer.hasArray()) {
                    var typedArray = Int8Array.copyFromJavaArray(buffer.array());
                    return new Uint8Array(typedArray.getBuffer());
                }
                else {
                     int position = buffer.position();
                     int limit = buffer.limit();
                     int capacity = buffer.capacity();
                     buffer.position(0);
                     buffer.limit(capacity);
                     var array = new byte[capacity];
                     buffer.get(array);
                     buffer.position(position);
                     buffer.limit(limit);
                    var typedArray = Int8Array.copyFromJavaArray(array);
                    return new Uint8Array(typedArray.getBuffer());
                }
            }
            else {
                ArrayBufferView typedArray = getTypedArray(true, buff);
                return new Uint8Array(typedArray.getBuffer());
            }
        }
    }

    public static Int16Array getInt16Array(Buffer buff) {
        if(PlatformDetector.isJavaScript() || buff.isDirect()) {
            return Int16Array.fromJavaBuffer(buff);
        }
        else {
            if(buff instanceof ShortBuffer) {
                ShortBuffer buffer = (ShortBuffer)buff;
                if(buffer.hasArray()) {
                    return Int16Array.copyFromJavaArray(buffer.array());
                }
                else {
                    int position = buffer.position();
                    int limit = buffer.limit();
                    int capacity = buffer.capacity();
                    buffer.position(0);
                    buffer.limit(capacity);
                    var array = new short[buffer.capacity()];
                    buffer.get(array);
                    buffer.position(position);
                    buffer.limit(limit);
                    return Int16Array.copyFromJavaArray(array);
                }
            }
            else if(buff instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer)buff;
                Int8Array array = getInt8Array(buffer);
                return new Int16Array(array);
            }
            else {
                throw new RuntimeException("TypedArrays#getInt16Array - Unsupported buffer type " + buff.getClass().getSimpleName());
            }
        }
    }

    public static Uint16Array getUint16Array(Buffer buff) {
        if(PlatformDetector.isJavaScript() || buff.isDirect()) {
            return Uint16Array.fromJavaBuffer(buff);
        }
        else {
            if(buff instanceof ShortBuffer) {
                ShortBuffer buffer = (ShortBuffer)buff;
                if(buffer.hasArray()) {
                    var typedArray = Int16Array.copyFromJavaArray(buffer.array());
                    return new Uint16Array(typedArray.getBuffer());
                }
                else {
                    int position = buffer.position();
                    int limit = buffer.limit();
                    int capacity = buffer.capacity();
                    buffer.position(0);
                    buffer.limit(capacity);
                    var array = new short[buffer.capacity()];
                    buffer.get(array);
                    buffer.position(position);
                    buffer.limit(limit);
                    var typedArray = Int16Array.copyFromJavaArray(array);
                    return new Uint16Array(typedArray.getBuffer());
                }
            }
            else if(buff instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer)buff;
                Uint8Array array = getUint8Array(buffer);
                return new Uint16Array(array.getBuffer());
            }
            else {
                throw new RuntimeException("TypedArrays#getUint16Array - Unsupported buffer type " + buff.getClass().getSimpleName());
            }
        }
    }

    public static Int32Array getInt32Array(Buffer buff) {
        if(PlatformDetector.isJavaScript() || buff.isDirect()) {
            return Int32Array.fromJavaBuffer(buff);
        }
        else {
            if(buff instanceof IntBuffer) {
                IntBuffer buffer = (IntBuffer)buff;
                if(buffer.hasArray()) {
                    return Int32Array.copyFromJavaArray(buffer.array());
                }
                else {
                    int position = buffer.position();
                    int limit = buffer.limit();
                    int capacity = buffer.capacity();
                    buffer.position(0);
                    buffer.limit(capacity);
                    var array = new int[buffer.capacity()];
                    buffer.get(array);
                    buffer.position(position);
                    buffer.limit(limit);
                    return Int32Array.copyFromJavaArray(array);
                }
            }
            else if(buff instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer)buff;
                Int8Array array = getInt8Array(buffer);
                return new Int32Array(array);
            }
            else {
                throw new RuntimeException("TypedArrays#getInt32Array - Unsupported buffer type " + buff.getClass().getSimpleName());
            }
        }
    }

    public static Float32Array getFloat32Array(Buffer buff) {
        if(PlatformDetector.isJavaScript() || buff.isDirect()) {
            return Float32Array.fromJavaBuffer(buff);
        }
        else {
            if(buff instanceof FloatBuffer) {
                FloatBuffer buffer = (FloatBuffer)buff;
                if(buffer.hasArray()) {
                    return Float32Array.copyFromJavaArray(buffer.array());
                }
                else {
                    int position = buffer.position();
                    int limit = buffer.limit();
                    int capacity = buffer.capacity();
                    buffer.position(0);
                    buffer.limit(capacity);
                    var array = new float[buffer.capacity()];
                    buffer.get(array);
                    buffer.position(position);
                    buffer.limit(limit);
                    return Float32Array.copyFromJavaArray(array);
                }
            }
            else if(buff instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer)buff;
                Int8Array array = getInt8Array(buffer);
                return new Float32Array(array);
            }
            else {
                throw new RuntimeException("TypedArrays#getFloat32Array - Unsupported buffer type " + buff.getClass().getSimpleName());
            }
        }
    }

    public static Int8Array getInt8Array(byte[] buffer) {
        return Int8Array.copyFromJavaArray(buffer);
    }

    public static void copy(Int8Array in, ByteBuffer out) {
        if(PlatformDetector.isJavaScript() || out.isDirect()) {
            Int8Array array = Int8Array.fromJavaBuffer(out);
            array.set(in);
        }
        else {
            var data = in.copyToJavaArray();
            out.put(data);
        }
    }
}
