package emu.com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Emulation of IndexBufferObject for TeaVM C backend (GLFW).
 * This class provides index buffer management for the C backend.
 */
public class TIndexBufferObject implements IndexData {
    ByteBuffer byteBuffer;
    ShortBuffer buffer;
    int bufferHandle;
    final boolean isDirect;
    boolean isDirty = true;
    boolean isBound = false;
    final int usage;
    int numIndices = 0;  // Track actual number of indices

    private static int instanceCount = 0;
    private int instanceId;

    public TIndexBufferObject(boolean isStatic, int maxIndices) {
        this.instanceId = ++instanceCount;

        isDirect = true;
        byteBuffer = BufferUtils.newUnsafeByteBuffer(maxIndices * 2);
        buffer = byteBuffer.asShortBuffer();
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
    }

    public TIndexBufferObject(int maxIndices) {
        this.isDirect = true;
        buffer = BufferUtils.newShortBuffer(maxIndices);
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = GL20.GL_STATIC_DRAW;
    }

    public int getNumIndices () {
        return numIndices;
    }

    public int getNumMaxIndices () {
        return buffer.capacity();
    }

    public void setIndices (short[] indices, int offset, int count) {
        isDirty = true;
        numIndices = count;
        buffer.clear();
        buffer.put(indices, offset, count);
        buffer.flip();
        byteBuffer.position(0);
        byteBuffer.limit(count * 2);

        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
            isDirty = false;
        }
    }

    public void setIndices (ShortBuffer indices) {
        isDirty = true;
        buffer.clear();
        buffer.put(indices);
        buffer.flip();
        numIndices = buffer.limit();
        byteBuffer.position(0);
        byteBuffer.limit(numIndices * 2);  // shorts are 2 bytes each

        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);  // Upload byteBuffer!
            isDirty = false;
        }
    }

    @Override
    public void updateIndices (int targetOffset, short[] indices, int offset, int count) {
        isDirty = true;
        final int pos = buffer.position();
        buffer.position(targetOffset);
        BufferUtils.copy(indices, offset, buffer, count);
        buffer.position(pos);
        // Update numIndices to be the maximum of current and updated positions
        if (targetOffset + count > numIndices) {
            numIndices = targetOffset + count;
        }
        byteBuffer.position(0);
        byteBuffer.limit(numIndices * 2);  // shorts are 2 bytes each

        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);  // Upload byteBuffer!
            isDirty = false;
        }
    }

    @Override
    @Deprecated
    public ShortBuffer getBuffer () {
        isDirty = true;
        return buffer;
    }

    @Override
    public ShortBuffer getBuffer (boolean forWriting) {
        isDirty |= forWriting;
        return buffer;
    }

    private static int bindCallCount = 0;

    public void bind () {
        if (bufferHandle == 0) throw new GdxRuntimeException("No buffer allocated!");

        Gdx.gl20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
        if (isDirty) {
            byteBuffer.position(0);
            byteBuffer.limit(numIndices * 2);
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
            isDirty = false;
        }
        isBound = true;
    }

    public void unbind () {
        Gdx.gl20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
        isBound = false;
    }

    public void invalidate () {
        bufferHandle = Gdx.gl20.glGenBuffer();
        isDirty = true;
    }

    public void dispose () {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(bufferHandle);
        bufferHandle = 0;
        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
        byteBuffer = null;
        buffer = null;
    }
}

