package com.github.xpenatan.gdx.teavm.backends.android.graphics;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import org.teavm.classlib.java.nio.TBuffer;
import org.teavm.classlib.java.nio.TNativeBufferUtil;
import org.teavm.interop.Address;

public class AndroidGL20 implements GL20 {
    public static class AddressUtils {
        public static Address of(Buffer buffer) {
            if (buffer == null) {
                return Address.fromInt(0);
            }

            // Always create a fresh address from the current buffer state
            // Don't cache because buffer position/limit may change
            if (buffer instanceof ByteBuffer) {
                return ofInternal((ByteBuffer) buffer);
            } else if (buffer instanceof ShortBuffer) {
                return ofInternal((ShortBuffer) buffer);
            } else if (buffer instanceof IntBuffer) {
                return ofInternal((IntBuffer) buffer);
            } else if (buffer instanceof LongBuffer) {
                return ofInternal((LongBuffer) buffer);
            } else if (buffer instanceof FloatBuffer) {
                return ofInternal((FloatBuffer) buffer);
            } else if (buffer instanceof DoubleBuffer) {
                return ofInternal((DoubleBuffer) buffer);
            } else if (buffer instanceof CharBuffer) {
                return ofInternal((CharBuffer) buffer);
            }

            throw new IllegalArgumentException("Unsupported buffer type: " + buffer.getClass());
        }

        public static Address ofInternal(ByteBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 1);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add(buffer.arrayOffset() + buffer.position());
            } else {
                byte[] data = new byte[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(ShortBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 2);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 2);
            } else {
                short[] data = new short[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(IntBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 4);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 4);
            } else {
                int[] data = new int[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(LongBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 8);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 8);
            } else {
                long[] data = new long[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(FloatBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 4);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 4);
            } else {
                float[] data = new float[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(DoubleBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 8);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 8);
            } else {
                double[] data = new double[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(CharBuffer buffer) {
            if (buffer.isDirect()) {
                return nativeAddress(buffer, 2);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 2);
            } else {
                char[] data = new char[buffer.remaining()];
                int pos = buffer.position();
                buffer.get(data);
                buffer.position(pos);
                return Address.ofData(data);
            }
        }

        private static Address nativeAddress(Buffer buffer, int elementSize) {
            Address address = TNativeBufferUtil.getAddress((TBuffer)(Object)buffer);
            return address.add(buffer.position() * elementSize);
        }
    }

    private static void nullCheck(int value) {
        if (value == 0) throw new GdxRuntimeException("NULL value passed to OpenGL method");
    }

    private static void nullCheck(int shader, int index) {
        if (shader == 0) throw new GdxRuntimeException("NULL value passed to OpenGL method from array/buffer index " + index);
    }

    private static void nullCheck(IntBuffer buf) {
        if (buf == null) throw new GdxRuntimeException("NULL value passed to OpenGL method from buffer");

        for (int i = 0; i < buf.capacity(); i++) {
            if (buf.get(i) == 0) throw new GdxRuntimeException("NULL value passed to OpenGL method from buffer at index " + i);
        }
    }

