package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.debug;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL31;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL31DebugInterceptor extends GL30DebugInterceptor implements GL31 {

    final GL31 gl31;

    public GL31DebugInterceptor(GLDebugProfiler glProfiler, GL31 gl31) {
        super(glProfiler, gl31);
        this.gl31 = gl31;
    }

    protected void check() {
        int error = gl30.glGetError();
        while(error != GL20.GL_NO_ERROR) {
            glProfiler.getListener().onError(error);
            error = gl30.glGetError();
        }
    }

    public void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        calls++;
        incrementMethod("glDispatchCompute");
        gl31.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
        check();
    }

    public void glDispatchComputeIndirect(long indirect) {
        calls++;
        incrementMethod("glDispatchComputeIndirect");
        gl31.glDispatchComputeIndirect(indirect);
        check();
    }

    public void glDrawArraysIndirect(int mode, long indirect) {
        drawCalls++;
        calls++;
        incrementMethod("glDrawArraysIndirect");
        gl31.glDrawArraysIndirect(mode, indirect);
        check();
    }

    public void glDrawElementsIndirect(int mode, int type, long indirect) {
        drawCalls++;
        calls++;
        incrementMethod("glDrawElementsIndirect");
        gl31.glDrawElementsIndirect(mode, type, indirect);
        check();
    }

    public void glFramebufferParameteri(int target, int pname, int param) {
        calls++;
        incrementMethod("glFramebufferParameteri");
        gl31.glFramebufferParameteri(target, pname, param);
        check();
    }

    public void glGetFramebufferParameteriv(int target, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetFramebufferParameteriv");
        gl31.glGetFramebufferParameteriv(target, pname, params);
        check();
    }

    public void glGetProgramInterfaceiv(int program, int programInterface, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetProgramInterfaceiv");
        gl31.glGetProgramInterfaceiv(program, programInterface, pname, params);
        check();
    }

    public int glGetProgramResourceIndex(int program, int programInterface, String name) {
        calls++;
        incrementMethod("glGetProgramResourceIndex");
        int v = gl31.glGetProgramResourceIndex(program, programInterface, name);
        check();
        return v;
    }

    public String glGetProgramResourceName(int program, int programInterface, int index) {
        calls++;
        incrementMethod("glGetProgramResourceName");
        String s = gl31.glGetProgramResourceName(program, programInterface, index);
        check();
        return s;
    }

    public void glGetProgramResourceiv(int program, int programInterface, int index, IntBuffer props, IntBuffer length,
                                       IntBuffer params) {
        calls++;
        incrementMethod("glGetProgramResourceiv");
        gl31.glGetProgramResourceiv(program, programInterface, index, props, length, params);
        check();
    }

    public int glGetProgramResourceLocation(int program, int programInterface, String name) {
        calls++;
        incrementMethod("glGetProgramResourceLocation");
        int v = gl31.glGetProgramResourceLocation(program, programInterface, name);
        check();
        return v;
    }

    public void glUseProgramStages(int pipeline, int stages, int program) {
        calls++;
        incrementMethod("glUseProgramStages");
        gl31.glUseProgramStages(pipeline, stages, program);
        check();
    }

    public void glActiveShaderProgram(int pipeline, int program) {
        calls++;
        incrementMethod("glActiveShaderProgram");
        gl31.glActiveShaderProgram(pipeline, program);
        check();
    }

    public int glCreateShaderProgramv(int type, String[] strings) {
        calls++;
        incrementMethod("glCreateShaderProgramv");
        int v = gl31.glCreateShaderProgramv(type, strings);
        check();
        return v;
    }

    public void glBindProgramPipeline(int pipeline) {
        calls++;
        incrementMethod("glBindProgramPipeline");
        gl31.glBindProgramPipeline(pipeline);
        check();
    }

    public void glDeleteProgramPipelines(int count, IntBuffer pipelines) {
        calls++;
        incrementMethod("glDeleteProgramPipelines");
        gl31.glDeleteProgramPipelines(count, pipelines);
        check();
    }

    public void glGenProgramPipelines(int count, IntBuffer pipelines) {
        calls++;
        incrementMethod("glGenProgramPipelines");
        gl31.glGenProgramPipelines(count, pipelines);
        check();
    }

    public boolean glIsProgramPipeline(int pipeline) {
        calls++;
        incrementMethod("glIsProgramPipeline");
        boolean v = gl31.glIsProgramPipeline(pipeline);
        check();
        return v;
    }

    public void glGetProgramPipelineiv(int pipeline, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetProgramPipelineiv");
        gl31.glGetProgramPipelineiv(pipeline, pname, params);
        check();
    }

    public void glProgramUniform1i(int program, int location, int v0) {
        calls++;
        incrementMethod("glProgramUniform1i");
        gl31.glProgramUniform1i(program, location, v0);
        check();
    }

    public void glProgramUniform2i(int program, int location, int v0, int v1) {
        calls++;
        incrementMethod("glProgramUniform2i");
        gl31.glProgramUniform2i(program, location, v0, v1);
        check();
    }

    public void glProgramUniform3i(int program, int location, int v0, int v1, int v2) {
        calls++;
        incrementMethod("glProgramUniform3i");
        gl31.glProgramUniform3i(program, location, v0, v1, v2);
        check();
    }

    public void glProgramUniform4i(int program, int location, int v0, int v1, int v2, int v3) {
        calls++;
        incrementMethod("glProgramUniform4i");
        gl31.glProgramUniform4i(program, location, v0, v1, v2, v3);
        check();
    }

    public void glProgramUniform1ui(int program, int location, int v0) {
        calls++;
        incrementMethod("glProgramUniform1ui");
        gl31.glProgramUniform1ui(program, location, v0);
        check();
    }

    public void glProgramUniform2ui(int program, int location, int v0, int v1) {
        calls++;
        incrementMethod("glProgramUniform2ui");
        gl31.glProgramUniform2ui(program, location, v0, v1);
        check();
    }

    public void glProgramUniform3ui(int program, int location, int v0, int v1, int v2) {
        calls++;
        incrementMethod("glProgramUniform3ui");
        gl31.glProgramUniform3ui(program, location, v0, v1, v2);
        check();
    }

    public void glProgramUniform4ui(int program, int location, int v0, int v1, int v2, int v3) {
        calls++;
        incrementMethod("glProgramUniform4ui");
        gl31.glProgramUniform4ui(program, location, v0, v1, v2, v3);
        check();
    }

    public void glProgramUniform1f(int program, int location, float v0) {
        calls++;
        incrementMethod("glProgramUniform1f");
        gl31.glProgramUniform1f(program, location, v0);
        check();
    }

    public void glProgramUniform2f(int program, int location, float v0, float v1) {
        calls++;
        incrementMethod("glProgramUniform2f");
        gl31.glProgramUniform2f(program, location, v0, v1);
        check();
    }

    public void glProgramUniform3f(int program, int location, float v0, float v1, float v2) {
        calls++;
        incrementMethod("glProgramUniform3f");
        gl31.glProgramUniform3f(program, location, v0, v1, v2);
        check();
    }

    public void glProgramUniform4f(int program, int location, float v0, float v1, float v2, float v3) {
        calls++;
        incrementMethod("glProgramUniform4f");
        gl31.glProgramUniform4f(program, location, v0, v1, v2, v3);
        check();
    }

    public void glProgramUniform1iv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform1iv");
        gl31.glProgramUniform1iv(program, location, value);
        check();
    }

    public void glProgramUniform2iv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform2iv");
        gl31.glProgramUniform2iv(program, location, value);
        check();
    }

    public void glProgramUniform3iv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform3iv");
        gl31.glProgramUniform3iv(program, location, value);
        check();
    }

    public void glProgramUniform4iv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform4iv");
        gl31.glProgramUniform4iv(program, location, value);
        check();
    }

    public void glProgramUniform1uiv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform1uiv");
        gl31.glProgramUniform1uiv(program, location, value);
        check();
    }

    public void glProgramUniform2uiv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform2uiv");
        gl31.glProgramUniform2uiv(program, location, value);
        check();
    }

    public void glProgramUniform3uiv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform3uiv");
        gl31.glProgramUniform3uiv(program, location, value);
        check();
    }

    public void glProgramUniform4uiv(int program, int location, IntBuffer value) {
        calls++;
        incrementMethod("glProgramUniform4uiv");
        gl31.glProgramUniform4uiv(program, location, value);
        check();
    }

    public void glProgramUniform1fv(int program, int location, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniform1fv");
        gl31.glProgramUniform1fv(program, location, value);
        check();
    }

    public void glProgramUniform2fv(int program, int location, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniform2fv");
        gl31.glProgramUniform2fv(program, location, value);
        check();
    }

    public void glProgramUniform3fv(int program, int location, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniform3fv");
        gl31.glProgramUniform3fv(program, location, value);
        check();
    }

    public void glProgramUniform4fv(int program, int location, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniform4fv");
        gl31.glProgramUniform4fv(program, location, value);
        check();
    }

    public void glProgramUniformMatrix2fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix2fv");
        gl31.glProgramUniformMatrix2fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix3fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix3fv");
        gl31.glProgramUniformMatrix3fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix4fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix4fv");
        gl31.glProgramUniformMatrix4fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix2x3fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix2x3fv");
        gl31.glProgramUniformMatrix2x3fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix3x2fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix3x2fv");
        gl31.glProgramUniformMatrix3x2fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix2x4fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix2x4fv");
        gl31.glProgramUniformMatrix2x4fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix4x2fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix4x2fv");
        gl31.glProgramUniformMatrix4x2fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix3x4fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix3x4fv");
        gl31.glProgramUniformMatrix3x4fv(program, location, transpose, value);
        check();
    }

    public void glProgramUniformMatrix4x3fv(int program, int location, boolean transpose, FloatBuffer value) {
        calls++;
        incrementMethod("glProgramUniformMatrix4x3fv");
        gl31.glProgramUniformMatrix4x3fv(program, location, transpose, value);
        check();
    }

    public void glValidateProgramPipeline(int pipeline) {
        calls++;
        incrementMethod("glValidateProgramPipeline");
        gl31.glValidateProgramPipeline(pipeline);
        check();
    }

    public String glGetProgramPipelineInfoLog(int program) {
        calls++;
        incrementMethod("glGetProgramPipelineInfoLog");
        String s = gl31.glGetProgramPipelineInfoLog(program);
        check();
        return s;
    }

    public void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format) {
        calls++;
        incrementMethod("glBindImageTexture");
        gl31.glBindImageTexture(unit, texture, level, layered, layer, access, format);
        check();
    }

    public void glGetBooleani_v(int target, int index, IntBuffer data) {
        calls++;
        incrementMethod("glGetBooleani_v");
        gl31.glGetBooleani_v(target, index, data);
        check();
    }

    public void glMemoryBarrier(int barriers) {
        calls++;
        incrementMethod("glMemoryBarrier");
        gl31.glMemoryBarrier(barriers);
        check();
    }

    public void glMemoryBarrierByRegion(int barriers) {
        calls++;
        incrementMethod("glMemoryBarrierByRegion");
        gl31.glMemoryBarrierByRegion(barriers);
        check();
    }

    public void glTexStorage2DMultisample(int target, int samples, int internalformat, int width, int height,
                                          boolean fixedsamplelocations) {
        calls++;
        incrementMethod("glTexStorage2DMultisample");
        gl31.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
        check();
    }

    public void glGetMultisamplefv(int pname, int index, FloatBuffer val) {
        calls++;
        incrementMethod("glGetMultisamplefv");
        gl31.glGetMultisamplefv(pname, index, val);
        check();
    }

    public void glSampleMaski(int maskNumber, int mask) {
        calls++;
        incrementMethod("glSampleMaski");
        gl31.glSampleMaski(maskNumber, mask);
        check();
    }

    public void glGetTexLevelParameteriv(int target, int level, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetTexLevelParameteriv");
        gl31.glGetTexLevelParameteriv(target, level, pname, params);
        check();
    }

    public void glGetTexLevelParameterfv(int target, int level, int pname, FloatBuffer params) {
        calls++;
        incrementMethod("glGetTexLevelParameterfv");
        gl31.glGetTexLevelParameterfv(target, level, pname, params);
        check();
    }

    public void glBindVertexBuffer(int bindingindex, int buffer, long offset, int stride) {
        calls++;
        incrementMethod("glBindVertexBuffer");
        gl31.glBindVertexBuffer(bindingindex, buffer, offset, stride);
        check();
    }

    public void glVertexAttribFormat(int attribindex, int size, int type, boolean normalized, int relativeoffset) {
        calls++;
        incrementMethod("glVertexAttribFormat");
        gl31.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
        check();
    }

    public void glVertexAttribIFormat(int attribindex, int size, int type, int relativeoffset) {
        calls++;
        incrementMethod("glVertexAttribIFormat");
        gl31.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
        check();
    }

    public void glVertexAttribBinding(int attribindex, int bindingindex) {
        calls++;
        incrementMethod("glVertexAttribBinding");
        gl31.glVertexAttribBinding(attribindex, bindingindex);
        check();
    }

    public void glVertexBindingDivisor(int bindingindex, int divisor) {
        calls++;
        incrementMethod("glVertexBindingDivisor");
        gl31.glVertexBindingDivisor(bindingindex, divisor);
        check();
    }

}
