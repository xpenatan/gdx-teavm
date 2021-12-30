package java.nio;

/** DoubleArrayBuffer, ReadWriteDoubleArrayBuffer and ReadOnlyDoubleArrayBuffer compose the implementation of array based double
 * buffers.
 * <p>
 * ReadOnlyDoubleArrayBuffer extends DoubleArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyDoubleArrayBuffer extends DoubleArrayBuffer {

    static ReadOnlyDoubleArrayBuffer copy (DoubleArrayBuffer other, int markOfOther) {
        ReadOnlyDoubleArrayBuffer buf = new ReadOnlyDoubleArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyDoubleArrayBuffer (int capacity, double[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XDoubleBuffer asReadOnlyBuffer () {
        return duplicate();
    }

    public XDoubleBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XDoubleBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected double[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public XDoubleBuffer put (double c) {
        throw new ReadOnlyBufferException();
    }

    public XDoubleBuffer put (int index, double c) {
        throw new ReadOnlyBufferException();
    }

    public final XDoubleBuffer put (double[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public final XDoubleBuffer put (XDoubleBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public XDoubleBuffer slice () {
        return new ReadOnlyDoubleArrayBuffer(remaining(), backingArray, offset + position);
    }

}