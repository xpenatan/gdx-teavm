package emu.com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class IndexBufferObject implements IndexData {
    ByteBuffer byteBuffer;
    ShortBuffer buffer;
    int bufferHandle;
    final boolean isDirect;
    boolean isDirty = true;
    boolean isBound = false;
    final int usage;

    public IndexBufferObject(boolean isStatic, int maxIndices) {
        isDirect = true;
        byteBuffer = BufferUtils.newUnsafeByteBuffer(maxIndices * 2);
        buffer = byteBuffer.asShortBuffer();
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
    }

    public IndexBufferObject(int maxIndices) {
        this.isDirect = true;
        buffer = BufferUtils.newShortBuffer(maxIndices);
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = GL20.GL_STATIC_DRAW;
    }

    public int getNumIndices () {
        return buffer.limit();
    }

    public int getNumMaxIndices () {
        return buffer.capacity();
    }

    public void setIndices (short[] indices, int offset, int count) {
        isDirty = true;
        buffer.clear();
        buffer.put(indices, offset, count);
        buffer.flip();

        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            isDirty = false;
        }
    }

    public void setIndices (ShortBuffer indices) {
        isDirty = true;
        buffer.clear();
        buffer.put(indices);
        buffer.flip();

        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buffer.limit(), buffer, usage);
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

        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buffer.limit(), buffer, usage);
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

    public void bind () {
        if (bufferHandle == 0) throw new GdxRuntimeException("No buffer allocated!");

        Gdx.gl20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
        if (isDirty) {
            Gdx.gl20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buffer.limit(), buffer, usage);
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
