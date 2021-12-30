package java.nio;

/** IntArrayBuffer, ReadWriteIntArrayBuffer and ReadOnlyIntArrayBuffer compose the implementation of array based int buffers.
 * <p>
 * ReadOnlyIntArrayBuffer extends IntArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyIntArrayBuffer extends IntArrayBuffer {

    static ReadOnlyIntArrayBuffer copy (IntArrayBuffer other, int markOfOther) {
        ReadOnlyIntArrayBuffer buf = new ReadOnlyIntArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyIntArrayBuffer (int capacity, int[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XIntBuffer asReadOnlyBuffer () {
        return duplicate();
    }

    public XIntBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XIntBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected int[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public XIntBuffer put (int c) {
        throw new ReadOnlyBufferException();
    }

    public XIntBuffer put (int index, int c) {
        throw new ReadOnlyBufferException();
    }

    public XIntBuffer put (XIntBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public final XIntBuffer put (int[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public XIntBuffer slice () {
        return new ReadOnlyIntArrayBuffer(remaining(), backingArray, offset + position);
    }

}