package com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObjectWithVAO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.utils.IntArray;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Emulate(VertexBufferObjectWithVAO.class)
public class VertexBufferObjectWithVAOEmu implements VertexData {
    final static IntBuffer tmpHandle = BufferUtils.newIntBuffer(1);

    final VertexAttributes attributes;
    final FloatBuffer buffer;
    int bufferHandle;
    final boolean isStatic;
    final int usage;
    boolean isDirty = false;
    boolean isBound = false;
    int vaoHandle = -1;
    IntArray cachedLocations = new IntArray();

    public VertexBufferObjectWithVAOEmu(boolean isStatic, int numVertices, VertexAttribute... attributes) {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }

    public VertexBufferObjectWithVAOEmu(boolean isStatic, int numVertices, VertexAttributes attributes) {
        this.isStatic = isStatic;
        this.attributes = attributes;

        buffer = BufferUtils.newFloatBuffer(this.attributes.vertexSize / 4 * numVertices);
        buffer.flip();
        bufferHandle = Gdx.gl20.glGenBuffer();
        usage = isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW;
        createVAO();
    }

    @Override
    public VertexAttributes getAttributes() {
        return attributes;
    }

    @Override
    public int getNumVertices() {
        return buffer.limit() * 4 / attributes.vertexSize;
    }

    @Override
    public int getNumMaxVertices() {
        return buffer.capacity() * 4 / attributes.vertexSize;
    }

    @Override
    public FloatBuffer getBuffer() {
        isDirty = true;
        return buffer;
    }

    private void bufferChanged() {
        if(isBound) {
            Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            isDirty = false;
        }
    }

    @Override
    public void setVertices(float[] vertices, int offset, int count) {
        isDirty = true;
        BufferUtils.copy(vertices, buffer, count, offset);
        buffer.position(0);
        buffer.limit(count);
        bufferChanged();
    }

    @Override
    public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
        isDirty = true;
        final int pos = buffer.position();
        buffer.position(targetOffset);
        BufferUtils.copy(vertices, sourceOffset, count, buffer);
        buffer.position(pos);
        bufferChanged();
    }

    @Override
    public void bind(ShaderProgram shader) {
        bind(shader, null);
    }

    @Override
    public void bind(ShaderProgram shader, int[] locations) {
        GL30 gl = Gdx.gl30;

        gl.glBindVertexArray(vaoHandle);

        bindAttributes(shader, locations);

        //if our data has changed upload it:
        bindData(gl);

        isBound = true;
    }

    private void bindAttributes(ShaderProgram shader, int[] locations) {
        boolean stillValid = this.cachedLocations.size != 0;
        final int numAttributes = attributes.size();

        if(stillValid) {
            if(locations == null) {
                for(int i = 0; stillValid && i < numAttributes; i++) {
                    VertexAttribute attribute = attributes.get(i);
                    int location = shader.getAttributeLocation(attribute.alias);
                    stillValid = location == this.cachedLocations.get(i);
                }
            }
            else {
                stillValid = locations.length == this.cachedLocations.size;
                for(int i = 0; stillValid && i < numAttributes; i++) {
                    stillValid = locations[i] == this.cachedLocations.get(i);
                }
            }
        }

        if(!stillValid) {
            Gdx.gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
            unbindAttributes(shader);
            this.cachedLocations.clear();

            for(int i = 0; i < numAttributes; i++) {
                VertexAttribute attribute = attributes.get(i);
                if(locations == null) {
                    this.cachedLocations.add(shader.getAttributeLocation(attribute.alias));
                }
                else {
                    this.cachedLocations.add(locations[i]);
                }

                int location = this.cachedLocations.get(i);
                if(location < 0) {
                    continue;
                }

                shader.enableVertexAttribute(location);
                shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, attributes.vertexSize, attribute.offset);
            }
        }
    }

    private void unbindAttributes(ShaderProgram shaderProgram) {
        if(cachedLocations == null) {
            return;
        }
        int numAttributes = attributes.size();
        for(int i = 0; i < numAttributes; i++) {
            int location = cachedLocations.get(i);
            if(location < 0) {
                continue;
            }
            shaderProgram.disableVertexAttribute(location);
        }
    }

    private void bindData(GL20 gl) {
        if(isDirty) {
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
            buffer.limit(buffer.limit());
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.limit(), buffer, usage);
            isDirty = false;
        }
    }

    @Override
    public void unbind(final ShaderProgram shader) {
        unbind(shader, null);
    }

    @Override
    public void unbind(final ShaderProgram shader, final int[] locations) {
        GL30 gl = Gdx.gl30;
        gl.glBindVertexArray(0);
        isBound = false;
    }

    @Override
    public void invalidate() {
        bufferHandle = Gdx.gl20.glGenBuffer();
        createVAO();
        isDirty = true;
    }

    @Override
    public void dispose() {
        GL30 gl = Gdx.gl30;

        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        gl.glDeleteBuffer(bufferHandle);
        bufferHandle = 0;
        deleteVAO();
    }

    private void createVAO() {
        tmpHandle.clear();
        Gdx.gl30.glGenVertexArrays(1, tmpHandle);
        vaoHandle = tmpHandle.get();
    }

    private void deleteVAO() {
        if(vaoHandle != -1) {
            tmpHandle.clear();
            tmpHandle.put(vaoHandle);
            tmpHandle.flip();
            Gdx.gl30.glDeleteVertexArrays(1, tmpHandle);
            vaoHandle = -1;
        }
    }
}