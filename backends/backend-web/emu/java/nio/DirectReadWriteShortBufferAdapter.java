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
final class DirectReadWriteShortBufferAdapter extends XShortBuffer
{
    // implements DirectBuffer {

    static XShortBuffer wrap(DirectReadWriteByteBuffer byteBuffer)
    {
        return new DirectReadWriteShortBufferAdapter((DirectReadWriteByteBuffer) byteBuffer.slice());
    }

    private final DirectReadWriteByteBuffer byteBuffer;
    private final Int16ArrayWrapper shortArray;

    DirectReadWriteShortBufferAdapter(DirectReadWriteByteBuffer byteBuffer)
    {
        super((byteBuffer.capacity() >> 1));
        this.byteBuffer= byteBuffer;
        this.byteBuffer.clear();
        this.shortArray= TypedArrays.getInstance().createInt16Array(byteBuffer.byteArray.getBuffer(), byteBuffer.byteArray.getByteOffset(), capacity);
    }

    // TODO(haustein) This will be slow
    @Override
    public XShortBuffer asReadOnlyBuffer()
    {
        DirectReadOnlyShortBufferAdapter buf= new DirectReadOnlyShortBufferAdapter(byteBuffer);
        buf.limit= limit;
        buf.position= position;
        buf.mark= mark;
        return buf;
    }

    @Override
    public XShortBuffer compact()
    {
        byteBuffer.limit(limit << 1);
        byteBuffer.position(position << 1);
        byteBuffer.compact();
        byteBuffer.clear();
        position= limit - position;
        limit= capacity;
        mark= UNSET_MARK;
        return this;
    }

    @Override
    public XShortBuffer duplicate()
    {
        DirectReadWriteShortBufferAdapter buf= new DirectReadWriteShortBufferAdapter((DirectReadWriteByteBuffer) byteBuffer.duplicate());
        buf.limit= limit;
        buf.position= position;
        buf.mark= mark;
        return buf;
    }

    @Override
    public short get()
    {
        // if (position == limit) {
        // throw new BufferUnderflowException();
        // }
        return (short) shortArray.get(position++);
    }

    @Override
    public short get(int index)
    {
        // if (index < 0 || index >= limit) {
        // throw new IndexOutOfBoundsException();
        // }
        return (short) shortArray.get(index);
    }

    @Override
    public boolean isDirect()
    {
        return true;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public ByteOrder order()
    {
        return byteBuffer.order();
    }

    @Override
    protected short[] protectedArray()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int protectedArrayOffset()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean protectedHasArray()
    {
        return false;
    }

    @Override
    public XShortBuffer put(short c)
    {
        // if (position == limit) {
        // throw new BufferOverflowException();
        // }
        shortArray.set(position++, c);
        return this;
    }

    @Override
    public XShortBuffer put(int index, short c)
    {
        // if (index < 0 || index >= limit) {
        // throw new IndexOutOfBoundsException();
        // }
        shortArray.set(index, c);
        return this;
    }

    @Override
    public XShortBuffer slice()
    {
        byteBuffer.limit(limit << 1);
        byteBuffer.position(position << 1);
        XShortBuffer result = new DirectReadWriteShortBufferAdapter((DirectReadWriteByteBuffer) byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

    public ArrayBufferViewWrapper getTypedArray()
    {
        return shortArray;
    }

    public int getElementSize()
    {
        return 2;
    }

}