package java.nio;

/** DoubleArrayBuffer, ReadWriteDoubleArrayBuffer and ReadOnlyDoubleArrayBuffer compose the implementation of array based double
 * buffers.
 * <p>
 * DoubleArrayBuffer implements all the shared readonly methods and is extended by the other two classes.
 * </p>
 * <p>
 * All methods are marked final for runtime performance.
 * </p> */
abstract class DoubleArrayBuffer extends XDoubleBuffer {

    protected final double[] backingArray;

    protected final int offset;

    DoubleArrayBuffer (double[] array) {
        this(array.length, array, 0);
    }

    DoubleArrayBuffer (int capacity) {
        this(capacity, new double[capacity], 0);
    }

    DoubleArrayBuffer (int capacity, double[] backingArray, int offset) {
        super(capacity);
        this.backingArray = backingArray;
        this.offset = offset;
    }

    public final double get () {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return backingArray[offset + position++];
    }

    public final double get (int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return backingArray[offset + index];
    }

    public final XDoubleBuffer get (double[] dest, int off, int len) {
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