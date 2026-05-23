package emu.com.badlogic.gdx.utils;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.impl.nio.Buffers;
import org.teavm.classlib.java.nio.TBuffer;
import org.teavm.classlib.java.nio.TNativeBufferUtil;
import org.teavm.interop.Address;

public final class TBufferUtils {

    private static final int BUFFER_BYTE = 0;
    private static final int BUFFER_CHAR = 1;
    private static final int BUFFER_SHORT = 2;
    private static final int BUFFER_INT = 3;
    private static final int BUFFER_LONG = 4;
    private static final int BUFFER_FLOAT = 5;
    private static final int BUFFER_DOUBLE = 6;

    private static boolean useDirectBuffer = true;

    static Array<ByteBuffer> unsafeBuffers = new Array<ByteBuffer>();
    static int allocatedUnsafe = 0;

    public static void copy(float[] src, Buffer dst, int numFloats, int offset) {
        checkArrayCopy(src, src == null ? 0 : src.length, offset, dst, numFloats);
        int dstKind = requireDestination(dst, BUFFER_FLOAT, "FloatBuffer");
        if (dstKind == BUFFER_BYTE)
            dst.limit(numFloats << 2);
        else
            dst.limit(numFloats);
        dst.position(0);
        copyToBuffer(Address.ofData(src).add(offset << 2), dst, dstKind, 0, numFloats << 2);
        dst.position(0);
    }

    public static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        if (!(dst instanceof ByteBuffer))
            throw new GdxRuntimeException("dst must be a ByteBuffer");
        requireWritable(dst, BUFFER_BYTE);

