package java.nio;

import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.TypedArrays;

/** This class wraps a byte buffer to be a int buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by the adapter. It must NOT be accessed outside the
 * adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter. The adapter extends Buffer, thus has its own position
 * and limit.</li>
 * </ul>
 * </p> */
final class DirectReadWriteIntBufferAdapter extends XIntBuffer {

    static XIntBuffer wrap (DirectReadWriteByteBuffer byteBuffer) {
        return new DirectReadWriteIntBufferAdapter((DirectReadWriteByteBuffer)byteBuffer.slice());
    }

    private final DirectReadWriteByteBuffer byteBuffer;
    private final Int32ArrayWrapper intArray;

    DirectReadWriteIntBufferAdapter (DirectReadWriteByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 2));
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.intArray = TypedArrays.getInstance().createInt32Array(byteBuffer.byteArray.getBuffer(), byteBuffer.byteArray.getByteOffset(), capacity);
    }

    // TODO(haustein) This will be slow
    @Override
    public XIntBuffer asReadOnlyBuffer () {
        DirectReadOnlyIntBufferAdapter buf = new DirectReadOnlyIntBufferAdapter(byteBuffer);
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public XIntBuffer compact () {
        byteBuffer.limit(limit << 2);
        byteBuffer.position(position << 2);
        byteBuffer.compact();
        byteBuffer.clear();
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public XIntBuffer duplicate () {
        DirectReadWriteIntBufferAdapter buf = new DirectReadWriteIntBufferAdapter((DirectReadWriteByteBuffer)byteBuffer.duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public int get () {
// if (position == limit) {
// throw new BufferUnderflowException();
// }
        return intArray.get(position++);
    }

    @Override
    public int get (int index) {
// if (index < 0 || index >= limit) {
// throw new IndexOutOfBoundsException();
// }
        return intArray.get(index);
    }

    @Override
    public boolean isDirect () {
        return true;
    }

    @Override
    public boolean isReadOnly () {
        return false;
    }

    @Override
    public ByteOrder order () {
        return byteBuffer.order();
    }

    @Override
    protected int[] protectedArray () {
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
    public XIntBuffer put (int c) {
// if (position == limit) {
// throw new BufferOverflowException();
// }
        intArray.set(position++, c);
        return this;
    }

    @Override
    public XIntBuffer put (int index, int c) {
// if (index < 0 || index >= limit) {
// throw new IndexOutOfBoundsException();
// }
        intArray.set(index, c);
        return this;
    }

    @Override
    public XIntBuffer slice () {
        byteBuffer.limit(limit << 2);
        byteBuffer.position(position << 2);
        XIntBuffer result = new DirectReadWriteIntBufferAdapter((DirectReadWriteByteBuffer)byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

    public ArrayBufferViewWrapper getTypedArray () {
        return intArray;
    }

    public int getElementSize () {
        return 4;
    }
}