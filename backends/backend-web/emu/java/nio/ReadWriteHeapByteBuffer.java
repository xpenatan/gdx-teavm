package java.nio;

import java.io.Numbers;

/** HeapByteBuffer, ReadWriteHeapByteBuffer and ReadOnlyHeapByteBuffer compose the implementation of array based byte buffers.
 * <p>
 * ReadWriteHeapByteBuffer extends HeapByteBuffer with all the write methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadWriteHeapByteBuffer extends XHeapByteBuffer {

    static ReadWriteHeapByteBuffer copy (XHeapByteBuffer other, int markOfOther) {
        ReadWriteHeapByteBuffer buf = new ReadWriteHeapByteBuffer(other.backingArray, other.capacity(), other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        buf.order(other.order());
        return buf;
    }

    ReadWriteHeapByteBuffer (byte[] backingArray) {
        super(backingArray);
    }

    ReadWriteHeapByteBuffer (int capacity) {
        super(capacity);
    }

    ReadWriteHeapByteBuffer (byte[] backingArray, int capacity, int arrayOffset) {
        super(backingArray, capacity, arrayOffset);
    }

    public XByteBuffer asReadOnlyBuffer () {
        return ReadOnlyHeapByteBuffer.copy(this, mark);
    }

    public XByteBuffer compact () {
        System.arraycopy(backingArray, position + offset, backingArray, offset, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    public XByteBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return false;
    }

    protected byte[] protectedArray () {
        return backingArray;
    }

    protected int protectedArrayOffset () {
        return offset;
    }

    protected boolean protectedHasArray () {
        return true;
    }

    public XByteBuffer put (byte b) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        backingArray[offset + position++] = b;
        return this;
    }

    public XByteBuffer put (int index, byte b) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        backingArray[offset + index] = b;
        return this;
    }

    /*
     * Override ByteBuffer.put(byte[], int, int) to improve performance.
     *
     * (non-Javadoc)
     *
     * @see java.nio.ByteBuffer#put(byte[], int, int)
     */
    public XByteBuffer put (byte[] src, int off, int len) {
        if (off < 0 || len < 0 || (long)off + (long)len > src.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len > remaining()) {
            throw new BufferOverflowException();
        }
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(src, off, backingArray, offset + position, len);
        position += len;
        return this;
    }

    public XByteBuffer putDouble (double value) {
        return putLong(Numbers.doubleToRawLongBits(value));
    }

    public XByteBuffer putDouble (int index, double value) {
        return putLong(index, Numbers.doubleToRawLongBits(value));
    }

    public XByteBuffer putFloat (float value) {
        return putInt(Numbers.floatToIntBits(value));
    }

    public XByteBuffer putFloat (int index, float value) {
        return putInt(index, Numbers.floatToIntBits(value));
    }

    public XByteBuffer putInt (int value) {
        int newPosition = position + 4;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        store(position, value);
        position = newPosition;
        return this;
    }

    public XByteBuffer putInt (int index, int value) {
        if (index < 0 || (long)index + 4 > limit) {
            throw new IndexOutOfBoundsException();
        }
        store(index, value);
        return this;
    }

    public XByteBuffer putLong (int index, long value) {
        if (index < 0 || (long)index + 8 > limit) {
            throw new IndexOutOfBoundsException();
        }
        store(index, value);
        return this;
    }

    public XByteBuffer putLong (long value) {
        int newPosition = position + 8;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        store(position, value);
        position = newPosition;
        return this;
    }

    public XByteBuffer putShort (int index, short value) {
        if (index < 0 || (long)index + 2 > limit) {
            throw new IndexOutOfBoundsException();
        }
        store(index, value);
        return this;
    }

    public XByteBuffer putShort (short value) {
        int newPosition = position + 2;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        store(position, value);
        position = newPosition;
        return this;
    }

    public XByteBuffer slice () {
        ReadWriteHeapByteBuffer slice = new ReadWriteHeapByteBuffer(backingArray, remaining(), offset + position);
        slice.order = order;
        return slice;
    }
}