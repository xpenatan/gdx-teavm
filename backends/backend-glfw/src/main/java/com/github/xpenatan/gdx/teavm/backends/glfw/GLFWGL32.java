package com.github.xpenatan.gdx.teavm.backends.glfw;

import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.OpenGL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.teavm.interop.Address;
import org.teavm.interop.Strings;

public class GLFWGL32 implements GL32 {

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
                int remaining = buffer.remaining();
                byte[] data = new byte[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(ShortBuffer buffer) {
            if (buffer.isDirect()) {
                int remaining = buffer.remaining();
                short[] data = new short[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                short[] data = new short[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(IntBuffer buffer) {
            if (buffer.isDirect()) {
                int remaining = buffer.remaining();
                int[] data = new int[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                int[] data = new int[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(LongBuffer buffer) {
            if (buffer.isDirect()) {
                int remaining = buffer.remaining();
                long[] data = new long[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                long[] data = new long[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(FloatBuffer buffer) {
            if (buffer.isDirect()) {
                int remaining = buffer.remaining();
                float[] data = new float[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                float[] data = new float[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(DoubleBuffer buffer) {
            if (buffer.isDirect()) {
                int remaining = buffer.remaining();
                double[] data = new double[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                double[] data = new double[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
        }

        public static Address ofInternal(CharBuffer buffer) {
            if (buffer.isDirect()) {
                int remaining = buffer.remaining();
                char[] data = new char[remaining];
                int pos = buffer.position();
                buffer.get(data, pos, remaining);
                buffer.position(pos); // Restore position
                return Address.ofData(data);
            } else if (buffer.hasArray()) {
                return Address.ofData(buffer.array());
            } else {
                char[] data = new char[buffer.remaining()];
                buffer.get(data);
                return Address.ofData(data);
            }
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

    @Override
    public void glBlendBarrier() {
        OpenGL.glBlendBarrier();
    }

    @Override
    public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        OpenGL.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ, srcWidth, srcHeight, srcDepth);
    }

    @Override
    public void glDebugMessageControl(int source, int type, int severity, IntBuffer ids, boolean enabled) {
        Address address = AddressUtils.of(ids);
        OpenGL.glDebugMessageControl(source, type, severity, address, enabled);
        ids.rewind();
    }

    @Override
    public void glDebugMessageInsert(int source, int type, int id, int severity, String buf) {
        OpenGL.glDebugMessageInsert(source, type, id, severity, buf);
    }

    @Override
    public void glDebugMessageCallback(DebugProc callback) {
        OpenGL.glDebugMessageCallback(callback);
    }

    @Override
    public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities, IntBuffer lengths, ByteBuffer messageLog) {
        Address address = AddressUtils.of(sources);
        Address address2 = AddressUtils.of(types);
        Address address3 = AddressUtils.of(ids);
        Address address4 = AddressUtils.of(severities);
        Address address5 = AddressUtils.of(lengths);
        Address address6 = AddressUtils.of(messageLog);

        int result = OpenGL.glGetDebugMessageLog(count, address, address2, address3, address4, address5, address6);

        sources.rewind();
        types.rewind();
        ids.rewind();
        severities.rewind();
        lengths.rewind();
        messageLog.rewind();

        return result;
    }

    @Override
    public void glPushDebugGroup(int source, int id, String message) {
        OpenGL.glPushDebugGroup(source, id, message);
    }

    @Override
    public void glPopDebugGroup() {
        OpenGL.glPopDebugGroup();
    }

    @Override
    public void glObjectLabel(int identifier, int name, String label) {
        OpenGL.glObjectLabel(identifier, name, label);
    }

    @Override
    public String glGetObjectLabel(int identifier, int name) {
        return OpenGL.glGetObjectLabel(identifier, name);
    }

    @Override
    public long glGetPointerv(int pname) {
        return OpenGL.glGetPointerv(pname);
    }

    @Override
    public void glEnablei(int target, int index) {
        OpenGL.glEnablei(target, index);
    }

    @Override
    public void glDisablei(int target, int index) {
        OpenGL.glDisablei(target, index);
    }

    @Override
    public void glBlendEquationi(int buf, int mode) {
        OpenGL.glBlendEquationi(buf, mode);
    }

    @Override
    public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {
        OpenGL.glBlendEquationSeparatei(buf, modeRGB, modeAlpha);
    }

    @Override
    public void glBlendFunci(int buf, int src, int dst) {
        OpenGL.glBlendFunci(buf, src, dst);
    }

    @Override
    public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        OpenGL.glBlendFuncSeparatei(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {
        OpenGL.glColorMaski(index, r, g, b, a);
    }

    @Override
    public boolean glIsEnabledi(int target, int index) {
        return OpenGL.glIsEnabledi(target, index);
    }

    @Override
    public void glDrawElementsBaseVertex(int mode, int count, int type, Buffer indices, int basevertex) {
        Address address = AddressUtils.of(indices);
        OpenGL.glDrawElementsBaseVertex(mode, count, type, address, basevertex);
        indices.rewind();
    }

    @Override
    public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        Address address = AddressUtils.of(indices);
        OpenGL.glDrawRangeElementsBaseVertex(mode, start, end, count, type, address, basevertex);
        indices.rewind();
    }

    @Override
    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount, int basevertex) {
        Address address = AddressUtils.of(indices);
        OpenGL.glDrawElementsInstancedBaseVertex(mode, count, type, address, instanceCount, basevertex);
        indices.rewind();
    }

    @Override
    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount, int basevertex) {
        OpenGL.glDrawElementsInstancedBaseVertex(mode, count, type, indicesOffset, instanceCount, basevertex);
    }

    @Override
    public void glFramebufferTexture(int target, int attachment, int texture, int level) {
        OpenGL.glFramebufferTexture(target, attachment, texture, level);
    }

    @Override
    public int glGetGraphicsResetStatus() {
        return OpenGL.glGetGraphicsResetStatus();
    }

    @Override
    public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, Buffer data) {
        Address address = AddressUtils.of(data);
        OpenGL.glReadnPixels(x, y, width, height, format, type, bufSize, address);
        data.rewind();
    }

    @Override
    public void glGetnUniformfv(int program, int location, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetnUniformfv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetnUniformiv(int program, int location, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetnUniformiv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetnUniformuiv(int program, int location, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetnUniformuiv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glMinSampleShading(float value) {
        OpenGL.glMinSampleShading(value);
    }

    @Override
    public void glPatchParameteri(int pname, int value) {
        OpenGL.glPatchParameteri(pname, value);
    }

    @Override
    public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glTexParameterIiv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glTexParameterIuiv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetTexParameterIiv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetTexParameterIuiv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        Address address = AddressUtils.of(param);
        OpenGL.glSamplerParameterIiv(sampler, pname, address);
        param.rewind();
    }

    @Override
    public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        Address address = AddressUtils.of(param);
        OpenGL.glSamplerParameterIuiv(sampler, pname, address);
        param.rewind();
    }

    @Override
    public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetSamplerParameterIiv(sampler, pname, address);
        params.rewind();
    }

    @Override
    public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetSamplerParameterIuiv(sampler, pname, address);
        params.rewind();
    }

    @Override
    public void glTexBuffer(int target, int internalformat, int buffer) {
        OpenGL.glTexBuffer(target, internalformat, buffer);
    }

    @Override
    public void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size) {
        OpenGL.glTexBufferRange(target, internalformat, buffer, offset, size);
    }

    @Override
    public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
        OpenGL.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
    }

    @Override
    public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        OpenGL.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
    }

    @Override
    public void glDispatchComputeIndirect(long indirect) {
        OpenGL.glDispatchComputeIndirect(indirect);
    }

    @Override
    public void glDrawArraysIndirect(int mode, long indirect) {
        OpenGL.glDrawArraysIndirect(mode, indirect);
    }

    @Override
    public void glDrawElementsIndirect(int mode, int type, long indirect) {
        OpenGL.glDrawElementsIndirect(mode, type, indirect);
    }

    @Override
    public void glFramebufferParameteri(int target, int pname, int param) {
        OpenGL.glFramebufferParameteri(target, pname, param);
    }

    @Override
    public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetFramebufferParameteriv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetProgramInterfaceiv(program, programInterface, pname, address);
        params.rewind();
    }

    @Override
    public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        return OpenGL.glGetProgramResourceIndex(program, programInterface, name);
    }

    @Override
    public String glGetProgramResourceName(int program, int programInterface, int index) {
        return OpenGL.glGetProgramResourceName(program, programInterface, index);
    }

    @Override
    public void glGetProgramResourceiv(int program, int programInterface, int index, IntBuffer props, IntBuffer length, IntBuffer params) {
        Address propsAddress = AddressUtils.of(props);
        Address lengthAddress = AddressUtils.of(length);
        Address paramsAddress = AddressUtils.of(params);

        OpenGL.glGetProgramResourceiv(program, programInterface, index,
                propsAddress,
                lengthAddress,
                paramsAddress);

        props.rewind();
        length.rewind();
        params.rewind();
    }

    @Override
    public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        return OpenGL.glGetProgramResourceLocation(program, programInterface, name);
    }

    @Override
    public void glUseProgramStages(int pipeline, int stages, int program) {
        OpenGL.glUseProgramStages(pipeline, stages, program);
    }

    @Override
    public void glActiveShaderProgram(int pipeline, int program) {
        OpenGL.glActiveShaderProgram(pipeline, program);
    }

    @Override
    public int glCreateShaderProgramv(int type, String[] strings) {
        return OpenGL.glCreateShaderProgramv(type, strings);
    }

    @Override
    public void glBindProgramPipeline(int pipeline) {
        OpenGL.glBindProgramPipeline(pipeline);
    }

    @Override
    public void glDeleteProgramPipelines(int n, IntBuffer pipelines) {
        for (int i = 0; i < n; i++) {
            int pipeline = pipelines.get(i);
            nullCheck(pipeline, i);
        }
        
        Address address = AddressUtils.of(pipelines);
        OpenGL.glDeleteProgramPipelines(n, address);
        pipelines.rewind();
    }

    @Override
    public void glGenProgramPipelines(int n, IntBuffer pipelines) {
        Address address = AddressUtils.of(pipelines);
        OpenGL.glGenProgramPipelines(n, address);
        pipelines.rewind();
    }

    @Override
    public boolean glIsProgramPipeline(int pipeline) {
        return OpenGL.glIsProgramPipeline(pipeline);
    }

    @Override
    public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetProgramPipelineiv(pipeline, pname, address);
        params.rewind();
    }

    @Override
    public void glProgramUniform1i(int program, int location, int v0) {
        OpenGL.glProgramUniform1i(program, location, v0);
    }

    @Override
    public void glProgramUniform2i(int program, int location, int v0, int v1) {
        OpenGL.glProgramUniform2i(program, location, v0, v1);
    }

    @Override
    public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        OpenGL.glProgramUniform3i(program, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        OpenGL.glProgramUniform4i(program, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1ui(int program, int location, int v0) {
        OpenGL.glProgramUniform1ui(program, location, v0);
    }

    @Override
    public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        OpenGL.glProgramUniform2ui(program, location, v0, v1);
    }

    @Override
    public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        OpenGL.glProgramUniform3ui(program, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        OpenGL.glProgramUniform4ui(program, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1f(int program, int location, float v0) {
        OpenGL.glProgramUniform1f(program, location, v0);
    }

    @Override
    public void glProgramUniform2f(int program, int location, float v0, float v1) {
        OpenGL.glProgramUniform2f(program, location, v0, v1);
    }

    @Override
    public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        OpenGL.glProgramUniform3f(program, location, v0, v1, v2);
    }

    @Override
    public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        OpenGL.glProgramUniform4f(program, location, v0, v1, v2, v3);
    }

    @Override
    public void glProgramUniform1iv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform1iv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform2iv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform2iv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform3iv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform3iv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform4iv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform4iv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform1uiv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform1uiv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform2uiv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform2uiv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform3uiv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform3uiv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform4uiv(int program, int location, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform4uiv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform1fv(int program, int location, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform1fv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform2fv(int program, int location, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform2fv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform3fv(int program, int location, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform3fv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniform4fv(int program, int location, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniform4fv(program, location, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix2fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix2fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix3fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix3fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix4fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix4fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix2x3fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix2x3fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix3x2fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix3x2fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix2x4fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix2x4fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix4x2fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix4x2fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix3x4fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix3x4fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glProgramUniformMatrix4x3fv(int program, int location, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glProgramUniformMatrix4x3fv(program, location, transpose, address);
        value.rewind();
    }

    @Override
    public void glValidateProgramPipeline(int pipeline) {
        OpenGL.glValidateProgramPipeline(pipeline);
    }

    @Override
    public String glGetProgramPipelineInfoLog(int program) {
        return OpenGL.glGetProgramPipelineInfoLog(program);
    }

    @Override
    public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        OpenGL.glBindImageTexture(unit, texture, level, layered, layer, access, format);
    }

    @Override
    public void glGetBooleani_v(int target, int index, IntBuffer data) {
        Address address = AddressUtils.of(data);
        OpenGL.glGetBooleani_v(target, index, address);
        data.rewind();
    }

    @Override
    public void glMemoryBarrier(int barriers) {
        OpenGL.glMemoryBarrier(barriers);
    }

    @Override
    public void glMemoryBarrierByRegion(int barriers) {
        OpenGL.glMemoryBarrierByRegion(barriers);
    }

    @Override
    public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
        OpenGL.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
    }

    @Override
    public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        Address address = AddressUtils.of(val);
        OpenGL.glGetMultisamplefv(pname, index, address);
        val.rewind();
    }

    @Override
    public void glSampleMaski(int maskNumber, int mask) {
        OpenGL.glSampleMaski(maskNumber, mask);
    }

    @Override
    public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetTexLevelParameteriv(target, level, pname, address);
        params.rewind();
    }

    @Override
    public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetTexLevelParameterfv(target, level, pname, address);
        params.rewind();
    }

    @Override
    public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        OpenGL.glBindVertexBuffer(bindingindex, buffer, offset, stride);
    }

    @Override
    public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        OpenGL.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
    }

    @Override
    public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        OpenGL.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
    }

    @Override
    public void glVertexAttribBinding(int attribindex, int bindingindex) {
        OpenGL.glVertexAttribBinding(attribindex, bindingindex);
    }

    @Override
    public void glVertexBindingDivisor(int bindingindex, int divisor) {
        OpenGL.glVertexBindingDivisor(bindingindex, divisor);
    }

    @Override
    public void glReadBuffer(int mode) {
        OpenGL.glReadBuffer(mode);
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        Address address = AddressUtils.of(indices);
        OpenGL.glDrawRangeElements(mode, start, end, count, type, address);
        indices.rewind();
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        OpenGL.glDrawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, int offset) {
        OpenGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, offset);
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        Address address = AddressUtils.of(pixels);
        OpenGL.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, address);
        pixels.rewind();
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {
        OpenGL.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, int offset) {
        OpenGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, offset);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        Address address = AddressUtils.of(pixels);
        OpenGL.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, address);
        pixels.rewind();
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        OpenGL.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        OpenGL.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override
    public void glGenQueries(int n, int[] ids, int offset) {
        OpenGL.glGenQueries(n, Address.ofData(ids), offset);
    }

    @Override
    public void glGenQueries(int n, IntBuffer ids) {
        Address address = AddressUtils.of(ids);
        OpenGL.glGenQueries(n, address, 0);
        ids.rewind();
    }

    @Override
    public void glDeleteQueries(int n, int[] ids, int offset) {
        for (int i = 0; i < n; i++) {
            nullCheck(ids[offset + i], i);
        }
        OpenGL.glDeleteQueries(n, Address.ofData(ids), offset);
    }

    @Override
    public void glDeleteQueries(int n, IntBuffer ids) {
        for (int i = 0; i < n; i++) {
            nullCheck(ids.get(n), n);
        }
        
        Address address = AddressUtils.of(ids);
        OpenGL.glDeleteQueries(n, address, 0);
        ids.rewind();
    }

    @Override
    public boolean glIsQuery(int id) {
        return OpenGL.glIsQuery(id);
    }

    @Override
    public void glBeginQuery(int target, int id) {
        nullCheck(id, 0);
        OpenGL.glBeginQuery(target, id);
    }

    @Override
    public void glEndQuery(int target) {
        OpenGL.glEndQuery(target);
    }

    @Override
    public void glGetQueryiv(int target, int pname, IntBuffer params) {
        nullCheck(params);
        
        Address address = AddressUtils.of(params);
        OpenGL.glGetQueryiv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        nullCheck(params);
        
        Address address = AddressUtils.of(params);
        OpenGL.glGetQueryObjectuiv(id, pname, address);
        params.rewind();
    }

    @Override
    public boolean glUnmapBuffer(int target) {
        return OpenGL.glUnmapBuffer(target);
    }

    @Override
    public Buffer glGetBufferPointerv(int target, int pname) {
        System.err.println("glGetBufferPointerv not supported");
        return null; // TODO: implement this
    }

    @Override
    public void glDrawBuffers(int n, IntBuffer bufs) {
        nullCheck(bufs);
        
        Address address = AddressUtils.of(bufs);
        OpenGL.glDrawBuffers(n, address);
        bufs.rewind();
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        nullCheck(location);
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix2x3fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        nullCheck(location);
        
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix3x2fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        nullCheck(location);
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix2x4fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        nullCheck(location);
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix4x2fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        nullCheck(location);
        
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix3x4fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        nullCheck(location);
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix4x3fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        OpenGL.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        OpenGL.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        OpenGL.glFramebufferTextureLayer(target, attachment, texture, level, layer);
    }

    @Override
    public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return null; // TODO: implement this
    }

    @Override
    public void glFlushMappedBufferRange(int target, int offset, int length) {
        OpenGL.glFlushMappedBufferRange(target, offset, length);
    }

    @Override
    public void glBindVertexArray(int array) {
        OpenGL.glBindVertexArray(array);
    }

    @Override
    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        nullCheck(arrays);
        OpenGL.glDeleteVertexArrays(n, IntBuffer.wrap(arrays, offset, n));
    }

    @Override
    public void glDeleteVertexArrays(int n, IntBuffer arrays) {
        nullCheck(arrays);

        OpenGL.glDeleteVertexArrays(n, arrays);
    }

    @Override
    public void glGenVertexArrays(int n, int[] arrays, int offset) {
        OpenGL.glGenVertexArrays(n, IntBuffer.wrap(arrays, offset, n));
    }

    @Override
    public void glGenVertexArrays(int n, IntBuffer arrays) {
        OpenGL.glGenVertexArrays(n, arrays);
    }

    @Override
    public boolean glIsVertexArray(int array) {
        return OpenGL.glIsVertexArray(array);
    }

    @Override
    public void glBeginTransformFeedback(int primitiveMode) {
        OpenGL.glBeginTransformFeedback(primitiveMode);
    }

    @Override
    public void glEndTransformFeedback() {
        OpenGL.glEndTransformFeedback();
    }

    @Override
    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        OpenGL.glBindBufferRange(target, index, buffer, offset, size);
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        OpenGL.glBindBufferBase(target, index, buffer);
    }

    @Override
    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        OpenGL.glTransformFeedbackVaryings(program, varyings, bufferMode);
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        OpenGL.glVertexAttribIPointer(index, size, type, stride, offset);
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetVertexAttribIiv(index, pname, address);
        params.rewind();
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetVertexAttribIuiv(index, pname, address);
        params.rewind();
    }

