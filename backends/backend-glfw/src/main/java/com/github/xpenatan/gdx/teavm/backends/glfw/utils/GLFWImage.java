package com.github.xpenatan.gdx.teavm.backends.glfw.utils;

import java.nio.ByteBuffer;
import org.teavm.interop.Structure;

public class GLFWImage extends Structure {
    public int width;
    public int height;
    public byte[] pixels;

    public GLFWImage() {
    }

    public static GLFWImage malloc() {
        return Memory.malloc(sizeOf(GLFWImage.class)).toStructure();
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
        public GLFWImage[] buffer = new GLFWImage[0];

        public void put(GLFWImage icon) {
            GLFWImage[] newBuffer = new GLFWImage[buffer.length + 1];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            newBuffer[buffer.length] = icon;
            buffer = newBuffer;
        }

        public void free() {
            Memory.free(toAddress());
        }
    }
}
