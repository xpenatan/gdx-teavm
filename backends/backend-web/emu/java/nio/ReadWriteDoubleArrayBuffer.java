package java.nio;

/** DoubleArrayBuffer, ReadWriteDoubleArrayBuffer and ReadOnlyDoubleArrayBuffer compose the implementation of array based double
 * buffers.
 * <p>
 * ReadWriteDoubleArrayBuffer extends DoubleArrayBuffer with all the write methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadWriteDoubleArrayBuffer extends DoubleArrayBuffer {

    static ReadWriteDoubleArrayBuffer copy (DoubleArrayBuffer other, int markOfOther) {
        ReadWriteDoubleArrayBuffer buf = new ReadWriteDoubleArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadWriteDoubleArrayBuffer (double[] array) {
        super(array);
    }

    ReadWriteDoubleArrayBuffer (int capacity) {
        super(capacity);
    }

    ReadWriteDoubleArrayBuffer (int capacity, double[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XDoubleBuffer asReadOnlyBuffer () {
        return ReadOnlyDoubleArrayBuffer.copy(this, mark);
    }

    public XDoubleBuffer compact () {
        System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    public XDoubleBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return false;
    }

    protected double[] protectedArray () {
        return backingArray;
    }

    protected int protectedArrayOffset () {
        return offset;
    }

    protected boolean protectedHasArray () {
        return true;
    }

    public XDoubleBuffer put (double c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        backingArray[offset + position++] = c;
        return this;
    }

    public XDoubleBuffer put (int index, double c) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        backingArray[offset + index] = c;
        return this;
    }

    public XDoubleBuffer put (double[] src, int off, int len) {
        int length = src.length;
        if (off < 0 || len < 0 || (long)off + (long)len > length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > remaining()) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, off, backingArray, offset + position, len);
        position += len;
        return this;
    }

    public XDoubleBuffer slice () {
        return new ReadWriteDoubleArrayBuffer(remaining(), backingArray, offset + position);
    }

}