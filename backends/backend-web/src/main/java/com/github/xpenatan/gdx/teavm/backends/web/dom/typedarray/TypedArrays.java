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

    /**
     * Returns a byte view covering the buffer's active range, from its current position to its limit.
     * The source buffer's position, limit, and mark are not changed.
     */
    public static Int8Array getTypedArrayRange(Buffer buffer) {
        ArrayBufferView typedArray = getTypedArray(getConversionBuffer(buffer));
        return new Int8Array(typedArray.getBuffer(), typedArray.getByteOffset() + getRangeByteOffset(buffer),
                getRangeByteLength(buffer));
    }

    static Buffer getConversionBuffer(Buffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect() || hasArray(buffer)) {
            return buffer;
        }
        // The Wasm-GC fallback for heap views without an accessible array copies data by temporarily rewinding its
        // input. Convert a duplicate so that operation cannot discard the caller's mark.
        if(buffer instanceof ByteBuffer) {
            return ((ByteBuffer)buffer).duplicate();
        }
        else if(buffer instanceof ShortBuffer) {
            return ((ShortBuffer)buffer).duplicate();
        }
        else if(buffer instanceof IntBuffer) {
            return ((IntBuffer)buffer).duplicate();
        }
        else if(buffer instanceof FloatBuffer) {
            return ((FloatBuffer)buffer).duplicate();
        }
        throw new GdxRuntimeException("No support for buffer " + buffer.getClass());
    }

    private static boolean hasArray(Buffer buffer) {
        if(buffer instanceof ByteBuffer) {
            return ((ByteBuffer)buffer).hasArray();
        }
        else if(buffer instanceof ShortBuffer) {
            return ((ShortBuffer)buffer).hasArray();
        }
        else if(buffer instanceof IntBuffer) {
            return ((IntBuffer)buffer).hasArray();
        }
        else if(buffer instanceof FloatBuffer) {
            return ((FloatBuffer)buffer).hasArray();
        }
        throw new GdxRuntimeException("No support for buffer " + buffer.getClass());
    }

    static int getRangeByteOffset(Buffer buffer) {
        int position = buffer.position();
        // JS and direct-buffer views already start at a sliced buffer's base. The heap fallback copies the
        // complete backing array, so its array offset must be included here.
        if(!PlatformDetector.isJavaScript() && !buffer.isDirect()) {
            position += getArrayOffset(buffer);
        }
        return position * getElementSize(buffer);
    }

    static int getRangeByteLength(Buffer buffer) {
        return buffer.remaining() * getElementSize(buffer);
    }

    private static int getElementSize(Buffer buffer) {
        if(buffer instanceof ByteBuffer) {
            return 1;
        }
        else if(buffer instanceof ShortBuffer) {
            return 2;
        }
        else if(buffer instanceof IntBuffer || buffer instanceof FloatBuffer) {
            return 4;
        }
        throw new GdxRuntimeException("No support for buffer " + buffer.getClass());
    }

    private static int getArrayOffset(Buffer buffer) {
        if(buffer instanceof ByteBuffer) {
            ByteBuffer byteBuffer = (ByteBuffer)buffer;
            return byteBuffer.hasArray() ? byteBuffer.arrayOffset() : 0;
        }
        else if(buffer instanceof ShortBuffer) {
            ShortBuffer shortBuffer = (ShortBuffer)buffer;
            return shortBuffer.hasArray() ? shortBuffer.arrayOffset() : 0;
        }
        else if(buffer instanceof IntBuffer) {
            IntBuffer intBuffer = (IntBuffer)buffer;
            return intBuffer.hasArray() ? intBuffer.arrayOffset() : 0;
        }
        else if(buffer instanceof FloatBuffer) {
            FloatBuffer floatBuffer = (FloatBuffer)buffer;
            return floatBuffer.hasArray() ? floatBuffer.arrayOffset() : 0;
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
