package java.nio;

/** IntArrayBuffer, ReadWriteIntArrayBuffer and ReadOnlyIntArrayBuffer compose the implementation of array based int buffers.
 * <p>
 * ReadWriteIntArrayBuffer extends IntArrayBuffer with all the write methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadWriteIntArrayBuffer extends IntArrayBuffer {

    static ReadWriteIntArrayBuffer copy (IntArrayBuffer other, int markOfOther) {
        ReadWriteIntArrayBuffer buf = new ReadWriteIntArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadWriteIntArrayBuffer (int[] array) {
        super(array);
    }

    ReadWriteIntArrayBuffer (int capacity) {
        super(capacity);
    }

    ReadWriteIntArrayBuffer (int capacity, int[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XIntBuffer asReadOnlyBuffer () {
        return ReadOnlyIntArrayBuffer.copy(this, mark);
    }

    public XIntBuffer compact () {
        System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    public XIntBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return false;
    }

    protected int[] protectedArray () {
        return backingArray;
    }

    protected int protectedArrayOffset () {
        return offset;
    }

    protected boolean protectedHasArray () {
        return true;
    }

    public XIntBuffer put (int c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        backingArray[offset + position++] = c;
        return this;
    }

    public XIntBuffer put (int index, int c) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        backingArray[offset + index] = c;
        return this;
    }

    public XIntBuffer put (int[] src, int off, int len) {
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

    public XIntBuffer slice () {
        return new ReadWriteIntArrayBuffer(remaining(), backingArray, offset + position);
    }

}