    private static void nullCheck(int[] buf) {
        if (buf == null) throw new GdxRuntimeException("NULL value passed to OpenGL method from array");

        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == 0) throw new GdxRuntimeException("NULL value passed to OpenGL method from array at index " + i);
        }
    }

    private static int stringLength(String value) {
        return value == null ? 0 : value.length();
    }

    private static int remainingBytes(Buffer buffer) {
        if (buffer == null) {
            return 0;
        } else if (buffer instanceof ByteBuffer) {
            return buffer.remaining();
        } else if (buffer instanceof ShortBuffer || buffer instanceof CharBuffer) {
            return buffer.remaining() * 2;
        } else if (buffer instanceof IntBuffer || buffer instanceof FloatBuffer) {
            return buffer.remaining() * 4;
        } else if (buffer instanceof LongBuffer || buffer instanceof DoubleBuffer) {
            return buffer.remaining() * 8;
        }
        throw new IllegalArgumentException("Unsupported buffer type: " + buffer.getClass());
    }

    private static int vectorCount(Buffer buffer, int componentCount) {
        return buffer == null ? 0 : buffer.remaining() / componentCount;
    }

    private static Address intArrayAddress(int[] data, int offset) {
        return Address.ofData(data).add(offset * 4);
    }

    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, int offset) {
        AndroidOpenGLES.glTexImage2D(target, level, internalformat, width, height, border, format, type, offset);
    }
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, int offset) {
        AndroidOpenGLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, offset);
    }
    public void glActiveTexture(int texture) {
        AndroidOpenGLES.glActiveTexture(texture);
    }
    public void glBindTexture(int target, int texture) {
        AndroidOpenGLES.glBindTexture(target, texture);
    }
    public void glBlendFunc(int sfactor, int dfactor) {
        AndroidOpenGLES.glBlendFunc(sfactor, dfactor);
    }
    public void glClear(int mask) {
        AndroidOpenGLES.glClear(mask);
    }
    public void glClearColor(float red, float green, float blue, float alpha) {
        AndroidOpenGLES.glClearColor(red, green, blue, alpha);
    }
    public void glClearDepthf(float depth) {
        AndroidOpenGLES.glClearDepthf(depth);
    }
    public void glClearStencil(int s) {
        AndroidOpenGLES.glClearStencil(s);
    }
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        AndroidOpenGLES.glColorMask(red, green, blue, alpha);
    }
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
        AndroidOpenGLES.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, AddressUtils.of(data));
    }
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
        AndroidOpenGLES.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, AddressUtils.of(data));
    }
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        AndroidOpenGLES.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        AndroidOpenGLES.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }
    public void glCullFace(int mode) {
        AndroidOpenGLES.glCullFace(mode);
    }
    public void glDeleteTextures(int n, IntBuffer textures) {
        Address address = AddressUtils.of(textures);
        AndroidOpenGLES.glDeleteTextures(n, address);
        textures.rewind();
    }
    public void glDeleteTexture(int texture) {
        IntBuffer textures = IntBuffer.allocate(1);
        textures.put(texture);
        AndroidOpenGLES.glDeleteTextures(1, textures);
    }
    public void glDepthFunc(int func) {
        AndroidOpenGLES.glDepthFunc(func);
    }
    public void glDepthMask(boolean flag) {
        AndroidOpenGLES.glDepthMask(flag);
    }
    public void glDepthRangef(float zNear, float zFar) {
        AndroidOpenGLES.glDepthRangef(zNear, zFar);
    }
    public void glDisable(int cap) {
        AndroidOpenGLES.glDisable(cap);
    }
    public void glDrawArrays(int mode, int first, int count) {
        AndroidOpenGLES.glDrawArrays(mode, first, count);
    }
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        Address address = AddressUtils.of(indices);
        AndroidOpenGLES.glDrawElements(mode, count, type, address);
        indices.rewind();
    }
    public void glEnable(int cap) {
        AndroidOpenGLES.glEnable(cap);
    }
    public void glFinish() {
        AndroidOpenGLES.glFinish();
    }
    public void glFlush() {
        AndroidOpenGLES.glFlush();
    }
    public void glFrontFace(int mode) {
        AndroidOpenGLES.glFrontFace(mode);
    }
    public void glGenTextures(int n, IntBuffer textures) {
        Address address = AddressUtils.of(textures);
        AndroidOpenGLES.glGenTextures(n, address);
        textures.rewind();
    }
    public int glGenTexture() {
        IntBuffer texture = IntBuffer.allocate(1);
        AndroidOpenGLES.glGenTextures(1, texture);
        return texture.get(0);
    }
    public int glGetError() {
        return AndroidOpenGLES.glGetError();
    }
    public void glGetIntegerv(int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetIntegerv(pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public String glGetString(int name) {
        return AndroidOpenGLES.glGetString(name);
    }
    public void glHint(int target, int mode) {
        AndroidOpenGLES.glHint(target, mode);
    }
    public void glLineWidth(float width) {
        AndroidOpenGLES.glLineWidth(width);
    }
    public void glPixelStorei(int pname, int param) {
        AndroidOpenGLES.glPixelStorei(pname, param);
    }
    public void glPolygonOffset(float factor, float units) {
        AndroidOpenGLES.glPolygonOffset(factor, units);
    }
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer)
            AndroidOpenGLES.glReadPixels(x, y, width, height, format, type, (ByteBuffer)pixels);
        else if (pixels instanceof ShortBuffer)
            AndroidOpenGLES.glReadPixels(x, y, width, height, format, type, (ShortBuffer)pixels);
        else if (pixels instanceof IntBuffer)
            AndroidOpenGLES.glReadPixels(x, y, width, height, format, type, (IntBuffer)pixels);
        else if (pixels instanceof FloatBuffer)
            AndroidOpenGLES.glReadPixels(x, y, width, height, format, type, (FloatBuffer)pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer or FloatBuffer instead.");
    }
    public void glScissor(int x, int y, int width, int height) {
        AndroidOpenGLES.glScissor(x, y, width, height);
    }
    public void glStencilFunc(int func, int ref, int mask) {
        AndroidOpenGLES.glStencilFunc(func, ref, mask);
    }
    public void glStencilMask(int mask) {
        AndroidOpenGLES.glStencilMask(mask);
    }
    public void glStencilOp(int fail, int zfail, int zpass) {
        AndroidOpenGLES.glStencilOp(fail, zfail, zpass);
    }
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer || pixels == null)
            AndroidOpenGLES.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer)pixels);
        else if (pixels instanceof ShortBuffer)
            AndroidOpenGLES.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ShortBuffer)pixels);
        else if (pixels instanceof IntBuffer)
            AndroidOpenGLES.glTexImage2D(target, level, internalformat, width, height, border, format, type, (IntBuffer)pixels);
        else if (pixels instanceof FloatBuffer)
            AndroidOpenGLES.glTexImage2D(target, level, internalformat, width, height, border, format, type, (FloatBuffer)pixels);
        else if (pixels instanceof DoubleBuffer)
            AndroidOpenGLES.glTexImage2D(target, level, internalformat, width, height, border, format, type, (DoubleBuffer)pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead");
    }
    public void glTexParameterf(int target, int pname, float param) {
        AndroidOpenGLES.glTexParameterf(target, pname, param);
    }
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer || pixels == null)
            AndroidOpenGLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ByteBuffer)pixels);
        else if (pixels instanceof ShortBuffer)
            AndroidOpenGLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ShortBuffer)pixels);
        else if (pixels instanceof IntBuffer)
            AndroidOpenGLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (IntBuffer)pixels);
        else if (pixels instanceof FloatBuffer)
            AndroidOpenGLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (FloatBuffer)pixels);
        else if (pixels instanceof DoubleBuffer)
            AndroidOpenGLES.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (DoubleBuffer)pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
    }
    public void glViewport(int x, int y, int width, int height) {
        AndroidOpenGLES.glViewport(x, y, width, height);
    }
    public void glAttachShader(int program, int shader) {
        AndroidOpenGLES.glAttachShader(program, shader);
    }
    public void glBindAttribLocation(int program, int index, String name) {
        AndroidOpenGLES.glBindAttribLocation(program, index, name);
    }
    public void glBindBuffer(int target, int buffer) {
        AndroidOpenGLES.glBindBuffer(target, buffer);
    }
    public void glBindFramebuffer(int target, int framebuffer) {
        AndroidOpenGLES.glBindFramebuffer(target, framebuffer);
    }
    public void glBindRenderbuffer(int target, int renderbuffer) {
        AndroidOpenGLES.glBindRenderbuffer(target, renderbuffer);
    }
    public void glBlendColor(float red, float green, float blue, float alpha) {
        AndroidOpenGLES.glBlendColor(red, green, blue, alpha);
    }
    public void glBlendEquation(int mode) {
        AndroidOpenGLES.glBlendEquation(mode);
    }
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        AndroidOpenGLES.glBlendEquationSeparate(modeRGB, modeAlpha);
    }
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        AndroidOpenGLES.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }
    public void glBufferData(int target, int size, Buffer data, int usage) {
        if (data instanceof ByteBuffer || data == null)
            AndroidOpenGLES.glBufferData(target, size, (ByteBuffer)data, usage);
        else if (data instanceof IntBuffer)
            AndroidOpenGLES.glBufferData(target, size, (IntBuffer)data, usage);
        else if (data instanceof FloatBuffer)
            AndroidOpenGLES.glBufferData(target, size, (FloatBuffer)data, usage);
        else if (data instanceof DoubleBuffer)
            AndroidOpenGLES.glBufferData(target, size, (DoubleBuffer)data, usage);
        else if (data instanceof ShortBuffer)
            AndroidOpenGLES.glBufferData(target, size, (ShortBuffer)data, usage);
    }
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        if (data instanceof ByteBuffer || data == null)
            AndroidOpenGLES.glBufferSubData(target, offset, size, (ByteBuffer)data);
        else if (data instanceof IntBuffer)
            AndroidOpenGLES.glBufferSubData(target, offset, size, (IntBuffer)data);
        else if (data instanceof FloatBuffer)
            AndroidOpenGLES.glBufferSubData(target, offset, size, (FloatBuffer)data);
        else if (data instanceof DoubleBuffer)
            AndroidOpenGLES.glBufferSubData(target, offset, size, (DoubleBuffer)data);
        else if (data instanceof ShortBuffer)
            AndroidOpenGLES.glBufferSubData(target, offset, size, (ShortBuffer)data);
    }
    public int glCheckFramebufferStatus(int target) {
        return AndroidOpenGLES.glCheckFramebufferStatus(target);
    }
    public void glCompileShader(int shader) {
        AndroidOpenGLES.glCompileShader(shader);
    }
    public int glCreateProgram() {
        return AndroidOpenGLES.glCreateProgram();
    }
    public int glCreateShader(int type) {
        return AndroidOpenGLES.glCreateShader(type);
    }
    public void glDeleteBuffer(int buffer) {
        IntBuffer buffers = IntBuffer.allocate(1);
        buffers.put(buffer);
        AndroidOpenGLES.glDeleteBuffers(1, buffers);
    }
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        Address address = AddressUtils.of(buffers);
        AndroidOpenGLES.glDeleteBuffers(n, address);
        buffers.rewind();
    }
    public void glDeleteFramebuffer(int framebuffer) {
        IntBuffer framebuffers = IntBuffer.allocate(1);
        framebuffers.put(framebuffer);
        AndroidOpenGLES.glDeleteFramebuffers(1, framebuffers);
    }
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        AndroidOpenGLES.glDeleteFramebuffers(n, framebuffers);
    }
    public void glDeleteProgram(int program) {
        AndroidOpenGLES.glDeleteProgram(program);
    }
    public void glDeleteRenderbuffer(int renderbuffer) {
        IntBuffer renderbuffers = IntBuffer.allocate(1);
        renderbuffers.put(renderbuffer);
        AndroidOpenGLES.glDeleteRenderbuffers(1, renderbuffers);
    }
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        AndroidOpenGLES.glDeleteRenderbuffers(n, renderbuffers);
    }
    public void glDeleteShader(int shader) {
        AndroidOpenGLES.glDeleteShader(shader);
    }
    public void glDetachShader(int program, int shader) {
        AndroidOpenGLES.glDetachShader(program, shader);
    }
    public void glDisableVertexAttribArray(int index) {
        AndroidOpenGLES.glDisableVertexAttribArray(index);
    }
    public void glDrawElements(int mode, int count, int type, int indices) {
        AndroidOpenGLES.glDrawElements(mode, count, type, Address.fromInt(indices));
    }
    public void glEnableVertexAttribArray(int index) {
        AndroidOpenGLES.glEnableVertexAttribArray(index);
    }
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        AndroidOpenGLES.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        AndroidOpenGLES.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }
    public int glGenBuffer() {
        IntBuffer buffer = IntBuffer.allocate(1);
        AndroidOpenGLES.glGenBuffers(1, buffer);
        return buffer.get(0);
    }
    public void glGenBuffers(int n, IntBuffer buffers) {
        Address address = AddressUtils.of(buffers);
        AndroidOpenGLES.glGenBuffers(n, address);
        buffers.rewind();
    }
    public void glGenerateMipmap(int target) {
        AndroidOpenGLES.glGenerateMipmap(target);
    }
    public int glGenFramebuffer() {
        IntBuffer buffer = IntBuffer.allocate(1);
        AndroidOpenGLES.glGenFramebuffers(1, buffer);
        return buffer.get(0);
    }
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        AndroidOpenGLES.glGenFramebuffers(n, framebuffers);
    }
    public int glGenRenderbuffer() {
        IntBuffer buffer = IntBuffer.allocate(1);
        AndroidOpenGLES.glGenRenderbuffers(1, buffer);
        return buffer.get(0);
    }
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        AndroidOpenGLES.glGenRenderbuffers(n, renderbuffers);
    }
    public String glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        // TeaVM C backend doesn't properly handle CharBuffer in native calls
        // Use byte array instead for proper marshalling
        IntBuffer lenBuf = IntBuffer.allocate(1);
        byte[] nameBytes = new byte[512];

        AndroidOpenGLES.glGetActiveAttrib(program, index, 512, lenBuf, size, type, nameBytes);

        int nameLength = lenBuf.get(0);
        String name = "";

        if (nameLength > 0 && nameLength <= 512) {
            name = new String(nameBytes, 0, nameLength);
        }

        return name;
    }
    public String glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        // Use ByteBuffer instead of CharBuffer
        ByteBuffer nameBufBytes = ByteBuffer.allocate(512);
        IntBuffer lenBuf = IntBuffer.allocate(1);

        CharBuffer nameBuf = nameBufBytes.asCharBuffer();
        AndroidOpenGLES.glGetActiveUniform(program, index, 512, lenBuf, size, type, nameBuf);

        int nameLength = lenBuf.get(0);
        String name = "";

        if (nameLength > 0 && nameLength <= 512) {
            byte[] bytes = new byte[nameLength];
            nameBufBytes.position(0);
            nameBufBytes.get(bytes);
            name = new String(bytes);
        }

        return name;
    }
    public void glGetAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
        AndroidOpenGLES.glGetAttachedShaders(program, maxcount, count, shaders);
    }
    public int glGetAttribLocation(int program, String name) {
        return AndroidOpenGLES.glGetAttribLocation(program, name);
    }
    public void glGetBooleanv(int pname, Buffer params) {
        Address address = AddressUtils.of(params);
        AndroidOpenGLES.glGetBooleanv(pname, address);
        params.rewind();
    }
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetBufferParameteriv(target, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetFloatv(int pname, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetFloatv(pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetFramebufferAttachmentParameteriv(target, attachment, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetProgramiv(program, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public String glGetProgramInfoLog(int program) {
        byte[] buffer = new byte[8192];
        IntBuffer len = IntBuffer.allocate(1);
        AndroidOpenGLES.glGetProgramInfoLog(program, 8192, len, buffer);
        return toLogString(buffer, len.get(0));
    }
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address address = Address.ofData(temp);
        AndroidOpenGLES.glGetRenderbufferParameteriv(target, pname, address);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetShaderiv(shader, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public String glGetShaderInfoLog(int shader) {
        byte[] buffer = new byte[8192];
        IntBuffer len = IntBuffer.allocate(1);
        AndroidOpenGLES.glGetShaderInfoLog(shader, 8192, len, buffer);
        return toLogString(buffer, len.get(0));
    }
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        if (range == null || precision == null) return;
        int[] tempRange = new int[range.capacity()];
        int[] tempPrecision = new int[precision.capacity()];
        Address rangeAddr = Address.ofData(tempRange);
        Address precisionAddr = Address.ofData(tempPrecision);
        AndroidOpenGLES.glGetShaderPrecisionFormat(shadertype, precisiontype, rangeAddr, precisionAddr);
        for (int i = 0; i < tempRange.length; i++) {
            range.put(i, tempRange[i]);
        }
        for (int i = 0; i < tempPrecision.length; i++) {
            precision.put(i, tempPrecision[i]);
        }
        range.rewind();
        precision.rewind();
    }
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetTexParameterfv(target, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetTexParameteriv(target, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetUniformfv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetUniformiv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public int glGetUniformLocation(int program, String name) {
        return AndroidOpenGLES.glGetUniformLocation(program, name);
    }
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetVertexAttribfv(index, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetVertexAttribiv(index, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }
    public void glGetVertexAttribPointerv(int index, int pname, Buffer pointer) {
        if (pointer == null) return;
        int[] temp = new int[pointer.capacity()];
        Address addr = Address.ofData(temp);
        AndroidOpenGLES.glGetVertexAttribPointerv(index, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            if (pointer instanceof IntBuffer) {
                ((IntBuffer)pointer).put(i, temp[i]);
            }
        }
        pointer.rewind();
    }
    public boolean glIsBuffer(int buffer) {
        return AndroidOpenGLES.glIsBuffer(buffer);
    }
    public boolean glIsEnabled(int cap) {
        return AndroidOpenGLES.glIsEnabled(cap);
    }
    public boolean glIsFramebuffer(int framebuffer) {
        return AndroidOpenGLES.glIsFramebuffer(framebuffer);
    }
    public boolean glIsProgram(int program) {
        return AndroidOpenGLES.glIsProgram(program);
    }
    public boolean glIsRenderbuffer(int renderbuffer) {
        return AndroidOpenGLES.glIsRenderbuffer(renderbuffer);
    }
    public boolean glIsShader(int shader) {
        return AndroidOpenGLES.glIsShader(shader);
    }
    public boolean glIsTexture(int texture) {
        return AndroidOpenGLES.glIsTexture(texture);
    }
    public void glLinkProgram(int program) {
        AndroidOpenGLES.glLinkProgram(program);
    }
    public void glReleaseShaderCompiler() {
        AndroidOpenGLES.glReleaseShaderCompiler();
    }
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        AndroidOpenGLES.glRenderbufferStorage(target, internalformat, width, height);
    }
    public void glSampleCoverage(float value, boolean invert) {
        AndroidOpenGLES.glSampleCoverage(value, invert);
    }
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        Address shadersAddr = AddressUtils.of(shaders);
        Address binaryAddr = AddressUtils.of(binary);
        AndroidOpenGLES.glShaderBinary(n, shadersAddr, binaryformat, binaryAddr, length);
        shaders.rewind();
        binary.rewind();
    }
    public void glShaderSource(int shader, String string) {
        byte[] sourceBytes = string.getBytes(StandardCharsets.UTF_8);
        byte[] sourceCString = new byte[sourceBytes.length + 1];
        System.arraycopy(sourceBytes, 0, sourceCString, 0, sourceBytes.length);

        byte[] sourcePointer = new byte[Address.sizeOf()];
        Address strings = Address.ofData(sourcePointer);
        Address source = Address.ofData(sourceCString);
        strings.putAddress(source);
        AndroidOpenGLES.glShaderSource(shader, 1, strings, Address.fromInt(0));
    }
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        AndroidOpenGLES.glStencilFuncSeparate(face, func, ref, mask);
    }
    public void glStencilMaskSeparate(int face, int mask) {
        AndroidOpenGLES.glStencilMaskSeparate(face, mask);
    }
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        AndroidOpenGLES.glStencilOpSeparate(face, fail, zfail, zpass);
    }
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        Address address = AddressUtils.of(params);
        AndroidOpenGLES.glTexParameterfv(target, pname, address);
        params.rewind();
    }
    public void glTexParameteri(int target, int pname, int param) {
        AndroidOpenGLES.glTexParameteri(target, pname, param);
    }
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        AndroidOpenGLES.glTexParameteriv(target, pname, address);
        params.rewind();
    }
    public void glUniform1f(int location, float x) {
        AndroidOpenGLES.glUniform1f(location, x);
    }
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform1fv(location, count, address);
        v.rewind();
    }
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform1fv(location, count, address);
    }
    public void glUniform1i(int location, int x) {
        AndroidOpenGLES.glUniform1i(location, x);
    }
    public void glUniform1iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform1iv(location, count, address);
        v.rewind();
    }
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform1iv(location, count, address);
    }
    public void glUniform2f(int location, float x, float y) {
        AndroidOpenGLES.glUniform2f(location, x, y);
    }
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform2fv(location, count, address);
        v.rewind();
    }
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform2fv(location, count, address);
    }
    public void glUniform2i(int location, int x, int y) {
        AndroidOpenGLES.glUniform2i(location, x, y);
    }
    public void glUniform2iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform2iv(location, count, address);
        v.rewind();
    }
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform2iv(location, count, address);
    }
    public void glUniform3f(int location, float x, float y, float z) {
        AndroidOpenGLES.glUniform3f(location, x, y, z);
    }
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform3fv(location, count, address);
        v.rewind();
    }
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform3fv(location, count, address);
    }
    public void glUniform3i(int location, int x, int y, int z) {
        AndroidOpenGLES.glUniform3i(location, x, y, z);
    }
    public void glUniform3iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform3iv(location, count, address);
        v.rewind();
    }
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform3iv(location, count, address);
    }
    public void glUniform4f(int location, float x, float y, float z, float w) {
        AndroidOpenGLES.glUniform4f(location, x, y, z, w);
    }
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform4fv(location, count, address);
        v.rewind();
    }
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform4fv(location, count, address);
    }
    public void glUniform4i(int location, int x, int y, int z, int w) {
        AndroidOpenGLES.glUniform4i(location, x, y, z, w);
    }
    public void glUniform4iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        AndroidOpenGLES.glUniform4iv(location, count, address);
        v.rewind();
    }
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        Address address = Address.ofData(v).add(offset * 4);
        AndroidOpenGLES.glUniform4iv(location, count, address);
    }
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        AndroidOpenGLES.glUniformMatrix2fv(location, count, transpose, address);
        value.rewind();
    }
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        Address address = Address.ofData(value).add(offset * 4);
        AndroidOpenGLES.glUniformMatrix2fv(location, count, transpose, address);
    }
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        AndroidOpenGLES.glUniformMatrix3fv(location, count, transpose, address);
        value.rewind();
    }
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        Address address = Address.ofData(value).add(offset * 4);
        AndroidOpenGLES.glUniformMatrix3fv(location, count, transpose, address);
    }
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        AndroidOpenGLES.glUniformMatrix4fv(location, count, transpose, address);
        value.rewind();
    }
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        Address address = Address.ofData(value).add(offset * 4);
        AndroidOpenGLES.glUniformMatrix4fv(location, count, transpose, address);
    }
    public void glUseProgram(int program) {
        AndroidOpenGLES.glUseProgram(program);
    }
    public void glValidateProgram(int program) {
        AndroidOpenGLES.glValidateProgram(program);
    }
    public void glVertexAttrib1f(int indx, float x) {
        AndroidOpenGLES.glVertexAttrib1f(indx, x);
    }
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        AndroidOpenGLES.glVertexAttrib1fv(indx, address);
        values.rewind();
    }
    public void glVertexAttrib2f(int indx, float x, float y) {
        AndroidOpenGLES.glVertexAttrib2f(indx, x, y);
    }
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        AndroidOpenGLES.glVertexAttrib2fv(indx, address);
        values.rewind();
    }
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        AndroidOpenGLES.glVertexAttrib3f(indx, x, y, z);
    }
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        AndroidOpenGLES.glVertexAttrib3fv(indx, address);
        values.rewind();
    }
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        AndroidOpenGLES.glVertexAttrib4f(indx, x, y, z, w);
    }
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        AndroidOpenGLES.glVertexAttrib4fv(indx, address);
        values.rewind();
    }
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        Address address = AddressUtils.of(ptr);
        AndroidOpenGLES.glVertexAttribPointer(indx, size, type, normalized, stride, address);
        ptr.rewind();
    }
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        AndroidOpenGLES.glVertexAttribPointer(indx, size, type, normalized, stride, Address.fromInt(ptr));
    }

    private static String toLogString(byte[] buffer, int length) {
        if (length <= 0) {
            return "";
        }
        int safeLength = Math.min(length, buffer.length);
        while (safeLength > 0 && buffer[safeLength - 1] == 0) {
            safeLength--;
        }
        return new String(buffer, 0, safeLength, StandardCharsets.UTF_8);
    }
}
