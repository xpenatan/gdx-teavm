package java.nio;

/** CharArrayBuffer, ReadWriteCharArrayBuffer and ReadOnlyCharArrayBuffer compose the implementation of array based char buffers.
 * <p>
 * CharArrayBuffer implements all the shared readonly methods and is extended by the other two classes.
 * </p>
 * <p>
 * All methods are marked final for runtime performance.
 * </p> */
abstract class CharArrayBuffer extends XCharBuffer {

    protected final char[] backingArray;

    protected final int offset;

    CharArrayBuffer (char[] array) {
        this(array.length, array, 0);
    }

    CharArrayBuffer (int capacity) {
        this(capacity, new char[capacity], 0);
    }

    CharArrayBuffer (int capacity, char[] backingArray, int offset) {
        super(capacity);
        this.backingArray = backingArray;
        this.offset = offset;
    }

    public final char get () {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return backingArray[offset + position++];
    }

    public final char get (int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return backingArray[offset + index];
    }

    public final XCharBuffer get (char[] dest, int off, int len) {
        int length = dest.length;
        if ((off < 0) || (len < 0) || (long)off + (long)len > length) {
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

    public final CharSequence subSequence (int start, int end) {
        if (start < 0 || end < start || end > remaining()) {
            throw new IndexOutOfBoundsException();
        }

        XCharBuffer result = duplicate();
        result.limit(position + end);
        result.position(position + start);
        return result;
    }

    public final String toString () {
        return String.copyValueOf(backingArray, offset + position, remaining());
    }
}