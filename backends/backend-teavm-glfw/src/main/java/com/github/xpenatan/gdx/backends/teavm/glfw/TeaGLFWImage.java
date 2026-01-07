package com.github.xpenatan.gdx.backends.teavm.glfw;

import java.nio.ByteBuffer;
import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Structure;

public class TeaGLFWImage extends Structure {
    public int width;
    public int height;
    public byte[] pixels;

    public TeaGLFWImage() {
    }

    public static TeaGLFWImage malloc() {
        return Memory.malloc(sizeOf(TeaGLFWImage.class)).toStructure();
    }

    public static Buffer malloc(int length) {
        return Memory.malloc(Byte.SIZE * length).toStructure();
    }

    public void free() {
        Memory.free(toAddress());
    }

    public void set(int width, int height, ByteBuffer pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels.array();
    }

    public static class Buffer extends Structure {
        public TeaGLFWImage[] buffer = new TeaGLFWImage[0];

        public void put(TeaGLFWImage icon) {
            TeaGLFWImage[] newBuffer = new TeaGLFWImage[buffer.length + 1];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            newBuffer[buffer.length] = icon;
            buffer = newBuffer;
        }

        public void free() {
            Memory.free(toAddress());
        }
    }
}
