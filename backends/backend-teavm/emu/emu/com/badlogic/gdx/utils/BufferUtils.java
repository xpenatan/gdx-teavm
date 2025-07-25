package emu.com.badlogic.gdx.utils;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.TeaTool;
import org.teavm.classlib.PlatformDetector;
import org.teavm.classlib.impl.nio.Buffers;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public final class BufferUtils {

    static Array<ByteBuffer> unsafeBuffers = new Array<ByteBuffer>();
    static int allocatedUnsafe = 0;

    public static void copy(float[] src, Buffer dst, int numFloats, int offset) {
        if (dst instanceof ByteBuffer)
            dst.limit(numFloats << 2);
        else if (dst instanceof FloatBuffer) dst.limit(numFloats);

        FloatBuffer floatBuffer = asFloatBuffer(dst);

        floatBuffer.clear();
        dst.position(0);
        floatBuffer.put(src, offset, numFloats);
        dst.position(0);
        if(dst instanceof ByteBuffer)
            dst.limit(numFloats << 2);
        else
            dst.limit(numFloats);
    }

    public static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
        if(!(dst instanceof ByteBuffer)) throw new GdxRuntimeException("dst must be a ByteBuffer");
        dst.limit(dst.position() + bytesToElements(dst, numElements));

        ByteBuffer byteBuffer = (ByteBuffer)dst;
        int oldPosition = byteBuffer.position();
        byteBuffer.limit(oldPosition + numElements);
        byteBuffer.put(src, srcOffset, numElements);
        byteBuffer.position(oldPosition);
    }

    public static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 1));

        ShortBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asShortBuffer();
        else if(dst instanceof ShortBuffer) buffer = (ShortBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or ShortBuffer");

        int oldPosition = buffer.position();
        buffer.limit(oldPosition + numElements);
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
        CharBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asCharBuffer();
        else if(dst instanceof CharBuffer) buffer = (CharBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or CharBuffer");

        int oldPosition = buffer.position();
        buffer.limit(oldPosition + numElements);
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
        IntBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asIntBuffer();
        else if(dst instanceof IntBuffer) buffer = (IntBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or IntBuffer");

        int oldPosition = buffer.position();
        buffer.limit(oldPosition + numElements);
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
        LongBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asLongBuffer();
        else if(dst instanceof LongBuffer) buffer = (LongBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or LongBuffer");

        int oldPosition = buffer.position();
        buffer.limit(oldPosition + numElements);
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
        FloatBuffer buffer = asFloatBuffer(dst);

        int oldPosition = buffer.position();
        buffer.limit(oldPosition + numElements);
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
        dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
        DoubleBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asDoubleBuffer();
        else if(dst instanceof DoubleBuffer) buffer = (DoubleBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or DoubleBuffer");

        int oldPosition = buffer.position();
        buffer.limit(oldPosition + numElements);
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(char[] src, int srcOffset, int numElements, Buffer dst) {
        CharBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asCharBuffer();
        else if(dst instanceof CharBuffer) buffer = (CharBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or CharBuffer");

        int oldPosition = buffer.position();
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(int[] src, int srcOffset, int numElements, Buffer dst) {
        IntBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asIntBuffer();
        else if(dst instanceof IntBuffer) buffer = (IntBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or IntBuffer");

        int oldPosition = buffer.position();
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(long[] src, int srcOffset, int numElements, Buffer dst) {
        LongBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asLongBuffer();
        else if(dst instanceof LongBuffer) buffer = (LongBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or LongBuffer");

        int oldPosition = buffer.position();
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(float[] src, int srcOffset, int numElements, Buffer dst) {
        FloatBuffer buffer = asFloatBuffer(dst);
        int oldPosition = buffer.position();
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(double[] src, int srcOffset, int numElements, Buffer dst) {
        DoubleBuffer buffer = null;
        if(dst instanceof ByteBuffer)
            buffer = ((ByteBuffer)dst).asDoubleBuffer();
        else if(dst instanceof DoubleBuffer) buffer = (DoubleBuffer)dst;
        if(buffer == null) throw new GdxRuntimeException("dst must be a ByteBuffer or DoubleBuffer");

        int oldPosition = buffer.position();
        buffer.put(src, srcOffset, numElements);
        buffer.position(oldPosition);
    }

    public static void copy(Buffer src, Buffer dst, int numElements) {
        int numBytes = elementsToBytes(src, numElements);
        dst.limit(dst.position() + bytesToElements(dst, numBytes));
        int srcPos = src.position();
        int dstPos = dst.position();
        src.limit(srcPos + numElements);
        final boolean srcIsByte = src instanceof ByteBuffer;
        final boolean dstIsByte = dst instanceof ByteBuffer;
        dst.limit(dst.capacity());
        if(srcIsByte && dstIsByte)
            ((ByteBuffer)dst).put((ByteBuffer)src);
        else if((srcIsByte || src instanceof CharBuffer) && (dstIsByte || dst instanceof CharBuffer))
            (dstIsByte ? ((ByteBuffer)dst).asCharBuffer() : (CharBuffer)dst).put((srcIsByte ? ((ByteBuffer)src).asCharBuffer() : (CharBuffer)src));
        else if((srcIsByte || src instanceof ShortBuffer) && (dstIsByte || dst instanceof ShortBuffer))
            (dstIsByte ? ((ByteBuffer)dst).asShortBuffer() : (ShortBuffer)dst).put((srcIsByte ? ((ByteBuffer)src).asShortBuffer() : (ShortBuffer)src));
        else if((srcIsByte || src instanceof IntBuffer) && (dstIsByte || dst instanceof IntBuffer))
            (dstIsByte ? ((ByteBuffer)dst).asIntBuffer() : (IntBuffer)dst).put((srcIsByte ? ((ByteBuffer)src).asIntBuffer() : (IntBuffer)src));
        else if((srcIsByte || src instanceof LongBuffer) && (dstIsByte || dst instanceof LongBuffer))
            (dstIsByte ? ((ByteBuffer)dst).asLongBuffer() : (LongBuffer)dst).put((srcIsByte ? ((ByteBuffer)src).asLongBuffer() : (LongBuffer)src));
        else if((srcIsByte || src instanceof FloatBuffer) && (dstIsByte || dst instanceof FloatBuffer))
            (dstIsByte ? ((ByteBuffer)dst).asFloatBuffer() : (FloatBuffer)dst).put((srcIsByte ? ((ByteBuffer)src).asFloatBuffer() : (FloatBuffer)src));
        else if((srcIsByte || src instanceof DoubleBuffer) && (dstIsByte || dst instanceof DoubleBuffer))
            (dstIsByte ? ((ByteBuffer)dst).asDoubleBuffer() : (DoubleBuffer)dst).put((srcIsByte ? ((ByteBuffer)src).asDoubleBuffer() : (DoubleBuffer)src));
        else
            throw new GdxRuntimeException("Buffers must be of same type or ByteBuffer");
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
        if(TeaTool.isProdMode()) {
            ByteBuffer buffer = newByteBuffer(numFloats * 4);
            return buffer.asFloatBuffer();
        }
        else {
            return FloatBuffer.wrap(new float[numFloats]);
        }
    }

    public static DoubleBuffer newDoubleBuffer(int numDoubles) {
        if(TeaTool.isProdMode()) {
            ByteBuffer buffer = newByteBuffer(numDoubles * 8);
            return buffer.asDoubleBuffer();
        }
        else {
            return DoubleBuffer.wrap(new double[numDoubles]);
        }
    }

    public static ByteBuffer newByteBuffer(int numBytes) {
        if(TeaTool.isProdMode()) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
            buffer.order(ByteOrder.nativeOrder());
            return buffer;
        }
        else {
            return ByteBuffer.wrap(new byte[numBytes]);
        }
    }

    public static ShortBuffer newShortBuffer(int numShorts) {
        if(TeaTool.isProdMode()) {
            ByteBuffer buffer = newByteBuffer(numShorts * 2);
            return buffer.asShortBuffer();
        }
        else {
            return ShortBuffer.wrap(new short[numShorts]);
        }
    }

    public static CharBuffer newCharBuffer(int numChars) {
        if(TeaTool.isProdMode()) {
            ByteBuffer buffer = newByteBuffer(numChars * 2);
            return buffer.asCharBuffer();
        }
        else {
            return CharBuffer.wrap(new char[numChars]);
        }
    }

    public static IntBuffer newIntBuffer(int numInts) {
        if(TeaTool.isProdMode()) {
            ByteBuffer buffer = newByteBuffer(numInts * 4);
            return buffer.asIntBuffer();
        }
        else {
            return IntBuffer.wrap(new int[numInts]);
        }
    }

    public static LongBuffer newLongBuffer(int numLongs) {
        // FIXME ouch :p
        return LongBuffer.wrap(new long[numLongs]);
    }

    public static void disposeUnsafeByteBuffer (ByteBuffer buffer) {
        int size = buffer.capacity();
        if (!unsafeBuffers.removeValue(buffer, true))
            throw new IllegalArgumentException("buffer not allocated with newUnsafeByteBuffer or already disposed");
        allocatedUnsafe -= size;
        freeMemory(buffer);
    }

    public static ByteBuffer newUnsafeByteBuffer (int numBytes) {
        ByteBuffer buffer = newDisposableByteBuffer(numBytes);
        buffer.order(ByteOrder.nativeOrder());
        allocatedUnsafe += numBytes;
        unsafeBuffers.add(buffer);
        return buffer;
    }

    public static int getAllocatedBytesUnsafe () {
        return allocatedUnsafe;
    }

    private static ByteBuffer newDisposableByteBuffer (int numBytes) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    private static void freeMemory (ByteBuffer buffer) {
        if (PlatformDetector.isWebAssemblyGC() && buffer.isDirect()) {
            Buffers.free(buffer);
        }
        // Do nothing because this is javascript
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

    private static int elementsToBytes (Buffer dst, int elements) {
        if (dst instanceof ByteBuffer)
            return elements;
        else if (dst instanceof ShortBuffer)
            return elements << 1;
        else if (dst instanceof CharBuffer)
            return elements << 1;
        else if (dst instanceof IntBuffer)
            return elements << 2;
        else if (dst instanceof LongBuffer)
            return elements << 3;
        else if (dst instanceof FloatBuffer)
            return elements << 2;
        else if (dst instanceof DoubleBuffer)
            return elements << 3;
        else
            throw new GdxRuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
    }
}