        int oldPosition = dst.position();
        dst.limit(oldPosition + numElements);
        copyToBuffer(Address.ofData(src).add(srcOffset), dst, BUFFER_BYTE, oldPosition, numElements);
    }

    public static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_SHORT, "ShortBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, true);
        copyToBuffer(Address.ofData(src).add(srcOffset << 1), dst, dstKind, dstByteOffset, numElements << 1);
    }

    public static void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_CHAR, "CharBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, true);
        copyToBuffer(Address.ofData(src).add(srcOffset << 1), dst, dstKind, dstByteOffset, numElements << 1);
    }

    public static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_INT, "IntBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, true);
        copyToBuffer(Address.ofData(src).add(srcOffset << 2), dst, dstKind, dstByteOffset, numElements << 2);
    }

    public static void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_LONG, "LongBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, true);
        copyToBuffer(Address.ofData(src).add(srcOffset << 3), dst, dstKind, dstByteOffset, numElements << 3);
    }

    public static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_FLOAT, "FloatBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, true);
        copyToBuffer(Address.ofData(src).add(srcOffset << 2), dst, dstKind, dstByteOffset, numElements << 2);
    }

    public static void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_DOUBLE, "DoubleBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, true);
        copyToBuffer(Address.ofData(src).add(srcOffset << 3), dst, dstKind, dstByteOffset, numElements << 3);
    }

    public static void copy(char[] src, int srcOffset, int numElements, Buffer dst) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_CHAR, "CharBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, false);
        copyToBuffer(Address.ofData(src).add(srcOffset << 1), dst, dstKind, dstByteOffset, numElements << 1);
    }

    public static void copy(int[] src, int srcOffset, int numElements, Buffer dst) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_INT, "IntBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, false);
        copyToBuffer(Address.ofData(src).add(srcOffset << 2), dst, dstKind, dstByteOffset, numElements << 2);
    }

    public static void copy(long[] src, int srcOffset, int numElements, Buffer dst) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_LONG, "LongBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, false);
        copyToBuffer(Address.ofData(src).add(srcOffset << 3), dst, dstKind, dstByteOffset, numElements << 3);
    }

    public static void copy(float[] src, int srcOffset, int numElements, Buffer dst) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_FLOAT, "FloatBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, false);
        copyToBuffer(Address.ofData(src).add(srcOffset << 2), dst, dstKind, dstByteOffset, numElements << 2);
    }

    public static void copy(double[] src, int srcOffset, int numElements, Buffer dst) {
        checkArrayCopy(src, src == null ? 0 : src.length, srcOffset, dst, numElements);
        int dstKind = requireDestination(dst, BUFFER_DOUBLE, "DoubleBuffer");
        int dstByteOffset = prepareArrayDestination(dst, dstKind, numElements, false);
        copyToBuffer(Address.ofData(src).add(srcOffset << 3), dst, dstKind, dstByteOffset, numElements << 3);
    }

    public static void copy(Buffer src, Buffer dst, int numElements) {
        if (src == null)
            throw new NullPointerException("src");
        if (dst == null)
            throw new NullPointerException("dst");
        if (numElements < 0)
            throw new IndexOutOfBoundsException();

        int srcPos = src.position();
        int dstPos = dst.position();
        int srcKind = bufferKind(src);
        int dstKind = bufferKind(dst);
        if (srcKind != dstKind && srcKind != BUFFER_BYTE && dstKind != BUFFER_BYTE)
            throw new GdxRuntimeException("Buffers must be of same type or ByteBuffer");
        requireWritable(dst, dstKind);

        src.limit(srcPos + numElements);
        dst.limit(dst.capacity());

        int copyBytes = bufferCopyBytes(srcKind, dstKind, numElements);
        int srcByteOffset = bufferByteOffset(srcKind, srcPos);
        int dstByteOffset = bufferByteOffset(dstKind, dstPos);
        int dstLimitBytes = bufferLimitBytes(dst, dstKind);
        if (dstByteOffset < 0 || dstByteOffset + copyBytes > dstLimitBytes)
            throw new IndexOutOfBoundsException();

        Address srcAddress = TNativeBufferUtil.getAddress((TBuffer)(Object)src).add(srcByteOffset);
        Address dstAddress = TNativeBufferUtil.getAddress((TBuffer)(Object)dst).add(dstByteOffset);
        Address.moveMemoryBlock(srcAddress, dstAddress, copyBytes);
        src.position(srcPos);
        dst.position(dstPos);
    }

    private final static FloatBuffer asFloatBuffer(final Buffer data) {
        FloatBuffer buffer = null;
        if(data instanceof ByteBuffer)
            buffer = ((ByteBuffer)data).asFloatBuffer();
        else if(data instanceof FloatBuffer) buffer = (FloatBuffer)data;
        if(buffer == null) throw new GdxRuntimeException("data must be a ByteBuffer or FloatBuffer");
        return buffer;
    }

    private static void checkArrayCopy(Object src, int srcLength, int srcOffset, Buffer dst, int numElements) {
        if (src == null)
            throw new NullPointerException("src");
        if (dst == null)
            throw new NullPointerException("dst");
        if (numElements < 0 || srcOffset < 0 || srcOffset + numElements > srcLength)
            throw new IndexOutOfBoundsException();
    }

    private static int requireDestination(Buffer dst, int kind, String typedName) {
        int dstKind = bufferKind(dst);
        if (dstKind != BUFFER_BYTE && dstKind != kind)
            throw new GdxRuntimeException("dst must be a ByteBuffer or " + typedName);
        requireWritable(dst, dstKind);
        return dstKind;
    }

    private static void requireWritable(Buffer dst, int kind) {
        if (kind == BUFFER_BYTE && ((ByteBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
        if (kind == BUFFER_CHAR && ((CharBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
        if (kind == BUFFER_SHORT && ((ShortBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
        if (kind == BUFFER_INT && ((IntBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
        if (kind == BUFFER_LONG && ((LongBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
        if (kind == BUFFER_FLOAT && ((FloatBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
        if (kind == BUFFER_DOUBLE && ((DoubleBuffer)dst).isReadOnly())
            throw new GdxRuntimeException("dst must not be read-only");
    }

    private static int prepareArrayDestination(Buffer dst, int dstKind, int numElements, boolean updateTypedLimit) {
        int position = dst.position();
        if (updateTypedLimit && dstKind != BUFFER_BYTE)
            dst.limit(position + numElements);
        return bufferByteOffset(dstKind, position);
    }

    private static void copyToBuffer(Address srcAddress, Buffer dst, int dstKind, int dstByteOffset, int numBytes) {
        int dstLimitBytes = bufferLimitBytes(dst, dstKind);
        if (dstByteOffset < 0 || dstByteOffset + numBytes > dstLimitBytes)
            throw new IndexOutOfBoundsException();
        Address dstAddress = TNativeBufferUtil.getAddress((TBuffer)(Object)dst).add(dstByteOffset);
        Address.moveMemoryBlock(srcAddress, dstAddress, numBytes);
    }

    private static int bufferCopyBytes(int srcKind, int dstKind, int numElements) {
        if (srcKind == BUFFER_BYTE && dstKind != BUFFER_BYTE) {
            int elementSize = bufferElementSize(dstKind);
            return (numElements / elementSize) * elementSize;
        }
        return numElements * bufferElementSize(srcKind);
    }

    private static int bufferByteOffset(int kind, int position) {
        return position * bufferElementSize(kind);
    }

    private static int bufferLimitBytes(Buffer buffer, int kind) {
        return buffer.limit() * bufferElementSize(kind);
    }

    private static int bufferKind(Buffer buffer) {
        if (buffer instanceof ByteBuffer)
            return BUFFER_BYTE;
        if (buffer instanceof CharBuffer)
            return BUFFER_CHAR;
        if (buffer instanceof ShortBuffer)
            return BUFFER_SHORT;
        if (buffer instanceof IntBuffer)
            return BUFFER_INT;
        if (buffer instanceof LongBuffer)
            return BUFFER_LONG;
        if (buffer instanceof FloatBuffer)
            return BUFFER_FLOAT;
        if (buffer instanceof DoubleBuffer)
            return BUFFER_DOUBLE;
        throw new GdxRuntimeException("Can't copy to a " + buffer.getClass().getName() + " instance");
    }

    private static int bufferElementSize(int kind) {
        if (kind == BUFFER_BYTE)
            return 1;
        if (kind == BUFFER_CHAR || kind == BUFFER_SHORT)
            return 2;
        if (kind == BUFFER_INT || kind == BUFFER_FLOAT)
            return 4;
        if (kind == BUFFER_LONG || kind == BUFFER_DOUBLE)
            return 8;
        throw new GdxRuntimeException("Unsupported buffer type");
    }

    private final static float[] asFloatArray(final FloatBuffer buffer) {
        final int pos = buffer.position();
        final float[] result = new float[buffer.remaining()];
        buffer.get(result);
        buffer.position(pos);
        return result;
    }

    public static void transform(Buffer data, int dimensions, int strideInBytes, int count, Matrix4 matrix) {
        FloatBuffer buffer = asFloatBuffer(data);
        final int pos = buffer.position();
        int idx = pos;
        float[] arr = asFloatArray(buffer);
        int stride = strideInBytes / 4;
        float[] m = matrix.val;
        for(int i = 0; i < count; i++) {
            final float x = arr[idx];
            final float y = arr[idx + 1];
            final float z = dimensions >= 3 ? arr[idx + 2] : 0f;
            final float w = dimensions >= 4 ? arr[idx + 3] : 1f;
            arr[idx] = x * m[0] + y * m[4] + z * m[8] + w * m[12];
            arr[idx + 1] = x * m[1] + y * m[5] + z * m[9] + w * m[13];
            if(dimensions >= 3) {
                arr[idx + 2] = x * m[2] + y * m[6] + z * m[10] + w * m[14];
                if(dimensions >= 4)
                    arr[idx + 3] = x * m[3] + y * m[7] + z * m[11] + w * m[15];
            }
            idx += stride;
        }
        buffer.put(arr);
        buffer.position(pos);
    }

    public static void transform(Buffer data, int dimensions, int strideInBytes, int count, Matrix3 matrix) {
        FloatBuffer buffer = asFloatBuffer(data);
        // FIXME untested code:
        final int pos = buffer.position();
        int idx = pos;
        float[] arr = asFloatArray(buffer);
        int stride = strideInBytes / 4;
        float[] m = matrix.val;
        for(int i = 0; i < count; i++) {
            final float x = arr[idx];
            final float y = arr[idx + 1];
            final float z = dimensions >= 3 ? arr[idx + 2] : 1f;
            arr[idx] = x * m[0] + y * m[3] + z * m[6];
            arr[idx + 1] = x * m[1] + y * m[4] + z * m[7];
            if(dimensions >= 3)
                arr[idx + 2] = x * m[2] + y * m[5] + z * m[8];
            idx += stride;
        }
        buffer.put(arr);
        buffer.position(pos);
    }

    public static long findFloats(Buffer vertex, int strideInBytes, Buffer vertices, int numVertices) {
        return findFloats(asFloatArray(asFloatBuffer(vertex)), strideInBytes, asFloatArray(asFloatBuffer(vertices)), numVertices);
    }

    public static long findFloats(float[] vertex, int strideInBytes, Buffer vertices, int numVertices) {
        return findFloats(vertex, strideInBytes, asFloatArray(asFloatBuffer(vertices)), numVertices);
    }

    public static long findFloats(Buffer vertex, int strideInBytes, float[] vertices, int numVertices) {
        return findFloats(asFloatArray(asFloatBuffer(vertex)), strideInBytes, vertices, numVertices);
    }

    public static long findFloats(float[] vertex, int strideInBytes, float[] vertices, int numVertices) {
        final int size = strideInBytes / 4;
        for(int i = 0; i < numVertices; i++) {
            final int offset = i * size;
            boolean found = true;
            for(int j = 0; !found && j < size; j++)
                if(vertices[offset + j] != vertex[j])
                    found = false;
            if(found)
                return i;
        }
        return -1;
    }

    public static long findFloats(Buffer vertex, int strideInBytes, Buffer vertices, int numVertices, float epsilon) {
        return findFloats(asFloatArray(asFloatBuffer(vertex)), strideInBytes, asFloatArray(asFloatBuffer(vertices)), numVertices, epsilon);
    }

    public static long findFloats(float[] vertex, int strideInBytes, Buffer vertices, int numVertices, float epsilon) {
        return findFloats(vertex, strideInBytes, asFloatArray(asFloatBuffer(vertices)), numVertices, epsilon);
    }

    public static long findFloats(Buffer vertex, int strideInBytes, float[] vertices, int numVertices, float epsilon) {
        return findFloats(asFloatArray(asFloatBuffer(vertex)), strideInBytes, vertices, numVertices, epsilon);
    }

    public static long findFloats(float[] vertex, int strideInBytes, float[] vertices, int numVertices, float epsilon) {
        final int size = strideInBytes / 4;
        for(int i = 0; i < numVertices; i++) {
            final int offset = i * size;
            boolean found = true;
            for(int j = 0; !found && j < size; j++)
                if((vertices[offset + j] > vertex[j] ? vertices[offset + j] - vertex[j] : vertex[j] - vertices[offset + j]) > epsilon)
                    found = false;
            if(found)
                return i;
        }
        return -1;
    }

    public static FloatBuffer newFloatBuffer(int numFloats) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numFloats * 4);
        }
        else {
            buffer = ByteBuffer.allocate(numFloats * 4);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asFloatBuffer();
    }

    public static DoubleBuffer newDoubleBuffer(int numDoubles) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numDoubles * 8);
        }
        else {
            buffer = ByteBuffer.allocate(numDoubles * 8);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asDoubleBuffer();
    }

    public static ByteBuffer newByteBuffer(int numBytes) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numBytes);
        }
        else {
            buffer = ByteBuffer.allocate(numBytes);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    public static ShortBuffer newShortBuffer(int numShorts) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numShorts * 2);
        }
        else {
            buffer = ByteBuffer.allocate(numShorts * 2);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asShortBuffer();
    }

    public static CharBuffer newCharBuffer(int numChars) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numChars * 2);
        }
        else {
            buffer = ByteBuffer.allocate(numChars * 2);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asCharBuffer();
    }

    public static IntBuffer newIntBuffer(int numInts) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numInts * 4);
        }
        else {
            buffer = ByteBuffer.allocate(numInts * 4);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asIntBuffer();
    }

    public static LongBuffer newLongBuffer(int numLongs) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numLongs * 8);
        }
        else {
            buffer = ByteBuffer.allocate(numLongs * 8);
        }
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asLongBuffer();
    }

    public static void disposeUnsafeByteBuffer (ByteBuffer buffer) {
        int size = buffer.capacity();
        if (!unsafeBuffers.removeValue(buffer, true))
            throw new IllegalArgumentException("buffer not allocated with newUnsafeByteBuffer or already disposed");
        allocatedUnsafe -= size;
        if(buffer.isDirect()) {
            Buffers.free(buffer);
        }
    }

    public static ByteBuffer newUnsafeByteBuffer (int numBytes) {
        ByteBuffer buffer;
        if(useDirectBuffer) {
            buffer = ByteBuffer.allocateDirect(numBytes);
        }
        else {
            buffer = ByteBuffer.allocate(numBytes);
        }
        buffer.order(ByteOrder.nativeOrder());
        allocatedUnsafe += numBytes;
        unsafeBuffers.add(buffer);
        return buffer;
    }

    public static int getAllocatedBytesUnsafe () {
        return allocatedUnsafe;
    }

    private static ByteBuffer newDisposableByteBuffer (int numBytes) {
        return newByteBuffer(numBytes);
    }

    private static int bytesToElements (Buffer dst, int bytes) {
        if (dst instanceof ByteBuffer)
            return bytes;
        else if (dst instanceof ShortBuffer)
            return bytes >>> 1;
        else if (dst instanceof CharBuffer)
            return bytes >>> 1;
        else if (dst instanceof IntBuffer)
            return bytes >>> 2;
        else if (dst instanceof LongBuffer)
            return bytes >>> 3;
        else if (dst instanceof FloatBuffer)
            return bytes >>> 2;
        else if (dst instanceof DoubleBuffer)
            return bytes >>> 3;
        else
            throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
    }
}
