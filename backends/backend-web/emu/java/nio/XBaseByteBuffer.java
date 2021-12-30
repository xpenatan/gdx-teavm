package java.nio;

abstract class XBaseByteBuffer extends XByteBuffer{
    protected XBaseByteBuffer (int capacity) {
        super(capacity);
    }

    @Override
    public XCharBuffer asCharBuffer () {
        return CharToByteBufferAdapter.wrap(this);
    }

    @Override
    public XDoubleBuffer asDoubleBuffer () {
        return DoubleToByteBufferAdapter.wrap(this);
    }

    @Override
    public XFloatBuffer asFloatBuffer () {
        return FloatToByteBufferAdapter.wrap(this);
    }

    @Override
    public XIntBuffer asIntBuffer () {
        return IntToByteBufferAdapter.wrap(this);
    }

    @Override
    public XLongBuffer asLongBuffer () {
        return LongToByteBufferAdapter.wrap(this);
    }

    @Override
    public XShortBuffer asShortBuffer () {
        return ShortToByteBufferAdapter.wrap(this);
    }

    public final char getChar () {
        return (char)getShort();
    }

    public final char getChar (int index) {
        return (char)getShort(index);
    }

    public final XByteBuffer putChar (char value) {
        return putShort((short)value);
    }

    public final XByteBuffer putChar (int index, char value) {
        return putShort(index, (short)value);
    }
}
