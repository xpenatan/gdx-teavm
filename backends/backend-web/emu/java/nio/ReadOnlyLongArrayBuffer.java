package java.nio;

/** LongArrayBuffer, ReadWriteLongArrayBuffer and ReadOnlyLongArrayBuffer compose the implementation of array based long buffers.
 * <p>
 * ReadOnlyLongArrayBuffer extends LongArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyLongArrayBuffer extends LongArrayBuffer {

    static ReadOnlyLongArrayBuffer copy (LongArrayBuffer other, int markOfOther) {
        ReadOnlyLongArrayBuffer buf = new ReadOnlyLongArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyLongArrayBuffer (int capacity, long[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XLongBuffer asReadOnlyBuffer () {
        return duplicate();
    }

    public XLongBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XLongBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected long[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public XLongBuffer put (long c) {
        throw new ReadOnlyBufferException();
    }

    public XLongBuffer put (int index, long c) {
        throw new ReadOnlyBufferException();
    }

    public XLongBuffer put (XLongBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public final XLongBuffer put (long[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public XLongBuffer slice () {
        return new ReadOnlyLongArrayBuffer(remaining(), backingArray, offset + position);
    }

}