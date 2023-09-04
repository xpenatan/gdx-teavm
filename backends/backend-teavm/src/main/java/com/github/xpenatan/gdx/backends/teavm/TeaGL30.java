package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.graphics.GL30;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGL2RenderingContextWrapper;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class TeaGL30 extends TeaGL20 implements GL30  {

    protected WebGL2RenderingContextWrapper gl;

    public TeaGL30(WebGL2RenderingContextWrapper gl) {
        super(gl);
        this.gl = gl;
    }

    @Override
    public void glReadBuffer(int mode) {

    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {

    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {

    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, int offset) {

    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {

    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, int offset) {

    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, int offset) {

    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {

    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {

    }

    @Override
    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {

    }

    @Override
    public void glGenQueries(int n, int[] ids, int offset) {

    }

    @Override
    public void glGenQueries(int n, IntBuffer ids) {

    }

    @Override
    public void glDeleteQueries(int n, int[] ids, int offset) {

    }

    @Override
    public void glDeleteQueries(int n, IntBuffer ids) {

    }

    @Override
    public boolean glIsQuery(int id) {
        return false;
    }

    @Override
    public void glBeginQuery(int target, int id) {

    }

    @Override
    public void glEndQuery(int target) {

    }

    @Override
    public void glGetQueryiv(int target, int pname, IntBuffer params) {

    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {

    }

    @Override
    public boolean glUnmapBuffer(int target) {
        return false;
    }

    @Override
    public Buffer glGetBufferPointerv(int target, int pname) {
        return null;
    }

    @Override
    public void glDrawBuffers(int n, IntBuffer bufs) {

    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {

    }

    @Override
    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {

    }

    @Override
    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {

    }

    @Override
    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {

    }

    @Override
    public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        return null;
    }

    @Override
    public void glFlushMappedBufferRange(int target, int offset, int length) {

    }

    @Override
    public void glBindVertexArray(int array) {

    }

    @Override
    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {

    }

    @Override
    public void glDeleteVertexArrays(int n, IntBuffer arrays) {

    }

    @Override
    public void glGenVertexArrays(int n, int[] arrays, int offset) {

    }

    @Override
    public void glGenVertexArrays(int n, IntBuffer arrays) {

    }

    @Override
    public boolean glIsVertexArray(int array) {
        return false;
    }

    @Override
    public void glBeginTransformFeedback(int primitiveMode) {

    }

    @Override
    public void glEndTransformFeedback() {

    }

    @Override
    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {

    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {

    }

    @Override
    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {

    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {

    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {

    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {

    }

    @Override
    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {

    }

    @Override
    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {

    }

    @Override
    public void glGetUniformuiv(int program, int location, IntBuffer params) {

    }

    @Override
    public int glGetFragDataLocation(int program, String name) {
        return 0;
    }

    @Override
    public void glUniform1uiv(int location, int count, IntBuffer value) {

    }

    @Override
    public void glUniform3uiv(int location, int count, IntBuffer value) {

    }

    @Override
    public void glUniform4uiv(int location, int count, IntBuffer value) {

    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {

    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {

    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {

    }

    @Override
    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {

    }

    @Override
    public String glGetStringi(int name, int index) {
        return null;
    }

    @Override
    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {

    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {

    }

    @Override
    public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {

    }

    @Override
    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return 0;
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {

    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {

    }

    @Override
    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return null;
    }

    @Override
    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {

    }

    @Override
    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {

    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {

    }

    @Override
    public void glGetInteger64v(int pname, LongBuffer params) {

    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {

    }

    @Override
    public void glGenSamplers(int count, int[] samplers, int offset) {

    }

    @Override
    public void glGenSamplers(int count, IntBuffer samplers) {

    }

    @Override
    public void glDeleteSamplers(int count, int[] samplers, int offset) {

    }

    @Override
    public void glDeleteSamplers(int count, IntBuffer samplers) {

    }

    @Override
    public boolean glIsSampler(int sampler) {
        return false;
    }

    @Override
    public void glBindSampler(int unit, int sampler) {

    }

    @Override
    public void glSamplerParameteri(int sampler, int pname, int param) {

    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {

    }

    @Override
    public void glSamplerParameterf(int sampler, int pname, float param) {

    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {

    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {

    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {

    }

    @Override
    public void glVertexAttribDivisor(int index, int divisor) {

    }

    @Override
    public void glBindTransformFeedback(int target, int id) {

    }

    @Override
    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {

    }

    @Override
    public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {

    }

    @Override
    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {

    }

    @Override
    public void glGenTransformFeedbacks(int n, IntBuffer ids) {

    }

    @Override
    public boolean glIsTransformFeedback(int id) {
        return false;
    }

    @Override
    public void glPauseTransformFeedback() {

    }

    @Override
    public void glResumeTransformFeedback() {

    }

    @Override
    public void glProgramParameteri(int program, int pname, int value) {

    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {

    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width, int height) {

    }
}