package java.nio;

/** CharArrayBuffer, ReadWriteCharArrayBuffer and ReadOnlyCharArrayBuffer compose the implementation of array based char buffers.
 * <p>
 * ReadOnlyCharArrayBuffer extends CharArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyCharArrayBuffer extends CharArrayBuffer {

    static ReadOnlyCharArrayBuffer copy (CharArrayBuffer other, int markOfOther) {
        ReadOnlyCharArrayBuffer buf = new ReadOnlyCharArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyCharArrayBuffer (int capacity, char[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XCharBuffer asReadOnlyBuffer () {
        return duplicate();
    }

    public XCharBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XCharBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected char[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public XCharBuffer put (char c) {
        throw new ReadOnlyBufferException();
    }

    public XCharBuffer put (int index, char c) {
        throw new ReadOnlyBufferException();
    }

    public final XCharBuffer put (char[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public final XCharBuffer put (XCharBuffer src) {
        throw new ReadOnlyBufferException();
    }

    public XCharBuffer put (String src, int start, int end) {
        if ((start < 0) || (end < 0) || (long)start + (long)end > src.length()) {
            throw new IndexOutOfBoundsException();
        }
        throw new ReadOnlyBufferException();
    }

    public XCharBuffer slice () {
        return new ReadOnlyCharArrayBuffer(remaining(), backingArray, offset + position);
    }
}