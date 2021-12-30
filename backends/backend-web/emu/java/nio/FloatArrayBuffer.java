package java.nio;

/** FloatArrayBuffer, ReadWriteFloatArrayBuffer and ReadOnlyFloatArrayBuffer compose the implementation of array based float
 * buffers.
 * <p>
 * FloatArrayBuffer implements all the shared readonly methods and is extended by the other two classes.
 * </p>
 * <p>
 * All methods are marked final for runtime performance.
 * </p> */
abstract class FloatArrayBuffer extends XFloatBuffer {

    protected final float[] backingArray;

    protected final int offset;

    FloatArrayBuffer (float[] array) {
        this(array.length, array, 0);
    }

    FloatArrayBuffer (int capacity) {
        this(capacity, new float[capacity], 0);
    }

    FloatArrayBuffer (int capacity, float[] backingArray, int offset) {
        super(capacity);
        this.backingArray = backingArray;
        this.offset = offset;
    }

    public final float get () {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return backingArray[offset + position++];
    }

    public final float get (int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return backingArray[offset + index];
    }

    public final XFloatBuffer get (float[] dest, int off, int len) {
        int length = dest.length;
        if (off < 0 || len < 0 || (long)off + (long)len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(backingArray, offset + position, dest, off, len);
        position += len;
        return this;
    }

    public final boolean isDirect () {
        return false;
    }

    public final ByteOrder order () {
        return ByteOrder.nativeOrder();
    }

}