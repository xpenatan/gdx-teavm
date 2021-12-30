package java.nio;

import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.TypedArrays;

/** This class wraps a byte buffer to be a float buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by the adapter. It must NOT be accessed outside the
 * adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter. The adapter extends Buffer, thus has its own position
 * and limit.</li>
 * </ul>
 * </p> */
final class DirectReadWriteFloatBufferAdapter extends XFloatBuffer {
// implements DirectBuffer {

    static XFloatBuffer wrap (DirectReadWriteByteBuffer byteBuffer) {
        return new DirectReadWriteFloatBufferAdapter((DirectReadWriteByteBuffer)byteBuffer.slice());
    }

    private final DirectReadWriteByteBuffer byteBuffer;
    private final Float32ArrayWrapper floatArray;

    DirectReadWriteFloatBufferAdapter (DirectReadWriteByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 2));
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
        this.floatArray = TypedArrays.getInstance().createFloat32Array(byteBuffer.byteArray.getBuffer(), byteBuffer.byteArray.getByteOffset(), capacity);
    }

    // TODO(haustein) This will be slow
    @Override
    public XFloatBuffer asReadOnlyBuffer () {
        DirectReadOnlyFloatBufferAdapter buf = new DirectReadOnlyFloatBufferAdapter(byteBuffer);
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public XFloatBuffer compact () {
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
    public XFloatBuffer duplicate () {
        DirectReadWriteFloatBufferAdapter buf = new DirectReadWriteFloatBufferAdapter(
                (DirectReadWriteByteBuffer)byteBuffer.duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    @Override
    public float get () {
// if (position == limit) {
// throw new BufferUnderflowException();
// }
        return floatArray.get(position++);
    }

    @Override
    public float get (int index) {
// if (index < 0 || index >= limit) {
// throw new IndexOutOfBoundsException();
// }
        return floatArray.get(index);
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
    protected float[] protectedArray () {
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
    public XFloatBuffer put (float c) {
// if (position == limit) {
// throw new BufferOverflowException();
// }
        floatArray.set(position++, c);
        return this;
    }

    @Override
    public XFloatBuffer put (int index, float c) {
// if (index < 0 || index >= limit) {
// throw new IndexOutOfBoundsException();
// }
        floatArray.set(index, c);
        return this;
    }

    @Override
    public XFloatBuffer slice () {
        byteBuffer.limit(limit << 2);
        byteBuffer.position(position << 2);
        XFloatBuffer result = new DirectReadWriteFloatBufferAdapter((DirectReadWriteByteBuffer)byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

    public ArrayBufferViewWrapper getTypedArray () {
        return floatArray;
    }

    public int getElementSize () {
        return 4;
    }
}