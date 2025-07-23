package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class VertexBufferObject implements VertexData {
    final VertexAttributes attributes;
    final ByteBuffer byteBuffer;
    final FloatBuffer buffer;
    int bufferHandle;
    final boolean isStatic;
    final int usage;
    boolean isDirty = false;
    boolean isBound = false;

    /** Constructs a new interleaved VertexBufferObject.
     *
     * @param isStatic whether the vertex data is static.
     * @param numVertices the maximum number of vertices
     * @param attributes the {@link VertexAttribute}s. */
    public VertexBufferObject(boolean isStatic, int numVertices, VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }

    /** Constructs a new interleaved VertexBufferObject.
     *
     * @param isStatic whether the vertex data is static.
     * @param numVertices the maximum number of vertices
     * @param attributes the {@link VertexAttributes}. */
    public VertexBufferObject(boolean isStatic, int numVertices, VertexAttributes attributes) {
        this.isStatic = isStatic;
        this.attributes = attributes;

        byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        buffer = byteBuffer.asFloatBuffer();
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
    }

    @Override
    public VertexAttributes getAttributes () {
        return attributes;
    }

    @Override
    public int getNumVertices () {
        return buffer.limit() / (attributes.vertexSize / 4);
    }

    @Override
    public int getNumMaxVertices () {
        return buffer.capacity() / (attributes.vertexSize / 4);
    }

    /** @deprecated use {@link #getBuffer(boolean)} instead */
    @Override
    @Deprecated
    public FloatBuffer getBuffer () {
        isDirty = true;
        return buffer;
    }

    @Override
    public FloatBuffer getBuffer (boolean forWriting) {
        isDirty |= forWriting;
        return buffer;
    }

    private void bufferChanged () {
        if (isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            isDirty = false;
        }
    }

    @Override
    public void setVertices (float[] vertices, int offset, int count) {
        isDirty = true;
        BufferUtils.copy(vertices, buffer, count, offset);
        buffer.position(0);
        buffer.limit(count);
        bufferChanged();
    }

    @Override
    public void updateVertices (int targetOffset, float[] vertices, int sourceOffset, int count) {
        isDirty = true;
        final int pos = buffer.position();
        buffer.position(targetOffset);
        BufferUtils.copy(vertices, sourceOffset, count, buffer);
        buffer.position(pos);
        bufferChanged();
    }

    /** Binds this VertexBufferObject for rendering via glDrawArrays or glDrawElements
     *
     * @param shader the shader */
    /** Binds this VertexBufferObject for rendering via glDrawArrays or glDrawElements
     *
     * @param shader the shader */
    @Override
    public void bind (ShaderProgram shader) {
        bind(shader, null);
    }

    @Override
    public void bind (ShaderProgram shader, int[] locations) {
        final GL20 gl = Gdx.gl20;

        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
        if (isDirty) {
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            isDirty = false;
        }

        final int numAttributes = attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = shader.getAttributeLocation(attribute.alias);
                if (location < 0) continue;
                shader.enableVertexAttribute(location);
                shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
            }
        } else {
            for (int i = 0; i < numAttributes; i++) {
                final VertexAttribute attribute = attributes.get(i);
                final int location = locations[i];
                if (location < 0) continue;
                shader.enableVertexAttribute(location);
                shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
            }
        }
        isBound = true;
    }

    /** Unbinds this VertexBufferObject.
     *
     * @param shader the shader */
    @Override
    public void unbind (final ShaderProgram shader) {
        unbind(shader, null);
    }

    @Override
    public void unbind (final ShaderProgram shader, final int[] locations) {
        final GL20 gl = Gdx.gl20;
        final int numAttributes = attributes.size();
        if (locations == null) {
            for (int i = 0; i < numAttributes; i++) {
                shader.disableVertexAttribute(attributes.get(i).alias);
            }
        } else {
            for (int i = 0; i < numAttributes; i++) {
                final int location = locations[i];
                if (location >= 0) shader.disableVertexAttribute(location);
            }
        }
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        isBound = false;
    }

    /** Invalidates the VertexBufferObject so a new OpenGL buffer handle is created. Use this in case of a context loss. */
    public void invalidate () {
        bufferHandle = Gdx.gl20.glGenBuffer();
        isDirty = true;
    }

    /** Disposes of all resources this VertexBufferObject uses. */
    @Override
    public void dispose () {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(bufferHandle);
        bufferHandle = 0;
        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
    }
}