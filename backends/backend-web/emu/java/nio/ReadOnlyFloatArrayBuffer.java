package java.nio;

/** FloatArrayBuffer, ReadWriteFloatArrayBuffer and ReadOnlyFloatArrayBuffer compose the implementation of array based float
 * buffers.
 * <p>
 * ReadOnlyFloatArrayBuffer extends FloatArrayBuffer with all the write methods throwing read only exception.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p> */
final class ReadOnlyFloatArrayBuffer extends FloatArrayBuffer {

    static ReadOnlyFloatArrayBuffer copy (FloatArrayBuffer other, int markOfOther) {
        ReadOnlyFloatArrayBuffer buf = new ReadOnlyFloatArrayBuffer(other.capacity(), other.backingArray, other.offset);
        buf.limit = other.limit();
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    ReadOnlyFloatArrayBuffer (int capacity, float[] backingArray, int arrayOffset) {
        super(capacity, backingArray, arrayOffset);
    }

    public XFloatBuffer asReadOnlyBuffer () {
        return duplicate();
    }

    public XFloatBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    public XFloatBuffer duplicate () {
        return copy(this, mark);
    }

    public boolean isReadOnly () {
        return true;
    }

    protected float[] protectedArray () {
        throw new ReadOnlyBufferException();
    }

    protected int protectedArrayOffset () {
        throw new ReadOnlyBufferException();
    }

    protected boolean protectedHasArray () {
        return false;
    }

    public XFloatBuffer put (float c) {
        throw new ReadOnlyBufferException();
    }

    public XFloatBuffer put (int index, float c) {
        throw new ReadOnlyBufferException();
    }

    public XFloatBuffer put (XFloatBuffer buf) {
        throw new ReadOnlyBufferException();
    }

    public final XFloatBuffer put (float[] src, int off, int len) {
        throw new ReadOnlyBufferException();
    }

    public XFloatBuffer slice () {
        return new ReadOnlyFloatArrayBuffer(remaining(), backingArray, offset + position);
    }

}