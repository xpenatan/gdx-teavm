package java.nio;

/** This class wraps a byte buffer to be a char buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by the adapter. It must NOT be accessed outside the
 * adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter. The adapter extends Buffer, thus has its own position
 * and limit.</li>
 * </ul>
 * </p> */
final class CharToByteBufferAdapter extends XCharBuffer { // implements DirectBuffer {

    static XCharBuffer wrap (XByteBuffer byteBuffer) {
        return new CharToByteBufferAdapter(byteBuffer.slice());
    }

    private final XByteBuffer byteBuffer;

    CharToByteBufferAdapter (XByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 1));
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
    }

// public int getByteCapacity() {
// if (byteBuffer instanceof DirectBuffer) {
// return ((DirectBuffer) byteBuffer).getByteCapacity();
// }
// assert false : byteBuffer;
// return -1;
// }
//
// public PlatformAddress getEffectiveAddress() {
// if (byteBuffer instanceof DirectBuffer) {
// return ((DirectBuffer) byteBuffer).getEffectiveAddress();
// }
// assert false : byteBuffer;
// return null;
// }
//
// public PlatformAddress getBaseAddress() {
// if (byteBuffer instanceof DirectBuffer) {
// return ((DirectBuffer) byteBuffer).getBaseAddress();
// }
// assert false : byteBuffer;
// return null;
// }
//
// public boolean isAddressValid() {
// if (byteBuffer instanceof DirectBuffer) {
// return ((DirectBuffer) byteBuffer).isAddressValid();
// }
// assert false : byteBuffer;
// return false;
// }
//
// public void addressValidityCheck() {
// if (byteBuffer instanceof DirectBuffer) {
// ((DirectBuffer) byteBuffer).addressValidityCheck();
// } else {
// assert false : byteBuffer;
// }
// }
//
// public void free() {
// if (byteBuffer instanceof DirectBuffer) {
// ((DirectBuffer) byteBuffer).free();
// } else {
// assert false : byteBuffer;
// }
// }

    @Override
    public XCharBuffer asReadOnlyBuffer () {
        CharToByteBufferAdapter buf = new CharToByteBufferAdapter(byteBuffer.asReadOnlyBuffer());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public XCharBuffer compact () {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        byteBuffer.limit(limit << 1);
        byteBuffer.position(position << 1);
        byteBuffer.compact();
        byteBuffer.clear();
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public XCharBuffer duplicate () {
        CharToByteBufferAdapter buf = new CharToByteBufferAdapter(byteBuffer.duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public char get () {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return byteBuffer.getChar(position++ << 1);
    }

    @Override
    public char get (int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return byteBuffer.getChar(index << 1);
    }

    @Override
    public boolean isDirect () {
        return byteBuffer.isDirect();
    }

    @Override
    public boolean isReadOnly () {
        return byteBuffer.isReadOnly();
    }

    @Override
    public ByteOrder order () {
        return byteBuffer.order();
    }

    @Override
    protected char[] protectedArray () {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int protectedArrayOffset () {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean protectedHasArray () {
        return false;
    }

    @Override
    public XCharBuffer put (char c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        byteBuffer.putChar(position++ << 1, c);
        return this;
    }

    @Override
    public XCharBuffer put (int index, char c) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        byteBuffer.putChar(index << 1, c);
        return this;
    }

    @Override
    public XCharBuffer slice () {
        byteBuffer.limit(limit << 1);
        byteBuffer.position(position << 1);
        XCharBuffer result = new CharToByteBufferAdapter(byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

    @Override
    public CharSequence subSequence (int start, int end) {
        if (start < 0 || end < start || end > remaining()) {
            throw new IndexOutOfBoundsException();
        }

        XCharBuffer result = duplicate();
        result.limit(position + end);
        result.position(position + start);
        return result;
    }
}