    @Override
    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        OpenGL.glVertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        OpenGL.glVertexAttribI4ui(index, x, y, z, w);
    }

    @Override
    public void glGetUniformuiv(int program, int location, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetUniformuiv(program, location, address);
        params.rewind();
    }

    @Override
    public int glGetFragDataLocation(int program, String name) {
        return OpenGL.glGetFragDataLocation(program, name);
    }

    @Override
    public void glUniform1uiv(int location, int count, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glUniform1uiv(location, count, address);
        value.rewind();
    }

    @Override
    public void glUniform3uiv(int location, int count, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glUniform3uiv(location, count, address);
        value.rewind();
    }

    @Override
    public void glUniform4uiv(int location, int count, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glUniform4uiv(location, count, address);
        value.rewind();
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glClearBufferiv(buffer, drawbuffer, address);
        value.rewind();
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glClearBufferuiv(buffer, drawbuffer, address);
        value.rewind();
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glClearBufferfv(buffer, drawbuffer, address);
        value.rewind();
    }

    @Override
    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        OpenGL.glClearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public String glGetStringi(int name, int index) {
        return OpenGL.glGetStringi(name, index);
    }

    @Override
    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        OpenGL.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        Address address = AddressUtils.of(uniformIndices);
        OpenGL.glGetUniformIndices(program, uniformNames, address);
        uniformIndices.rewind();
    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        Address indicesAddress = AddressUtils.of(uniformIndices);
        Address paramsAddress = AddressUtils.of(params);
        OpenGL.glGetActiveUniformsiv(program, uniformCount, indicesAddress, pname, paramsAddress);
        uniformIndices.rewind();
        params.rewind();
    }

    @Override
    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return OpenGL.glGetUniformBlockIndex(program, uniformBlockName);
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, address);
        params.rewind();
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        Address lengthAddress = AddressUtils.of(length);
        Address nameAddress = AddressUtils.of(uniformBlockName);
        OpenGL.glGetActiveUniformBlockName(program, uniformBlockIndex, lengthAddress, nameAddress);
        length.rewind();
        uniformBlockName.rewind();
    }

    @Override
    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return OpenGL.glGetActiveUniformBlockName(program, uniformBlockIndex);
    }

    @Override
    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        OpenGL.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        OpenGL.glDrawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        OpenGL.glDrawElementsInstanced(mode, count, type, Address.fromInt(indicesOffset), instanceCount);
    }

    @Override
    public void glGetInteger64v(int pname, LongBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetInteger64v(pname, address);
        params.rewind();
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetBufferParameteri64v(target, pname, address);
        params.rewind();
    }

    @Override
    public void glGenSamplers(int count, int[] samplers, int offset) {
        OpenGL.glGenSamplers(count, Address.ofData(samplers), offset);
    }

    @Override
    public void glGenSamplers(int count, IntBuffer samplers) {
        Address address = AddressUtils.of(samplers);
        OpenGL.glGenSamplers(count, address, 0);
        samplers.rewind();
    }

    @Override
    public void glDeleteSamplers(int count, int[] samplers, int offset) {
        OpenGL.glDeleteSamplers(count, Address.ofData(samplers), offset);
    }

    @Override
    public void glDeleteSamplers(int count, IntBuffer samplers) {
        Address address = AddressUtils.of(samplers);
        OpenGL.glDeleteSamplers(count, address, 0);
        samplers.rewind();
    }

    @Override
    public boolean glIsSampler(int sampler) {
        return OpenGL.glIsSampler(sampler);
    }

    @Override
    public void glBindSampler(int unit, int sampler) {
        OpenGL.glBindSampler(unit, sampler);
    }

    @Override
    public void glSamplerParameteri(int sampler, int pname, int param) {
        OpenGL.glSamplerParameteri(sampler, pname, param);
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        Address address = AddressUtils.of(param);
        OpenGL.glSamplerParameteriv(sampler, pname, address);
        param.rewind();
    }

    @Override
    public void glSamplerParameterf(int sampler, int pname, float param) {
        OpenGL.glSamplerParameterf(sampler, pname, param);
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        Address address = AddressUtils.of(param);
        OpenGL.glSamplerParameterfv(sampler, pname, address);
        param.rewind();
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetSamplerParameteriv(sampler, pname, address);
        params.rewind();
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetSamplerParameterfv(sampler, pname, address);
        params.rewind();
    }

    @Override
    public void glVertexAttribDivisor(int index, int divisor) {
        OpenGL.glVertexAttribDivisor(index, divisor);
    }

    @Override
    public void glBindTransformFeedback(int target, int id) {
        OpenGL.glBindTransformFeedback(target, id);
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        OpenGL.glDeleteTransformFeedbacks(n, IntBuffer.wrap(ids, offset, n));
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        OpenGL.glDeleteTransformFeedbacks(n, ids);
    }

    @Override
    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        OpenGL.glGenTransformFeedbacks(n, IntBuffer.wrap(ids, offset, n));
    }

    @Override
    public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        OpenGL.glGenTransformFeedbacks(n, ids);
    }

    @Override
    public boolean glIsTransformFeedback(int id) {
        return OpenGL.glIsTransformFeedback(id);
    }

    @Override
    public void glPauseTransformFeedback() {
        OpenGL.glPauseTransformFeedback();
    }

    @Override
    public void glResumeTransformFeedback() {
        OpenGL.glResumeTransformFeedback();
    }

    @Override
    public void glProgramParameteri(int program, int pname, int value) {
        OpenGL.glProgramParameteri(program, pname, value);
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        Address address = AddressUtils.of(attachments);
        OpenGL.glInvalidateFramebuffer(target, numAttachments, address);
        attachments.rewind();
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {
        Address address = AddressUtils.of(attachments);
        OpenGL.glInvalidateSubFramebuffer(target, numAttachments, address, x, y, width, height);
        attachments.rewind();
    }

    @Override
    public void glActiveTexture(int texture) {
        OpenGL.glActiveTexture(texture);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        OpenGL.glBindTexture(target, texture);
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        OpenGL.glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void glClear(int mask) {
        OpenGL.glClear(mask);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        OpenGL.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glClearDepthf(float depth) {
        OpenGL.glClearDepthf(depth);
    }

    @Override
    public void glClearStencil(int s) {
        OpenGL.glClearStencil(s);
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        OpenGL.glColorMask(red, green, blue, alpha);
    }

    @Override
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border, int imageSize, Buffer data) {
//        if (data instanceof ByteBuffer) {
//            OpenGL.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, (ByteBuffer)data);
//        } else {
//            throw new GdxRuntimeException("Can't use " + data.getClass().getName() + " with this method. Use ByteBuffer instead.");
//        }
    }

    @Override
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int imageSize, Buffer data) {
//        if (data instanceof ByteBuffer) {
//            OpenGL.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, (ByteBuffer)data);
//        } else {
//            throw new GdxRuntimeException("Can't use " + data.getClass().getName() + " with this method. Use ByteBuffer instead.");
//        }
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        OpenGL.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        OpenGL.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override
    public void glCullFace(int mode) {
        OpenGL.glCullFace(mode);
    }

    @Override
    public void glDeleteTextures(int n, IntBuffer textures) {
        Address address = AddressUtils.of(textures);
        OpenGL.glDeleteTextures(n, address);
        textures.rewind();
    }

    @Override
    public void glDeleteTexture(int texture) {
        IntBuffer textures = IntBuffer.allocate(1);
        textures.put(texture);
        OpenGL.glDeleteTextures(1, textures);
    }

    @Override
    public void glDepthFunc(int func) {
        OpenGL.glDepthFunc(func);
    }

    @Override
    public void glDepthMask(boolean flag) {
        OpenGL.glDepthMask(flag);
    }

    @Override
    public void glDepthRangef(float zNear, float zFar) {
        OpenGL.glDepthRangef(zNear, zFar);
    }

    @Override
    public void glDisable(int cap) {
        OpenGL.glDisable(cap);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        OpenGL.glDrawArrays(mode, first, count);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        Address address = AddressUtils.of(indices);
        OpenGL.glDrawElements(mode, count, type, address);
        indices.rewind();
    }

    @Override
    public void glEnable(int cap) {
        OpenGL.glEnable(cap);
    }

    @Override
    public void glFinish() {
        OpenGL.glFinish();
    }

    @Override
    public void glFlush() {
        OpenGL.glFlush();
    }

    @Override
    public void glFrontFace(int mode) {
        OpenGL.glFrontFace(mode);
    }

    @Override
    public void glGenTextures(int n, IntBuffer textures) {
        Address address = AddressUtils.of(textures);
        OpenGL.glGenTextures(n, address);
        textures.rewind();
    }

    @Override
    public int glGenTexture() {
        IntBuffer texture = IntBuffer.allocate(1);
        OpenGL.glGenTextures(1, texture);
        return texture.get(0);
    }

    @Override
    public int glGetError() {
        return OpenGL.glGetError();
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetIntegerv(pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public String glGetString(int name) {
        return OpenGL.glGetString(name);
    }

    @Override
    public void glHint(int target, int mode) {
        OpenGL.glHint(target, mode);
    }

    @Override
    public void glLineWidth(float width) {
        OpenGL.glLineWidth(width);
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        OpenGL.glPixelStorei(pname, param);
    }

    @Override
    public void glPolygonOffset(float factor, float units) {
        OpenGL.glPolygonOffset(factor, units);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
//        if (pixels instanceof ByteBuffer)
//            OpenGL.glReadPixels(x, y, width, height, format, type, (ByteBuffer)pixels);
//        else if (pixels instanceof ShortBuffer)
//            OpenGL.glReadPixels(x, y, width, height, format, type, (ShortBuffer)pixels);
//        else if (pixels instanceof IntBuffer)
//            OpenGL.glReadPixels(x, y, width, height, format, type, (IntBuffer)pixels);
//        else if (pixels instanceof FloatBuffer)
//            OpenGL.glReadPixels(x, y, width, height, format, type, (FloatBuffer)pixels);
//        else
//            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
//                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer or FloatBuffer instead.");
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        OpenGL.glScissor(x, y, width, height);
    }

    @Override
    public void glStencilFunc(int func, int ref, int mask) {
        OpenGL.glStencilFunc(func, ref, mask);
    }

    @Override
    public void glStencilMask(int mask) {
        OpenGL.glStencilMask(mask);
    }

    @Override
    public void glStencilOp(int fail, int zfail, int zpass) {
        OpenGL.glStencilOp(fail, zfail, zpass);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer || pixels == null)
            OpenGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer)pixels);
        else if (pixels instanceof ShortBuffer)
            OpenGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ShortBuffer)pixels);
        else if (pixels instanceof IntBuffer)
            OpenGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, (IntBuffer)pixels);
        else if (pixels instanceof FloatBuffer)
            OpenGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, (FloatBuffer)pixels);
        else if (pixels instanceof DoubleBuffer)
            OpenGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, (DoubleBuffer)pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead");
    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        OpenGL.glTexParameterf(target, pname, param);
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer || pixels == null)
            OpenGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ByteBuffer)pixels);
        else if (pixels instanceof ShortBuffer)
            OpenGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (ByteBuffer)pixels);
        else if (pixels instanceof IntBuffer)
            OpenGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (IntBuffer)pixels);
        else if (pixels instanceof FloatBuffer)
            OpenGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (FloatBuffer)pixels);
        else if (pixels instanceof DoubleBuffer)
            OpenGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, (DoubleBuffer)pixels);
        else
            throw new GdxRuntimeException("Can't use " + pixels.getClass().getName()
                    + " with this method. Use ByteBuffer, ShortBuffer, IntBuffer, FloatBuffer or DoubleBuffer instead. Blame LWJGL");
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        OpenGL.glViewport(x, y, width, height);
    }

    @Override
    public void glAttachShader(int program, int shader) {
        OpenGL.glAttachShader(program, shader);
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        OpenGL.glBindAttribLocation(program, index, name);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        OpenGL.glBindBuffer(target, buffer);
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        OpenGL.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        OpenGL.glBindRenderbuffer(target, renderbuffer);
    }

    @Override
    public void glBlendColor(float red, float green, float blue, float alpha) {
        OpenGL.glBlendColor(red, green, blue, alpha);
    }

    @Override
    public void glBlendEquation(int mode) {
        OpenGL.glBlendEquation(mode);
    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        OpenGL.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        OpenGL.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        if (data instanceof ByteBuffer || data == null)
            OpenGL.glBufferData(target, size, (ByteBuffer)data, usage);
        else if (data instanceof IntBuffer)
            OpenGL.glBufferData(target, size, (IntBuffer)data, usage);
        else if (data instanceof FloatBuffer)
            OpenGL.glBufferData(target, size, (FloatBuffer)data, usage);
        else if (data instanceof DoubleBuffer)
            OpenGL.glBufferData(target, size, (DoubleBuffer)data, usage);
        else if (data instanceof ShortBuffer)
            OpenGL.glBufferData(target, size, (ShortBuffer)data, usage);
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        if (data instanceof ByteBuffer || data == null)
            OpenGL.glBufferSubData(target, offset, size, (ByteBuffer)data);
        else if (data instanceof IntBuffer)
            OpenGL.glBufferSubData(target, offset, size, (IntBuffer)data);
        else if (data instanceof FloatBuffer)
            OpenGL.glBufferSubData(target, offset, size, (FloatBuffer)data);
        else if (data instanceof DoubleBuffer)
            OpenGL.glBufferSubData(target, offset, size, (DoubleBuffer)data);
        else if (data instanceof ShortBuffer)
            OpenGL.glBufferSubData(target, offset, size, (ShortBuffer)data);
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        return OpenGL.glCheckFramebufferStatus(target);
    }

    @Override
    public void glCompileShader(int shader) {
        OpenGL.glCompileShader(shader);
    }

    @Override
    public int glCreateProgram() {
        return OpenGL.glCreateProgram();
    }

    @Override
    public int glCreateShader(int type) {
        return OpenGL.glCreateShader(type);
    }

    @Override
    public void glDeleteBuffer(int buffer) {
        IntBuffer buffers = IntBuffer.allocate(1);
        buffers.put(buffer);
        OpenGL.glDeleteBuffers(1, buffers);
    }

    @Override
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        Address address = AddressUtils.of(buffers);
        OpenGL.glDeleteBuffers(n, address);
        buffers.rewind();
    }

    @Override
    public void glDeleteFramebuffer(int framebuffer) {
        IntBuffer framebuffers = IntBuffer.allocate(1);
        framebuffers.put(framebuffer);
        OpenGL.glDeleteFramebuffers(1, framebuffers);
    }

    @Override
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        OpenGL.glDeleteFramebuffers(n, framebuffers);
    }

    @Override
    public void glDeleteProgram(int program) {
        nullCheck(program);
        
        OpenGL.glDeleteProgram(program);
    }

    @Override
    public void glDeleteRenderbuffer(int renderbuffer) {
        IntBuffer renderbuffers = IntBuffer.allocate(1);
        renderbuffers.put(renderbuffer);
        OpenGL.glDeleteRenderbuffers(1, renderbuffers);
    }

    @Override
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        OpenGL.glDeleteRenderbuffers(n, renderbuffers);
    }

    @Override
    public void glDeleteShader(int shader) {
        nullCheck(shader);
        OpenGL.glDeleteShader(shader);
    }

    @Override
    public void glDetachShader(int program, int shader) {
        OpenGL.glDetachShader(program, shader);
    }

    @Override
    public void glDisableVertexAttribArray(int index) {
        OpenGL.glDisableVertexAttribArray(index);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, int indices) {
        OpenGL.glDrawElements(mode, count, type, Address.fromInt(indices));
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        OpenGL.glEnableVertexAttribArray(index);
    }

    @Override
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        OpenGL.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        OpenGL.glFramebufferTexture2D(target, attachment, textarget, texture, level);
    }

    @Override
    public int glGenBuffer() {
        IntBuffer buffer = IntBuffer.allocate(1);
        OpenGL.glGenBuffers(1, buffer);
        return buffer.get(0);
    }

    @Override
    public void glGenBuffers(int n, IntBuffer buffers) {
        Address address = AddressUtils.of(buffers);
        OpenGL.glGenBuffers(n, address);
        buffers.rewind();
    }

    @Override
    public void glGenerateMipmap(int target) {
        OpenGL.glGenerateMipmap(target);
    }

    @Override
    public int glGenFramebuffer() {
        IntBuffer buffer = IntBuffer.allocate(1);
        OpenGL.glGenFramebuffers(1, buffer);
        return buffer.get(0);
    }

    @Override
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        OpenGL.glGenFramebuffers(n, framebuffers);
    }

    @Override
    public int glGenRenderbuffer() {
        IntBuffer buffer = IntBuffer.allocate(1);
        OpenGL.glGenRenderbuffers(1, buffer);
        return buffer.get(0);
    }

    @Override
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        OpenGL.glGenRenderbuffers(n, renderbuffers);
    }

    @Override
    public String glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        // TeaVM C backend doesn't properly handle CharBuffer in native calls
        // Use byte array instead for proper marshalling
        IntBuffer lenBuf = IntBuffer.allocate(1);
        byte[] nameBytes = new byte[512];

        OpenGL.glGetActiveAttrib(program, index, 512, lenBuf, size, type, nameBytes);

        int nameLength = lenBuf.get(0);
        String name = "";

        if (nameLength > 0 && nameLength <= 512) {
            name = new String(nameBytes, 0, nameLength);
        }

        return name;
    }

    @Override
    public String glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        // Use ByteBuffer instead of CharBuffer
        ByteBuffer nameBufBytes = ByteBuffer.allocate(512);
        IntBuffer lenBuf = IntBuffer.allocate(1);

        CharBuffer nameBuf = nameBufBytes.asCharBuffer();
        OpenGL.glGetActiveUniform(program, index, 512, lenBuf, size, type, nameBuf);

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

    @Override
    public void glGetAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
        OpenGL.glGetAttachedShaders(program, maxcount, count, shaders);
    }

    @Override
    public int glGetAttribLocation(int program, String name) {
        return OpenGL.glGetAttribLocation(program, name);
    }

    @Override
    public void glGetBooleanv(int pname, Buffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glGetBooleanv(pname, address);
        params.rewind();
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetBufferParameteriv(target, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetFloatv(pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetFramebufferAttachmentParameteriv(target, attachment, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetProgramiv(program, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        CharBuffer buffer = CharBuffer.allocate(8192);
        IntBuffer len = IntBuffer.allocate(1);
        OpenGL.glGetProgramInfoLog(program, 8192, len, buffer);
        return buffer.limit(len.get(0)).toString();
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address address = Address.ofData(temp);
        OpenGL.glGetRenderbufferParameteriv(target, pname, address);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetShaderiv(shader, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        CharBuffer buffer = CharBuffer.allocate(8192);
        IntBuffer len = IntBuffer.allocate(1);
        OpenGL.glGetShaderInfoLog(shader, 8192, len, buffer);
        return buffer.limit(len.get(0)).toString();
    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        if (range == null || precision == null) return;
        int[] tempRange = new int[range.capacity()];
        int[] tempPrecision = new int[precision.capacity()];
        Address rangeAddr = Address.ofData(tempRange);
        Address precisionAddr = Address.ofData(tempPrecision);
        OpenGL.glGetShaderPrecisionFormat(shadertype, precisiontype, rangeAddr, precisionAddr);
        for (int i = 0; i < tempRange.length; i++) {
            range.put(i, tempRange[i]);
        }
        for (int i = 0; i < tempPrecision.length; i++) {
            precision.put(i, tempPrecision[i]);
        }
        range.rewind();
        precision.rewind();
    }

    @Override
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetTexParameterfv(target, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetTexParameteriv(target, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetUniformfv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetUniformiv(program, location, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        return OpenGL.glGetUniformLocation(program, name);
    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        if (params == null) return;
        float[] temp = new float[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetVertexAttribfv(index, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        if (params == null) return;
        int[] temp = new int[params.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetVertexAttribiv(index, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            params.put(i, temp[i]);
        }
        params.rewind();
    }

    @Override
    public void glGetVertexAttribPointerv(int index, int pname, Buffer pointer) {
        if (pointer == null) return;
        int[] temp = new int[pointer.capacity()];
        Address addr = Address.ofData(temp);
        OpenGL.glGetVertexAttribPointerv(index, pname, addr);
        for (int i = 0; i < temp.length; i++) {
            if (pointer instanceof IntBuffer) {
                ((IntBuffer)pointer).put(i, temp[i]);
            }
        }
        pointer.rewind();
    }

    @Override
    public boolean glIsBuffer(int buffer) {
        return OpenGL.glIsBuffer(buffer);
    }

    @Override
    public boolean glIsEnabled(int cap) {
        return OpenGL.glIsEnabled(cap);
    }

    @Override
    public boolean glIsFramebuffer(int framebuffer) {
        return OpenGL.glIsFramebuffer(framebuffer);
    }

    @Override
    public boolean glIsProgram(int program) {
        return OpenGL.glIsProgram(program);
    }

    @Override
    public boolean glIsRenderbuffer(int renderbuffer) {
        return OpenGL.glIsRenderbuffer(renderbuffer);
    }

    @Override
    public boolean glIsShader(int shader) {
        return OpenGL.glIsShader(shader);
    }

    @Override
    public boolean glIsTexture(int texture) {
        return OpenGL.glIsTexture(texture);
    }

    @Override
    public void glLinkProgram(int program) {
        OpenGL.glLinkProgram(program);
    }

    @Override
    public void glReleaseShaderCompiler() {
        OpenGL.glReleaseShaderCompiler();
    }

    @Override
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        OpenGL.glRenderbufferStorage(target, internalformat, width, height);
    }

    @Override
    public void glSampleCoverage(float value, boolean invert) {
        OpenGL.glSampleCoverage(value, invert);
    }

    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        Address shadersAddr = AddressUtils.of(shaders);
        Address binaryAddr = AddressUtils.of(binary);
        OpenGL.glShaderBinary(n, shadersAddr, binaryformat, binaryAddr, length);
        shaders.rewind();
        binary.rewind();
    }

    @Override
    public void glShaderSource(int shader, String string) {
        Address strings = Address.ofData(new byte[Address.sizeOf()]);
        Address source = Strings.toC(string);
        strings.putAddress(source);
        IntBuffer len = IntBuffer.allocate(1);
        len.put(0, string.length());
        OpenGL.glShaderSource(shader, 1, strings, len);
    }

    @Override
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        OpenGL.glStencilFuncSeparate(face, func, ref, mask);
    }

    @Override
    public void glStencilMaskSeparate(int face, int mask) {
        OpenGL.glStencilMaskSeparate(face, mask);
    }

    @Override
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        OpenGL.glStencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glTexParameterfv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        OpenGL.glTexParameteri(target, pname, param);
    }

    @Override
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        Address address = AddressUtils.of(params);
        OpenGL.glTexParameteriv(target, pname, address);
        params.rewind();
    }

    @Override
    public void glUniform1f(int location, float x) {
        OpenGL.glUniform1f(location, x);
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform1fv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        OpenGL.glUniform1fv(location, count, Address.ofData(v), offset);
    }

    @Override
    public void glUniform1i(int location, int x) {
        OpenGL.glUniform1i(location, x);
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform1iv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        OpenGL.glUniform1iv(location, count, Address.ofData(v), offset);
    }

    @Override
    public void glUniform2f(int location, float x, float y) {
        OpenGL.glUniform2f(location, x, y);
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform2fv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        OpenGL.glUniform2fv(location, count, Address.ofData(v), offset);
    }

    @Override
    public void glUniform2i(int location, int x, int y) {
        OpenGL.glUniform2i(location, x, y);
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform2iv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        OpenGL.glUniform2iv(location, count, Address.ofData(v), offset);
    }

    @Override
    public void glUniform3f(int location, float x, float y, float z) {
        OpenGL.glUniform3f(location, x, y, z);
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform3fv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        Address address = Address.ofData(v);
        address.add(offset * 4); // offset is in floats, but address.add() expects bytes
        OpenGL.glUniform3fv(location, count, address);
    }

    @Override
    public void glUniform3i(int location, int x, int y, int z) {
        OpenGL.glUniform3i(location, x, y, z);
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform3iv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        OpenGL.glUniform3iv(location, count, Address.ofData(v), offset);
    }

    @Override
    public void glUniform4f(int location, float x, float y, float z, float w) {
        OpenGL.glUniform4f(location, x, y, z, w);
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform4fv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        Address address = Address.ofData(v);
        address.add(offset * 4); // offset is in floats, but address.add() expects bytes
        OpenGL.glUniform4fv(location, count, address);
    }

    @Override
    public void glUniform4i(int location, int x, int y, int z, int w) {
        OpenGL.glUniform4i(location, x, y, z, w);
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer v) {
        Address address = AddressUtils.of(v);
        OpenGL.glUniform4iv(location, count, address);
        v.rewind();
    }

    @Override
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        Address address = Address.ofData(v);
        address.add(offset * 4); // offset is in ints, but address.add() expects bytes
        OpenGL.glUniform4iv(location, count, address);
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix2fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        Address address = Address.ofData(value);
        address.add(offset * 4); // offset is in floats, but address.add() expects bytes
        OpenGL.glUniformMatrix2fv(location, count, transpose, address);
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix3fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        Address address = Address.ofData(value);
        address.add(offset * 4); // offset is in floats, but address.add() expects bytes
        OpenGL.glUniformMatrix3fv(location, count, transpose, address);
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        Address address = AddressUtils.of(value);
        OpenGL.glUniformMatrix4fv(location, count, transpose, address);
        value.rewind();
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        Address address = Address.ofData(value);
        address.add(offset * 4); // offset is in floats, but address.add() expects bytes, so multiply by 4
        OpenGL.glUniformMatrix4fv(location, count, transpose, address);
    }

    @Override
    public void glUseProgram(int program) {
        OpenGL.glUseProgram(program);
    }

    @Override
    public void glValidateProgram(int program) {
        OpenGL.glValidateProgram(program);
    }

    @Override
    public void glVertexAttrib1f(int indx, float x) {
        OpenGL.glVertexAttrib1f(indx, x);
    }

    @Override
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        OpenGL.glVertexAttrib1fv(indx, address);
        values.rewind();
    }

    @Override
    public void glVertexAttrib2f(int indx, float x, float y) {
        OpenGL.glVertexAttrib2f(indx, x, y);
    }

    @Override
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        OpenGL.glVertexAttrib2fv(indx, address);
        values.rewind();
    }

    @Override
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        OpenGL.glVertexAttrib3f(indx, x, y, z);
    }

    @Override
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        OpenGL.glVertexAttrib3fv(indx, address);
        values.rewind();
    }

    @Override
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        OpenGL.glVertexAttrib4f(indx, x, y, z, w);
    }

    @Override
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        Address address = AddressUtils.of(values);
        OpenGL.glVertexAttrib4fv(indx, address);
        values.rewind();
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        Address address = AddressUtils.of(ptr);
        OpenGL.glVertexAttribPointer(indx, size, type, normalized, stride, address);
        ptr.rewind();
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        OpenGL.glVertexAttribPointer(indx, size, type, normalized, stride, Address.fromInt(ptr));
    }

    private static native Address ofData(ByteBuffer buffer);
}
