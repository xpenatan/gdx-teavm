package emu.com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Emulation of VertexBufferObjectWithVAO for TeaVM C backend (GLFW).
 * This class provides VAO-based vertex buffer management for the C backend.
 */
public class TVertexBufferObjectWithVAO implements VertexData {
    final static IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);

    final VertexAttributes attributes;
    ByteBuffer byteBuffer;
    final FloatBuffer buffer;
    int bufferHandle;
    final boolean isStatic;
    final int usage;
    boolean isDirty = false;
    boolean isBound = false;
    int vaoHandle = -1;
    IntArray cachedLocations = new IntArray();
    int numFloats = 0;  // Track actual number of floats set

    public TVertexBufferObjectWithVAO(boolean isStatic, int numVertices, VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }

    private static int instanceCount = 0;
    private int instanceId;

    public TVertexBufferObjectWithVAO(boolean isStatic, int numVertices, VertexAttributes attributes) {
        this.instanceId = ++instanceCount;

        this.isStatic = isStatic;
        this.attributes = attributes;

        byteBuffer = BufferUtils.newUnsafeByteBuffer(this.attributes.vertexSize * numVertices);
        buffer = byteBuffer.asFloatBuffer();
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;

        createVAO();
    }

    private void createVAO () {
        tmpHandle.clear();
        Gdx.gl30.glGenVertexArrays(1, tmpHandle);
        vaoHandle = tmpHandle.get(0);
    }

    @Override
    public VertexAttributes getAttributes () {
        return attributes;
    }

    @Override
    public int getNumVertices () {
        return numFloats / (attributes.vertexSize / 4);
    }

    @Override
    public int getNumMaxVertices () {
        return buffer.capacity() * 4 / attributes.vertexSize;
    }

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
            byteBuffer.position(0);  // Ensure position is 0
            byteBuffer.limit(numFloats * 4);  // Use numFloats to calculate byte limit
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);  // Upload byteBuffer!
            isDirty = false;
        }
    }

    private static int setVerticesCallCount = 0;

    @Override
    public void setVertices(float[] vertices, int offset, int count) {
        isDirty = true;
        numFloats = count;
        BufferUtils.copy(vertices, byteBuffer, count, offset);
        buffer.position(0);
        buffer.limit(count);
        bufferChanged();
    }

    @Override
    public void updateVertices (int targetOffset, float[] vertices, int sourceOffset, int count) {
        isDirty = true;
        final int pos = byteBuffer.position();
        byteBuffer.position(targetOffset * 4);  // Target offset in bytes
        BufferUtils.copy(vertices, sourceOffset, count, byteBuffer);  // Copy to byteBuffer!
        byteBuffer.position(pos);
        buffer.position(0);
        bufferChanged();
    }

    @Override
    public void bind (ShaderProgram shader) {
        bind(shader, null);
    }

    private static int bindCallCount = 0;

    @Override
    public void bind (ShaderProgram shader, int[] locations) {

        GL30 gl = Gdx.gl30;

        gl.glBindVertexArray(vaoHandle);

        bindAttributes(shader, locations);

        // if our data has changed upload it:
        bindData(gl);

        isBound = true;
    }

    private void bindAttributes (ShaderProgram shader, int[] locations) {
        boolean stillValid = this.cachedLocations.size != 0;
        final int numAttributes = attributes.size();

        if (stillValid) {
            if (locations == null) {
                for (int i = 0; stillValid && i < numAttributes; i++) {
                    VertexAttribute attribute = attributes.get(i);
                    int location = shader.getAttributeLocation(attribute.alias);
                    stillValid = location == this.cachedLocations.get(i);
                }
            } else {
                stillValid = locations.length == this.cachedLocations.size;
                for (int i = 0; stillValid && i < numAttributes; i++) {
                    stillValid = locations[i] == this.cachedLocations.get(i);
                }
            }
        }

        if (!stillValid) {
            Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
            unbindAttributes(shader);
            this.cachedLocations.clear();

            for (int i = 0; i < numAttributes; i++) {
                VertexAttribute attribute = attributes.get(i);
                String aliasName = attribute.alias;

                int location;
                if (locations == null) {
                    location = shader.getAttributeLocation(aliasName);
                    this.cachedLocations.add(location);
                } else {
                    location = locations[i];
                    this.cachedLocations.add(location);
                }

                if (location < 0) {
                    continue;
                }


                shader.enableVertexAttribute(location);
                shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized,
                        attributes.vertexSize, attribute.offset);
            }
        }
    }

    private void unbindAttributes (ShaderProgram shaderProgram) {
        if (cachedLocations.size == 0) {
            return;
        }
        int numAttributes = attributes.size();
        for (int i = 0; i < numAttributes; i++) {
            int location = cachedLocations.get(i);
            if (location < 0) {
                continue;
            }
            shaderProgram.disableVertexAttribute(location);
        }
    }

    private void bindData (GL20 gl) {
        if (isDirty) {
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
            byteBuffer.position(0);
            byteBuffer.limit(numFloats * 4);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, usage);
            isDirty = false;
        }
    }

    @Override
    public void unbind (final ShaderProgram shader) {
        unbind(shader, null);
    }

    @Override
    public void unbind (final ShaderProgram shader, final int[] locations) {
        GL30 gl = Gdx.gl30;
        gl.glBindVertexArray(0);
        isBound = false;
    }

    @Override
    public void invalidate () {
        bufferHandle = Gdx.gl20.glGenBuffer();
        createVAO();
        isDirty = true;
    }

    @Override
    public void dispose () {
        GL20 gl = Gdx.gl20;
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(bufferHandle);
        bufferHandle = 0;
        if (vaoHandle != -1) {
            tmpHandle.clear();
            tmpHandle.put(vaoHandle);
            tmpHandle.flip();
            Gdx.gl30.glDeleteVertexArrays(1, tmpHandle);
            vaoHandle = -1;
        }
        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
    }
}

