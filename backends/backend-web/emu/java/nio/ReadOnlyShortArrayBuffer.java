package java.nio;

/** ShortArrayBuffer, ReadWriteShortArrayBuffer and ReadOnlyShortArrayBuffer compose the implementation of array based short
 * buffers.
 * <p>
 * ReadOnlyShortArrayBuffer extends ShortArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyShortArrayBuffer extends ShortArrayBuffer {

    static ReadOnlyShortArrayBuffer copy (ShortArrayBuffer other, int markOfOther) {
        ReadOnlyShortArrayBuffer buf = new ReadOnlyShortArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyShortArrayBuffer (int capacity, short[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XShortBuffer asReadOnlyBuffer () {
        return duplicate();
    }

    public XShortBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XShortBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected short[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public ShortBuffer put (ShortBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public XShortBuffer put (short c) {
        throw new ReadOnlyBufferException();
    }

    public XShortBuffer put (int index, short c) {
        throw new ReadOnlyBufferException();
    }

    public final XShortBuffer put (short[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public XShortBuffer slice () {
        return new ReadOnlyShortArrayBuffer(remaining(), backingArray, offset + position);
    }

}