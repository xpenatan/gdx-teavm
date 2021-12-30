package java.nio;

/** This class wraps a byte buffer to be a long buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by the adapter. It must NOT be accessed outside the
 * adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter. The adapter extends Buffer, thus has its own position
 * and limit.</li>
 * </ul>
 * </p> */
final class LongToByteBufferAdapter extends XLongBuffer {// implements DirectBuffer {

    static XLongBuffer wrap (XByteBuffer byteBuffer) {
        return new LongToByteBufferAdapter(byteBuffer.slice());
    }

    private final XByteBuffer byteBuffer;

    LongToByteBufferAdapter (XByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 3));
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
    public XLongBuffer asReadOnlyBuffer () {
        LongToByteBufferAdapter buf = new LongToByteBufferAdapter(byteBuffer.asReadOnlyBuffer());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public XLongBuffer compact () {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        byteBuffer.limit(limit << 3);
        byteBuffer.position(position << 3);
        byteBuffer.compact();
        byteBuffer.clear();
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public XLongBuffer duplicate () {
        LongToByteBufferAdapter buf = new LongToByteBufferAdapter(byteBuffer.duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public long get () {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return byteBuffer.getLong(position++ << 3);
    }

    @Override
    public long get (int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return byteBuffer.getLong(index << 3);
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
    protected long[] protectedArray () {
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
    public XLongBuffer put (long c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        byteBuffer.putLong(position++ << 3, c);
        return this;
    }

    @Override
    public XLongBuffer put (int index, long c) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        byteBuffer.putLong(index << 3, c);
        return this;
    }

    @Override
    public XLongBuffer slice () {
        byteBuffer.limit(limit << 3);
        byteBuffer.position(position << 3);
        XLongBuffer result = new LongToByteBufferAdapter(byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

}