package java.nio;

import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.TypedArrays;

/** This class wraps a byte buffer to be a short buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by the adapter. It must NOT be accessed outside the
 * adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter. The adapter extends Buffer, thus has its own position
 * and limit.</li>
 * </ul>
 * </p> */
final class DirectReadOnlyShortBufferAdapter extends XShortBuffer {

    static XShortBuffer wrap (XDirectByteBuffer byteBuffer) {
        return new DirectReadOnlyShortBufferAdapter((XDirectByteBuffer)byteBuffer.slice());
    }

    private final XDirectByteBuffer byteBuffer;
    private final Int16ArrayWrapper shortArray;

    DirectReadOnlyShortBufferAdapter (XDirectByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 1));
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.shortArray = TypedArrays.getInstance().createInt16Array(byteBuffer.byteArray.getBuffer(), byteBuffer.byteArray.getByteOffset(), capacity);
    }

    @Override
    public XShortBuffer asReadOnlyBuffer () {
        DirectReadOnlyShortBufferAdapter buf = new DirectReadOnlyShortBufferAdapter(byteBuffer);
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public XShortBuffer compact () {
        throw new ReadOnlyBufferException();
    }

    @Override
    public XShortBuffer duplicate () {
        DirectReadOnlyShortBufferAdapter buf = new DirectReadOnlyShortBufferAdapter((XDirectByteBuffer)byteBuffer.duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public short get () {
// if (position == limit) {
// throw new BufferUnderflowException();
// }
        return (short) shortArray.get(position++);
    }

    @Override
    public short get (int index) {
// if (index < 0 || index >= limit) {
// throw new IndexOutOfBoundsException();
// }
        return (short) shortArray.get(index);
    }

    @Override
    public boolean isDirect () {
        return true;
    }

    @Override
    public boolean isReadOnly () {
        return true;
    }

    @Override
    public ByteOrder order () {
        return byteBuffer.order();
    }

    @Override
    protected short[] protectedArray () {
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
    public XShortBuffer put (short c) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public XShortBuffer put (int index, short c) {
        throw new ReadOnlyBufferException();
    }

    @Override
    public XShortBuffer slice () {
        byteBuffer.limit(limit << 1);
        byteBuffer.position(position << 1);
        XShortBuffer result = new DirectReadOnlyShortBufferAdapter((XDirectByteBuffer)byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

    public ArrayBufferViewWrapper getTypedArray () {
        return shortArray;
    }

    public int getElementSize () {
        return 2;
    }
}