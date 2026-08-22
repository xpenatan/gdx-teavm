package com.github.xpenatan.gdx.teavm.backends.web;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.PlatformDetector;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Prepares NIO buffer ranges for WebGL uploads without changing the source buffer state.
 */
final class WebGLBufferUpload {
    private RangeCache rangeCache;

    ArrayBufferView getWebGL1View(Buffer buffer) {
        int relativeByteOffset = getRangeElementOffset(buffer) * getElementSize(buffer);
        int byteLength = getRangeElementLength(buffer) * getElementSize(buffer);
        boolean cacheable = isCacheable(buffer);

        // The cache is checked before asking TeaVM for another full typed-array wrapper. This keeps a stable
        // partial range allocation-free after its first WebGL 1 upload. Detached Wasm views report byteLength 0
        // and are rebuilt below after linear memory grows.
        if(cacheable && byteLength > 0 && isPartialRange(buffer) && rangeCache != null) {
            ArrayBufferView cached = rangeCache.get(buffer, relativeByteOffset, byteLength);
            if(cached != null) {
                return cached;
            }
        }

        ArrayBufferView typedArray = getTypedArray(buffer);
        int byteOffset = typedArray.getByteOffset() + relativeByteOffset;
        if(typedArray.getByteOffset() == byteOffset && typedArray.getByteLength() == byteLength) {
            return typedArray;
        }

        Int8Array range = new Int8Array(typedArray.getBuffer(), byteOffset, byteLength);
        if(cacheable && byteLength > 0) {
            if(rangeCache == null) {
                rangeCache = new RangeCache();
            }
            rangeCache.put(buffer, relativeByteOffset, byteLength, range);
        }
        return range;
    }

    static ArrayBufferView getTypedArray(Buffer buffer) {
        return TypedArrays.getTypedArray(getConversionBuffer(buffer));
    }

    static int getRangeElementOffset(Buffer buffer) {
        int offset = buffer.position();
        // JavaScript and direct views already begin at the logical buffer's base. The Wasm heap fallback copies
        // the complete backing array, so a sliced heap buffer also needs its array offset.
        if(!PlatformDetector.isJavaScript() && !buffer.isDirect()) {
            offset += getArrayOffset(buffer);
        }
        return offset;
    }

    static int getRangeElementLength(Buffer buffer) {
        return buffer.remaining();
    }

    private static boolean isPartialRange(Buffer buffer) {
        return buffer.position() != 0 || buffer.limit() != buffer.capacity();
    }

    private static boolean isCacheable(Buffer buffer) {
        // These are zero-copy views. Wasm heap buffers are intentionally excluded because their conversion makes
        // a new backing copy whose contents must not be cached across uploads.
        return PlatformDetector.isJavaScript() || buffer.isDirect();
    }

    private static Buffer getConversionBuffer(Buffer buffer) {
        if(PlatformDetector.isJavaScript() || buffer.isDirect() || hasArray(buffer)) {
            return buffer;
        }

        // The Wasm heap fallback for views without an accessible array temporarily rewinds its input while
        // copying. Converting a duplicate preserves the caller's position, limit, and mark.
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
        throw unsupportedBuffer(buffer);
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
        throw unsupportedBuffer(buffer);
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
        throw unsupportedBuffer(buffer);
    }

    private static int getElementSize(Buffer buffer) {
        if(buffer instanceof ByteBuffer) {
            return Byte.BYTES;
        }
        else if(buffer instanceof ShortBuffer) {
            return Short.BYTES;
        }
        else if(buffer instanceof IntBuffer) {
            return Integer.BYTES;
        }
        else if(buffer instanceof FloatBuffer) {
            return Float.BYTES;
        }
        throw unsupportedBuffer(buffer);
    }

    private static GdxRuntimeException unsupportedBuffer(Buffer buffer) {
        return new GdxRuntimeException("No support for buffer " + buffer.getClass());
    }

    /**
     * A bounded four-way set-associative cache. It neither grows indefinitely nor shifts entries on a hit/miss.
     * The logical Buffer identity is part of the key because Wasm direct buffers share linear memory.
     */
    private static final class RangeCache {
        private static final int SET_COUNT = 64;
        private static final int WAY_COUNT = 4;
        private static final int SET_MASK = SET_COUNT - 1;

        private final Buffer[] buffers = new Buffer[SET_COUNT * WAY_COUNT];
        private final int[] byteOffsets = new int[SET_COUNT * WAY_COUNT];
        private final int[] byteLengths = new int[SET_COUNT * WAY_COUNT];
        private final JSArray<Int8Array> ranges = new JSArray<>(SET_COUNT * WAY_COUNT);
        private final int[] nextWays = new int[SET_COUNT];

        ArrayBufferView get(Buffer buffer, int byteOffset, int byteLength) {
            int firstSlot = getFirstSlot(buffer, byteOffset, byteLength);
            for(int way = 0; way < WAY_COUNT; way++) {
                int slot = firstSlot + way;
                if(buffers[slot] == buffer
                        && byteOffsets[slot] == byteOffset
                        && byteLengths[slot] == byteLength) {
                    Int8Array range = ranges.get(slot);
                    if(range != null && range.getByteLength() == byteLength) {
                        return range;
                    }

                    // A Wasm memory growth detached this view. Free the slot so the current memory can replace it.
                    buffers[slot] = null;
                    ranges.set(slot, null);
                    return null;
                }
            }
            return null;
        }

        void put(Buffer buffer, int byteOffset, int byteLength, Int8Array range) {
            int firstSlot = getFirstSlot(buffer, byteOffset, byteLength);
            int slot = -1;
            for(int way = 0; way < WAY_COUNT; way++) {
                int candidate = firstSlot + way;
                if(buffers[candidate] == null) {
                    slot = candidate;
                    break;
                }
            }

            if(slot < 0) {
                int set = firstSlot / WAY_COUNT;
                int nextWay = nextWays[set];
                slot = firstSlot + nextWay;
                nextWays[set] = (nextWay + 1) & (WAY_COUNT - 1);
            }

            buffers[slot] = buffer;
            byteOffsets[slot] = byteOffset;
            byteLengths[slot] = byteLength;
            ranges.set(slot, range);
        }

        private static int getFirstSlot(Buffer buffer, int byteOffset, int byteLength) {
            int hash = System.identityHashCode(buffer);
            hash = 31 * hash + byteOffset;
            hash = 31 * hash + byteLength;
            hash ^= hash >>> 16;
            return (hash & SET_MASK) * WAY_COUNT;
        }
    }
}
