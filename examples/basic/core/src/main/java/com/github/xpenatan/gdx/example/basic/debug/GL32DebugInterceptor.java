package com.github.xpenatan.gdx.example.basic.debug;

import com.badlogic.gdx.graphics.GL32;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL32DebugInterceptor extends GL31DebugInterceptor implements GL32 {

    final GL32 gl32;

    public GL32DebugInterceptor(GLDebugProfiler glProfiler, GL32 gl32) {
        super(glProfiler, gl32);
        this.gl32 = gl32;
    }

    public void glBlendBarrier() {
        calls++;
        incrementMethod("glBlendBarrier");
        gl32.glBlendBarrier();
        check();
    }

    public void glCopyImageSubData(int srcName, int srcTarget, int srcLevel, int srcX, int srcY, int srcZ, int dstName,
                                   int dstTarget, int dstLevel, int dstX, int dstY, int dstZ, int srcWidth, int srcHeight, int srcDepth) {
        calls++;
        incrementMethod("glCopyImageSubData");
        gl32.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX, dstY, dstZ,
                srcWidth, srcHeight, srcDepth);
        check();
    }

    public void glDebugMessageControl(int source, int type, int severity, IntBuffer ids, boolean enabled) {
        calls++;
        incrementMethod("glDebugMessageControl");
        gl32.glDebugMessageControl(source, type, severity, ids, enabled);
        check();
    }

    public void glDebugMessageInsert(int source, int type, int id, int severity, String buf) {
        calls++;
        incrementMethod("glDebugMessageInsert");
        gl32.glDebugMessageInsert(source, type, id, severity, buf);
        check();
    }

    public void glDebugMessageCallback(DebugProc callsback) {
        calls++;
        incrementMethod("glDebugMessageCallback");
        gl32.glDebugMessageCallback(callsback);
        check();
        check();
    }

    public int glGetDebugMessageLog(int count, IntBuffer sources, IntBuffer types, IntBuffer ids, IntBuffer severities,
                                    IntBuffer lengths, ByteBuffer messageLog) {
        calls++;
        incrementMethod("glGetDebugMessageLog");
        int v = gl32.glGetDebugMessageLog(count, sources, types, ids, severities, lengths, messageLog);
        check();
        return v;
    }

    public void glPushDebugGroup(int source, int id, String message) {
        calls++;
        incrementMethod("glPushDebugGroup");
        gl32.glPushDebugGroup(source, id, message);
        check();
    }

    public void glPopDebugGroup() {
        calls++;
        incrementMethod("glPopDebugGroup");
        gl32.glPopDebugGroup();
        check();
    }

    public void glObjectLabel(int identifier, int name, String label) {
        calls++;
        incrementMethod("glObjectLabel");
        gl32.glObjectLabel(identifier, name, label);
        check();
    }

    public String glGetObjectLabel(int identifier, int name) {
        calls++;
        incrementMethod("glGetObjectLabel");
        String v = gl32.glGetObjectLabel(identifier, name);
        check();
        return v;
    }

    public long glGetPointerv(int pname) {
        calls++;
        incrementMethod("glGetPointerv");
        long v = gl32.glGetPointerv(pname);
        check();
        return v;
    }

    public void glEnablei(int target, int index) {
        calls++;
        incrementMethod("glEnablei");
        gl32.glEnablei(target, index);
        check();
    }

    public void glDisablei(int target, int index) {
        calls++;
        incrementMethod("glDisablei");
        gl32.glDisablei(target, index);
        check();
    }

    public void glBlendEquationi(int buf, int mode) {
        calls++;
        incrementMethod("glBlendEquationi");
        gl32.glBlendEquationi(buf, mode);
        check();
    }

    public void glBlendEquationSeparatei(int buf, int modeRGB, int modeAlpha) {
        calls++;
        incrementMethod("glBlendEquationSeparatei");
        gl32.glBlendEquationSeparatei(buf, modeRGB, modeAlpha);
        check();
    }

    public void glBlendFunci(int buf, int src, int dst) {
        calls++;
        incrementMethod("glBlendFunci");
        gl32.glBlendFunci(buf, src, dst);
        check();
    }

    public void glBlendFuncSeparatei(int buf, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        calls++;
        incrementMethod("glBlendFuncSeparatei");
        gl32.glBlendFuncSeparatei(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
        check();
    }

    public void glColorMaski(int index, boolean r, boolean g, boolean b, boolean a) {
        calls++;
        incrementMethod("glColorMaski");
        gl32.glColorMaski(index, r, g, b, a);
        check();
    }

    public boolean glIsEnabledi(int target, int index) {
        calls++;
        incrementMethod("glIsEnabledi");
        boolean v = gl32.glIsEnabledi(target, index);
        check();
        return v;
    }

    public void glDrawElementsBaseVertex(int mode, int count, int type, Buffer indices, int basevertex) {
        vertexCount.put(count);
        drawCalls++;
        calls++;
        incrementMethod("glDrawElementsBaseVertex");
        gl32.glDrawElementsBaseVertex(mode, count, type, indices, basevertex);
        check();
    }

    public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int count, int type, Buffer indices, int basevertex) {
        vertexCount.put(count);
        drawCalls++;
        calls++;
        incrementMethod("glDrawRangeElementsBaseVertex");
        gl32.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices, basevertex);
        check();
    }

    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, Buffer indices, int instanceCount,
                                                  int basevertex) {
        vertexCount.put(count);
        drawCalls++;
        calls++;
        incrementMethod("glDrawElementsInstancedBaseVertex");
        gl32.glDrawElementsInstancedBaseVertex(mode, count, type, indices, instanceCount, basevertex);
        check();
    }

    public void glDrawElementsInstancedBaseVertex(int mode, int count, int type, int indicesOffset, int instanceCount,
                                                  int basevertex) {
        vertexCount.put(count);
        drawCalls++;
        calls++;
        incrementMethod("glDrawElementsInstancedBaseVertex");
        gl32.glDrawElementsInstancedBaseVertex(mode, count, type, indicesOffset, instanceCount, basevertex);
        check();
    }

    public void glFramebufferTexture(int target, int attachment, int texture, int level) {
        calls++;
        incrementMethod("glFramebufferTexture");
        gl32.glFramebufferTexture(target, attachment, texture, level);
        check();
    }

    public int glGetGraphicsResetStatus() {
        calls++;
        incrementMethod("glGetGraphicsResetStatus");
        int v = gl32.glGetGraphicsResetStatus();
        check();
        return v;
    }

    public void glReadnPixels(int x, int y, int width, int height, int format, int type, int bufSize, Buffer data) {
        calls++;
        incrementMethod("glReadnPixels");
        gl32.glReadnPixels(x, y, width, height, format, type, bufSize, data);
        check();
    }

    public void glGetnUniformfv(int program, int location, FloatBuffer params) {
        calls++;
        incrementMethod("glGetnUniformfv");
        gl32.glGetnUniformfv(program, location, params);
        check();
    }

    public void glGetnUniformiv(int program, int location, IntBuffer params) {
        calls++;
        incrementMethod("glGetnUniformiv");
        gl32.glGetnUniformiv(program, location, params);
        check();
    }

    public void glGetnUniformuiv(int program, int location, IntBuffer params) {
        calls++;
        incrementMethod("glGetnUniformuiv");
        gl32.glGetnUniformuiv(program, location, params);
        check();
    }

    public void glMinSampleShading(float value) {
        calls++;
        incrementMethod("glMinSampleShading");
        gl32.glMinSampleShading(value);
        check();
    }

    public void glPatchParameteri(int pname, int value) {
        calls++;
        incrementMethod("glPatchParameteri");
        gl32.glPatchParameteri(pname, value);
        check();
    }

    public void glTexParameterIiv(int target, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glTexParameterIiv");
        gl32.glTexParameterIiv(target, pname, params);
        check();
    }

    public void glTexParameterIuiv(int target, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glTexParameterIuiv");
        gl32.glTexParameterIuiv(target, pname, params);
        check();
    }

    public void glGetTexParameterIiv(int target, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetTexParameterIiv");
        gl32.glGetTexParameterIiv(target, pname, params);
        check();
    }

    public void glGetTexParameterIuiv(int target, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetTexParameterIuiv");
        gl32.glGetTexParameterIuiv(target, pname, params);
        check();
    }

    public void glSamplerParameterIiv(int sampler, int pname, IntBuffer param) {
        calls++;
        incrementMethod("glSamplerParameterIiv");
        gl32.glSamplerParameterIiv(sampler, pname, param);
        check();
    }

    public void glSamplerParameterIuiv(int sampler, int pname, IntBuffer param) {
        calls++;
        incrementMethod("glSamplerParameterIuiv");
        gl32.glSamplerParameterIuiv(sampler, pname, param);
        check();
    }

    public void glGetSamplerParameterIiv(int sampler, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetSamplerParameterIiv");
        gl32.glGetSamplerParameterIiv(sampler, pname, params);
        check();
    }

    public void glGetSamplerParameterIuiv(int sampler, int pname, IntBuffer params) {
        calls++;
        incrementMethod("glGetSamplerParameterIuiv");
        gl32.glGetSamplerParameterIuiv(sampler, pname, params);
        check();
    }

    public void glTexBuffer(int target, int internalformat, int buffer) {
        calls++;
        incrementMethod("glTexBuffer");
        gl32.glTexBuffer(target, internalformat, buffer);
        check();
    }

    public void glTexBufferRange(int target, int internalformat, int buffer, int offset, int size) {
        calls++;
        incrementMethod("glTexBufferRange");
        gl32.glTexBufferRange(target, internalformat, buffer, offset, size);
        check();
    }

    public void glTexStorage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth,
                                          boolean fixedsamplelocations) {
        calls++;
        incrementMethod("glTexStorage3DMultisample");
        gl32.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
        check();
    }

}
