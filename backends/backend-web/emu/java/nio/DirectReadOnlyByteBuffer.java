package java.nio;

import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferWrapper;

/** HeapByteBuffer, ReadWriteHeapByteBuffer and ReadOnlyHeapByteBuffer compose the implementation of array based byte buffers.
 * <p>
 * ReadOnlyHeapByteBuffer extends HeapByteBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class DirectReadOnlyByteBuffer extends XDirectByteBuffer {

    static DirectReadOnlyByteBuffer copy (XDirectByteBuffer other, int markOfOther) {
        DirectReadOnlyByteBuffer buf = new DirectReadOnlyByteBuffer(other.byteArray.getBuffer(), other.capacity(),
                other.byteArray.getByteOffset());
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        buf.order(other.order());
        return buf;
    }

    DirectReadOnlyByteBuffer (ArrayBufferWrapper backingArray, int capacity, int arrayOffset) {
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

    public XFloatBuffer asFloatBuffer () {
        return DirectReadOnlyFloatBufferAdapter.wrap(this);
    }

    public XIntBuffer asIntBuffer () {
        return order() == ByteOrder.nativeOrder() ? DirectReadOnlyIntBufferAdapter.wrap(this) : super.asIntBuffer();
    }

    public XShortBuffer asShortBuffer () {
        return order() == ByteOrder.nativeOrder() ? DirectReadOnlyShortBufferAdapter.wrap(this) : super.asShortBuffer();
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

    public XByteBuffer put (XByteBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public XByteBuffer slice () {
        DirectReadOnlyByteBuffer slice = new DirectReadOnlyByteBuffer(byteArray.getBuffer(), remaining(), byteArray.getByteOffset()
                + position);
        slice.order = order;
        return slice;
    }
}