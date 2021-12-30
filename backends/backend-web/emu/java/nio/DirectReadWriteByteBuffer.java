package java.nio;

import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferWrapper;

import java.io.Numbers;

/** DirectByteBuffer, DirectReadWriteByteBuffer and DirectReadOnlyByteBuffer compose the implementation of direct byte buffers.
 * <p>
 * DirectReadWriteByteBuffer extends DirectByteBuffer with all the write methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
public final class DirectReadWriteByteBuffer extends XDirectByteBuffer {

    static DirectReadWriteByteBuffer copy (XDirectByteBuffer other, int markOfOther) {
        DirectReadWriteByteBuffer buf = new DirectReadWriteByteBuffer(other.byteArray.getBuffer(), other.capacity(),
                other.byteArray.getByteOffset());
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        buf.order(other.order());
        return buf;
    }

    public DirectReadWriteByteBuffer (ArrayBufferWrapper backingArray) {
        super(backingArray);
    }

    public DirectReadWriteByteBuffer (int capacity) {
        super(capacity);
    }

    public DirectReadWriteByteBuffer (ArrayBufferWrapper backingArray, int capacity, int arrayOffset) {
        super(backingArray, capacity, arrayOffset);
    }

    public XFloatBuffer asFloatBuffer () {
        return DirectReadWriteFloatBufferAdapter.wrap(this);
    }

    public XIntBuffer asIntBuffer () {
        return order() == ByteOrder.nativeOrder() ? DirectReadWriteIntBufferAdapter.wrap(this) : super.asIntBuffer();
    }

    public XShortBuffer asShortBuffer () {
        return order() == ByteOrder.nativeOrder() ? DirectReadWriteShortBufferAdapter.wrap(this) : super.asShortBuffer();
    }

    public XByteBuffer asReadOnlyBuffer () {
        return DirectReadOnlyByteBuffer.copy(this, mark);
    }

    public XByteBuffer compact () {
// System.arraycopy(backingArray, position + offset, backingArray, offset,
// remaining());

        int rem = remaining();
        for (int i = 0; i < rem; i++) {
            byteArray.set(i, byteArray.get(position + i));
        }

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
        throw new UnsupportedOperationException();
    }

    protected int protectedArrayOffset () {
        throw new UnsupportedOperationException();
    }

    protected boolean protectedHasArray () {
        return true;
    }

    public XByteBuffer put (byte b) {
// if (position == limit) {
// throw new BufferOverflowException();
// }
        byteArray.set(position++, b);
        return this;
    }

    public XByteBuffer put (int index, byte b) {
// if (index < 0 || index >= limit) {
// throw new IndexOutOfBoundsException();
// }
        byteArray.set(index, b);
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
        for (int i = 0; i < len; i++) {
            byteArray.set(i + position, src[off + i]);
        }
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
// if (newPosition > limit) {
// throw new BufferOverflowException();
// }
        store(position, value);
        position = newPosition;
        return this;
    }

    public XByteBuffer putInt (int index, int value) {
// if (index < 0 || (long)index + 4 > limit) {
// throw new IndexOutOfBoundsException();
// }
        store(index, value);
        return this;
    }

    public XByteBuffer putLong (int index, long value) {
// if (index < 0 || (long)index + 8 > limit) {
// throw new IndexOutOfBoundsException();
// }
        store(index, value);
        return this;
    }

    public XByteBuffer putLong (long value) {
        int newPosition = position + 8;
// if (newPosition > limit) {
// throw new BufferOverflowException();
// }
        store(position, value);
        position = newPosition;
        return this;
    }

    public XByteBuffer putShort (int index, short value) {
// if (index < 0 || (long)index + 2 > limit) {
// throw new IndexOutOfBoundsException();
// }
        store(index, value);
        return this;
    }

    public XByteBuffer putShort (short value) {
        int newPosition = position + 2;
// if (newPosition > limit) {
// throw new BufferOverflowException();
// }
        store(position, value);
        position = newPosition;
        return this;
    }

    public XByteBuffer slice () {
        DirectReadWriteByteBuffer slice = new DirectReadWriteByteBuffer(byteArray.getBuffer(), remaining(),
                byteArray.getByteOffset() + position);
        slice.order = order;
        return slice;
    }
}