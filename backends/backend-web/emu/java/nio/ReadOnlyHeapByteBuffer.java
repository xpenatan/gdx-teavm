package java.nio;

/** HeapByteBuffer, ReadWriteHeapByteBuffer and ReadOnlyHeapByteBuffer compose the implementation of array based byte buffers.
 * <p>
 * ReadOnlyHeapByteBuffer extends HeapByteBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyHeapByteBuffer extends XHeapByteBuffer {

    static ReadOnlyHeapByteBuffer copy (XHeapByteBuffer other, int markOfOther) {
        ReadOnlyHeapByteBuffer buf = new ReadOnlyHeapByteBuffer(other.backingArray, other.capacity(), other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        buf.order(other.order());
        return buf;
    }

    ReadOnlyHeapByteBuffer (byte[] backingArray, int capacity, int arrayOffset) {
        super(backingArray, capacity, arrayOffset);
    }

    public XByteBuffer asReadOnlyBuffer () {
        return copy(this, mark);
    }

    public XByteBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected byte[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public XByteBuffer put (byte b) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer put (int index, byte b) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer put (byte[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putDouble (double value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putDouble (int index, double value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putFloat (float value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putFloat (int index, float value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putInt (int value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putInt (int index, int value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putLong (int index, long value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putLong (long value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putShort (int index, short value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer putShort (short value) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer put (ByteBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer slice () {
        ReadOnlyHeapByteBuffer slice = new ReadOnlyHeapByteBuffer(backingArray, remaining(), offset + position);
        slice.order = order;
        return slice;
    }
}