package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGL2RenderingContextWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLQueryWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLSamplerWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLTransformFeedbackWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLVertexArrayObjectWrapper;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.webgl.WebGLFramebuffer;
import org.teavm.jso.webgl.WebGLRenderingContext;
import org.teavm.jso.webgl.WebGLTexture;
import org.teavm.jso.webgl.WebGLUniformLocation;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
@Emulate(TeaGL30.class)
public class TeaGL30 extends TeaGL20 implements GL30 {

    protected WebGL2RenderingContextWrapper gl;

    final CustomIntMap<WebGLQueryWrapper> queries = CustomIntMap.create();
    final CustomIntMap<WebGLSamplerWrapper> samplers = CustomIntMap.create();
    final CustomIntMap<WebGLTransformFeedbackWrapper> feedbacks = CustomIntMap.create();
    final CustomIntMap<WebGLVertexArrayObjectWrapper> vertexArrays = CustomIntMap.create();

    public TeaGL30(WebGL2RenderingContextWrapper gl) {
        super((WebGLRenderingContext)gl);
        this.gl = gl;
    }

    private void deallocateQueryId(int id) {
        queries.remove(id);
    }

    private void deallocateFeedbackId(int id) {
        feedbacks.remove(id);
    }

    private void deallocateSamplerId(int id) {
        samplers.remove(id);
    }

    private void deallocateVertexArrayId(int id) {
        vertexArrays.remove(id);
    }

    @Override
    public void glBeginQuery(int target, int id) {
        gl.beginQuery(target, queries.get(id));
    }

    @Override
    public void glBeginTransformFeedback(int primitiveMode) {
        gl.beginTransformFeedback(primitiveMode);
    }

    @Override
    public void glBindBufferBase(int target, int index, int buffer) {
        gl.bindBufferBase(target, index, buffers.get(buffer));
    }

    @Override
    public void glBindBufferRange(int target, int index, int buffer, int offset, int size) {
        gl.bindBufferRange(target, index, buffers.get(buffer), offset, size);
    }

    @Override
    public void glBindSampler(int unit, int sampler) {
        gl.bindSampler(unit, samplers.get(sampler));
    }

    @Override
    public void glBindTransformFeedback(int target, int id) {
        gl.bindTransformFeedback(target, feedbacks.get(id));
    }

    @Override
    public void glBindVertexArray(int array) {
        gl.bindVertexArray(vertexArrays.get(array));
    }

    @Override
    public void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1,
                                  int mask, int filter) {
        gl.blitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

    @Override
    public void glClearBufferfi(int buffer, int drawbuffer, float depth, int stencil) {
        gl.clearBufferfi(buffer, drawbuffer, depth, stencil);
    }

    @Override
    public void glClearBufferfv(int buffer, int drawbuffer, FloatBuffer value) {
        gl.clearBufferfv(buffer, drawbuffer, TypedArrays.getTypedArray(value));
    }

    @Override
    public void glClearBufferiv(int buffer, int drawbuffer, IntBuffer value) {
        gl.clearBufferiv(buffer, drawbuffer, TypedArrays.getTypedArray(value));
    }

    @Override
    public void glClearBufferuiv(int buffer, int drawbuffer, IntBuffer value) {
        gl.clearBufferuiv(buffer, drawbuffer, TypedArrays.getTypedArray(value));
    }

    @Override
    public void glCopyBufferSubData(int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
        gl.copyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
    }

    @Override
    public void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
        gl.copyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
    }

    @Override
    public void glDeleteQueries(int n, int[] ids, int offset) {
        for(int i = offset; i < offset + n; i++) {
            int id = ids[i];
            WebGLQueryWrapper query = queries.get(id);
            deallocateQueryId(id);
            gl.deleteQuery(query);
        }
    }

    @Override
    public void glDeleteQueries(int n, IntBuffer ids) {
        int startPosition = ids.position();
        for(int i = 0; i < n; i++) {
            int id = ids.get();
            WebGLQueryWrapper query = queries.get(id);
            deallocateQueryId(id);
            gl.deleteQuery(query);
        }
        ids.position(startPosition);
    }

    @Override
    public void glDeleteSamplers(int count, int[] samplers, int offset) {
        for(int i = offset; i < offset + count; i++) {
            int id = samplers[i];
            WebGLSamplerWrapper sampler = this.samplers.get(id);
            deallocateSamplerId(id);
            gl.deleteSampler(sampler);
        }
    }

    @Override
    public void glDeleteSamplers(int n, IntBuffer ids) {
        int startPosition = ids.position();
        for(int i = 0; i < n; i++) {
            int id = ids.get();
            WebGLSamplerWrapper sampler = samplers.get(id);
            deallocateSamplerId(id);
            gl.deleteSampler(sampler);
        }
        ids.position(startPosition);
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, int[] ids, int offset) {
        for(int i = offset; i < offset + n; i++) {
            int id = ids[i];
            WebGLTransformFeedbackWrapper feedback = feedbacks.get(id);
            deallocateFeedbackId(id);
            gl.deleteTransformFeedback(feedback);
        }
    }

    @Override
    public void glDeleteTransformFeedbacks(int n, IntBuffer ids) {
        int startPosition = ids.position();
        for(int i = 0; i < n; i++) {
            int id = ids.get();
            WebGLTransformFeedbackWrapper feedback = feedbacks.get(id);
            deallocateFeedbackId(id);
            gl.deleteTransformFeedback(feedback);
        }
        ids.position(startPosition);
    }

    @Override
    public void glDeleteVertexArrays(int n, int[] arrays, int offset) {
        for(int i = offset; i < offset + n; i++) {
            int id = arrays[i];
            WebGLVertexArrayObjectWrapper vArray = vertexArrays.get(id);
            deallocateVertexArrayId(id);
            gl.deleteVertexArray(vArray);
        }
    }

    @Override
    public void glDeleteVertexArrays(int n, IntBuffer ids) {
        int startPosition = ids.position();
        for(int i = 0; i < n; i++) {
            int id = ids.get();
            WebGLVertexArrayObjectWrapper vArray = vertexArrays.get(id);
            deallocateVertexArrayId(id);
            gl.deleteVertexArray(vArray);
        }
        ids.position(startPosition);
    }

    @Override
    public void glDrawArraysInstanced(int mode, int first, int count, int instanceCount) {
        gl.drawArraysInstanced(mode, first, count, instanceCount);
    }

    @Override
    public void glDrawBuffers(int n, IntBuffer bufs) {
        Int32Array arrayBuffer = TypedArrays.getTypedArray(bufs);
        gl.drawBuffers(arrayBuffer);
    }

    @Override
    public void glDrawElementsInstanced(int mode, int count, int type, int indicesOffset, int instanceCount) {
        gl.drawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, Buffer indices) {
        gl.drawRangeElements(mode, start, end, count, type, indices.position());
    }

    @Override
    public void glDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
        gl.drawRangeElements(mode, start, end, count, type, offset);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type,
                             int offset) {
        gl.texImage2D(target, level, internalformat, width, height, border, format, type, offset);
    }

    @Override
    public void glEndQuery(int target) {
        gl.endQuery(target);
    }

    @Override
    public void glEndTransformFeedback() {
        gl.endTransformFeedback();
    }

    @Override
    public void glFlushMappedBufferRange(int target, int offset, int length) {
        throw new UnsupportedOperationException("glFlushMappedBufferRange not supported on WebGL2");
    }

    @Override
    public void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer) {
        gl.framebufferTextureLayer(target, attachment, textures.get(texture), level, layer);
    }

    @Override
    public void glGenQueries(int n, int[] ids, int offset) {
        for(int i = offset; i < offset + n; i++) {
            WebGLQueryWrapper query = gl.createQuery();
            int id = queries.add(query);
            ids[i] = id;
        }
    }

    @Override
    public void glGenQueries(int n, IntBuffer ids) {
        for(int i = 0; i < n; i++) {
            WebGLQueryWrapper query = gl.createQuery();
            int id = queries.add(query);
            ids.put(i, id);
        }
    }

    @Override
    public void glGenSamplers(int count, int[] samplerr, int offset) {
        for(int i = offset; i < offset + count; i++) {
            WebGLSamplerWrapper sampler = gl.createSampler();
            int id = samplers.add(sampler);
            samplerr[i] = id;
        }
    }

    @Override
    public void glGenSamplers(int n, IntBuffer ids) {
        for(int i = 0; i < n; i++) {
            WebGLSamplerWrapper sampler = gl.createSampler();
            int id = samplers.add(sampler);
            ids.put(i, id);
        }
    }

    @Override
    public void glGenTransformFeedbacks(int n, int[] ids, int offset) {
        for(int i = offset; i < offset + n; i++) {
            WebGLTransformFeedbackWrapper feedback = gl.createTransformFeedback();
            int id = feedbacks.add(feedback);
            ids[i] = id;
        }
    }

    @Override
    public void glGenTransformFeedbacks(int n, IntBuffer ids) {
        for(int i = 0; i < n; i++) {
            WebGLTransformFeedbackWrapper feedback = gl.createTransformFeedback();
            int id = feedbacks.add(feedback);
            ids.put(i, id);
        }
    }

    @Override
    public void glGenVertexArrays(int n, int[] arrays, int offset) {
        for(int i = offset; i < offset + n; i++) {
            WebGLVertexArrayObjectWrapper vArray = gl.createVertexArray();
            int id = vertexArrays.add(vArray);
            arrays[i] = id;
        }
    }

    @Override
    public void glGenVertexArrays(int n, IntBuffer ids) {
        for(int i = 0; i < n; i++) {
            WebGLVertexArrayObjectWrapper vArray = gl.createVertexArray();
            int id = vertexArrays.add(vArray);
            ids.put(i, id);
        }
    }

    @Override
    public void glGetActiveUniformBlockiv(int program, int uniformBlockIndex, int pname, IntBuffer params) {
        if(pname == GL30.GL_UNIFORM_BLOCK_BINDING || pname == GL30.GL_UNIFORM_BLOCK_DATA_SIZE
                || pname == GL30.GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS) {
            params.put(0, gl.getActiveUniformBlockParameteri(programs.get(program), uniformBlockIndex, pname));
        }
        else if(pname == GL30.GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES) {
            Int32Array array = gl.getActiveUniformBlockParameterv(programs.get(program), uniformBlockIndex, pname);
            for(int i = 0; i < array.getLength(); i++) {
                params.put(i, array.get(i));
            }
        }
        else if(pname == GL30.GL_UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER
                || pname == GL30.GL_UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER) {
            boolean result = gl.getActiveUniformBlockParameterb(programs.get(program), uniformBlockIndex, pname);
            params.put(0, result ? GL20.GL_TRUE : GL20.GL_FALSE);
        }
        else {
            throw new GdxRuntimeException("Unsupported pname passed to glGetActiveUniformBlockiv");
        }
    }

    @Override
    public String glGetActiveUniformBlockName(int program, int uniformBlockIndex) {
        return gl.getActiveUniformBlockName(programs.get(program), uniformBlockIndex);
    }

    @Override
    public void glGetActiveUniformBlockName(int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
        throw new UnsupportedOperationException("glGetActiveUniformBlockName with Buffer parameters not supported on WebGL2");
    }

    @Override
        public void glGetActiveUniformsiv(int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
        if(pname == GL30.GL_UNIFORM_IS_ROW_MAJOR) {
//            JSArray<Boolean> arr = gl.getActiveUniformsb(programs.get(program), TypedArrays.getTypedArray(uniformIndices).subarray(0, uniformCount), pname);
            JSArray<Boolean> arr = gl.getActiveUniformsb(programs.get(program), TypedArrays.getTypedArray(uniformIndices), pname);
            for(int i = 0; i < uniformCount; i++) {
                params.put(i, arr.get(i) ? GL20.GL_TRUE : GL20.GL_FALSE);
            }
        }
        else {
//            JSArray<Integer> arr = gl.getActiveUniformsi(programs.get(program), TypedArrays.getTypedArray(uniformIndices).subarray(0, uniformCount), pname);
            JSArray<Integer> arr = gl.getActiveUniformsi(programs.get(program), TypedArrays.getTypedArray(uniformIndices), pname);
            for(int i = 0; i < uniformCount; i++) {
                params.put(i, arr.get(i));
            }
        }
    }

    @Override
    public void glGetBufferParameteri64v(int target, int pname, LongBuffer params) {
        throw new UnsupportedOperationException("glGetBufferParameteri64v not supported on WebGL2");
    }

    @Override
    public Buffer glGetBufferPointerv(int target, int pname) {
        throw new UnsupportedOperationException("glGetBufferPointerv not supported on WebGL2");
    }

    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        // Override GwtGL20 method to check if it's a pname introduced with GL30.
        if(pname == GL30.GL_MAX_TEXTURE_LOD_BIAS) {
            params.put(0, gl.getParameterf(pname));
        }
        else {
            super.glGetFloatv(pname, params);
        }
    }

    @Override
    public int glGetFragDataLocation(int program, String name) {
        return gl.getFragDataLocation(programs.get(program), name);
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        // Override GwtGL20 method to check if it's a pname introduced with GL30.
        switch(pname) {
            case GL30.GL_DRAW_BUFFER0:
            case GL30.GL_DRAW_BUFFER1:
            case GL30.GL_DRAW_BUFFER2:
            case GL30.GL_DRAW_BUFFER3:
            case GL30.GL_DRAW_BUFFER4:
            case GL30.GL_DRAW_BUFFER5:
            case GL30.GL_DRAW_BUFFER6:
            case GL30.GL_DRAW_BUFFER7:
            case GL30.GL_DRAW_BUFFER8:
            case GL30.GL_DRAW_BUFFER9:
            case GL30.GL_DRAW_BUFFER10:
            case GL30.GL_FRAGMENT_SHADER_DERIVATIVE_HINT:
            case GL30.GL_MAX_3D_TEXTURE_SIZE:
            case GL30.GL_MAX_ARRAY_TEXTURE_LAYERS:
            case GL30.GL_MAX_COLOR_ATTACHMENTS:
            case GL30.GL_MAX_DRAW_BUFFERS:
            case GL30.GL_MAX_ELEMENTS_INDICES:
            case GL30.GL_MAX_ELEMENTS_VERTICES:
            case GL30.GL_MAX_FRAGMENT_INPUT_COMPONENTS:
            case GL30.GL_MAX_FRAGMENT_UNIFORM_BLOCKS:
            case GL30.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS:
            case GL30.GL_MAX_PROGRAM_TEXEL_OFFSET:
            case GL30.GL_MAX_SAMPLES:
            case GL30.GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS:
            case GL30.GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS:
            case GL30.GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS:
            case GL30.GL_MAX_UNIFORM_BUFFER_BINDINGS:
            case GL30.GL_MAX_VARYING_COMPONENTS:
            case GL30.GL_MAX_VERTEX_OUTPUT_COMPONENTS:
            case GL30.GL_MAX_VERTEX_UNIFORM_BLOCKS:
            case GL30.GL_MAX_VERTEX_UNIFORM_COMPONENTS:
            case GL30.GL_MIN_PROGRAM_TEXEL_OFFSET:
            case GL30.GL_PACK_ROW_LENGTH:
            case GL30.GL_PACK_SKIP_PIXELS:
            case GL30.GL_PACK_SKIP_ROWS:
            case GL30.GL_READ_BUFFER:
            case GL30.GL_UNPACK_IMAGE_HEIGHT:
            case GL30.GL_UNPACK_ROW_LENGTH:
            case GL30.GL_UNPACK_SKIP_IMAGES:
            case GL30.GL_UNPACK_SKIP_PIXELS:
            case GL30.GL_UNPACK_SKIP_ROWS:
                params.put(0, gl.getParameteri(pname));
                return;
            case GL30.GL_DRAW_FRAMEBUFFER_BINDING:
            case GL30.GL_READ_FRAMEBUFFER_BINDING:
                WebGLFramebuffer fbo = (WebGLFramebuffer)gl.getParameter(pname);
                if(fbo == null) {
                    params.put(0, 0);
                }
                else {
                    params.put(0, frameBuffers.getKey(fbo));
                }
                return;
            case GL30.GL_TEXTURE_BINDING_2D_ARRAY:
            case GL30.GL_TEXTURE_BINDING_3D:
                WebGLTexture tex = (WebGLTexture)gl.getParameter(pname);
                if(tex == null) {
                    params.put(0, 0);
                }
                else {
                    params.put(0, textures.getKey(tex));
                }
                return;
            case GL30.GL_VERTEX_ARRAY_BINDING:
                WebGLVertexArrayObjectWrapper obj = (WebGLVertexArrayObjectWrapper)gl.getParameter(pname);
                if(obj == null) {
                    params.put(0, 0);
                }
                else {
                    params.put(0, vertexArrays.getKey(obj));
                }
                return;
            default:
                // Assume it is a GL20 pname
                super.glGetIntegerv(pname, params);
        }
    }

    @Override
    public void glGetInteger64v(int pname, LongBuffer params) {
        switch(pname) {
            case GL30.GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS:
            case GL30.GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS:
            case GL30.GL_MAX_ELEMENT_INDEX:
            case GL30.GL_MAX_SERVER_WAIT_TIMEOUT:
            case GL30.GL_MAX_UNIFORM_BLOCK_SIZE:
                params.put(0, gl.getParameteri64(pname));
                return;
            default:
                throw new UnsupportedOperationException("Given glGetInteger64v enum not supported on WebGL2");
        }
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv (int target, int attachment, int pname, IntBuffer params) {
        switch (pname) {
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_BLUE_SIZE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_COLOR_ENCODING:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_COMPONENT_TYPE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_DEPTH_SIZE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_GREEN_SIZE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_RED_SIZE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_STENCIL_SIZE:
            case GL30.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LAYER:
                params.put(0, (int)(Object)gl.getFramebufferAttachmentParameter(target, attachment, pname));
                break;
            default:
                // Assume it is a GL20 pname
                super.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
        }
    }

    @Override
    public void glGetQueryiv(int target, int pname, IntBuffer params) {
        // Not 100% clear on this one. Returning the integer key for the query.
        // Similar to how GwtGL20 handles FBO in glGetIntegerv
        WebGLQueryWrapper query = gl.getQuery(target, pname);
        if(query == null) {
            params.put(0, 0);
        }
        else {
            params.put(0, queries.getKey(query));
        }
    }

    @Override
    public void glGetQueryObjectuiv(int id, int pname, IntBuffer params) {
        // In WebGL2 getQueryObject was renamed to getQueryParameter
        if(pname == GL30.GL_QUERY_RESULT) {
            params.put(0, gl.getQueryParameteri(queries.get(id), pname));
        }
        else if(pname == GL30.GL_QUERY_RESULT_AVAILABLE) {
            boolean result = gl.getQueryParameterb(queries.get(id), pname);
            params.put(0, result ? GL20.GL_TRUE : GL20.GL_FALSE);
        }
        else {
            throw new GdxRuntimeException("Unsupported pname passed to glGetQueryObjectuiv");
        }
    }

    @Override
    public void glGetSamplerParameterfv(int sampler, int pname, FloatBuffer params) {
        params.put(0, gl.getSamplerParameterf(samplers.get(sampler), pname));
    }

    @Override
    public void glGetSamplerParameteriv(int sampler, int pname, IntBuffer params) {
        params.put(0, gl.getSamplerParameteri(samplers.get(sampler), pname));
    }

    @Override
    public String glGetStringi(int name, int index) {
        throw new UnsupportedOperationException("glGetStringi not supported on WebGL2");
    }

    @Override
    public int glGetUniformBlockIndex(int program, String uniformBlockName) {
        return gl.getUniformBlockIndex(programs.get(program), uniformBlockName);
    }

    @Override
    public void glGetUniformIndices(int program, String[] uniformNames, IntBuffer uniformIndices) {
        JSArray<Integer> array = gl.getUniformIndices(programs.get(program), uniformNames);
        int length = array.getLength();
        for(int i = 0; i < length; i++) {
            uniformIndices.put(i, array.get(i));
        }
    }

    @Override
    public void glGetUniformuiv(int program, int location, IntBuffer params) {
        // fv and iv also not implemented in GwtGL20
        throw new UnsupportedOperationException("glGetUniformuiv not implemented on WebGL2");
    }

    @Override
    public void glGetVertexAttribIiv(int index, int pname, IntBuffer params) {
        // fv and iv also not implemented in GwtGL20
        throw new UnsupportedOperationException("glGetVertexAttribIiv not implemented on WebGL2");
    }

    @Override
    public void glGetVertexAttribIuiv(int index, int pname, IntBuffer params) {
        // fv and iv also not implemented in GwtGL20
        throw new UnsupportedOperationException("glGetVertexAttribIuiv not implemented on WebGL2");
    }

    @Override
    public void glInvalidateFramebuffer(int target, int numAttachments, IntBuffer attachments) {
        int startPosition = attachments.position();
//        gl.invalidateFramebuffer(target, TypedArrays.getTypedArray(attachments).subarray(0, numAttachments));
        gl.invalidateFramebuffer(target, TypedArrays.getTypedArray(attachments));
        attachments.position(startPosition);
    }

    @Override
    public void glInvalidateSubFramebuffer(int target, int numAttachments, IntBuffer attachments, int x, int y, int width,
                                           int height) {
        int startPosition = attachments.position();
//        gl.invalidateSubFramebuffer(target, TypedArrays.getTypedArray(attachments).subarray(0, numAttachments), x, y, width, height);
        gl.invalidateSubFramebuffer(target, TypedArrays.getTypedArray(attachments), x, y, width, height);
        attachments.position(startPosition);
    }

    @Override
    public boolean glIsQuery(int id) {
        return gl.isQuery(queries.get(id));
    }

    @Override
    public boolean glIsSampler(int id) {
        return gl.isSampler(samplers.get(id));
    }

    @Override
    public boolean glIsTransformFeedback(int id) {
        return gl.isTransformFeedback(feedbacks.get(id));
    }

    @Override
    public boolean glIsVertexArray(int id) {
        return gl.isVertexArray(vertexArrays.get(id));
    }

    @Override
    public Buffer glMapBufferRange(int target, int offset, int length, int access) {
        throw new UnsupportedOperationException("glMapBufferRange not supported on WebGL2");
    }

    @Override
    public void glPauseTransformFeedback() {
        gl.pauseTransformFeedback();
    }

    @Override
    public void glProgramParameteri(int program, int pname, int value) {
        // Per WebGL2 spec: Accessing binary representations of compiled shader programs is not supported in the WebGL 2.0 API.
        // This includes OpenGL ES 3.0 GetProgramBinary, ProgramBinary, and ProgramParameteri entry points
        throw new UnsupportedOperationException("glProgramParameteri not supported on WebGL2");
    }

    @Override
    public void glReadBuffer(int mode) {
        gl.readBuffer(mode);
    }

    @Override
    public void glRenderbufferStorageMultisample(int target, int samples, int internalformat, int width, int height) {
        gl.renderbufferStorageMultisample(target, samples, internalformat, width, height);
    }

    @Override
    public void glResumeTransformFeedback() {
        gl.resumeTransformFeedback();
    }

    @Override
    public void glSamplerParameterf(int sampler, int pname, float param) {
        gl.samplerParameterf(samplers.get(sampler), pname, param);
    }

    @Override
    public void glSamplerParameterfv(int sampler, int pname, FloatBuffer param) {
        gl.samplerParameterf(samplers.get(sampler), pname, param.get());
    }

    @Override
    public void glSamplerParameteri(int sampler, int pname, int param) {
        gl.samplerParameteri(samplers.get(sampler), pname, param);
    }

    @Override
    public void glSamplerParameteriv(int sampler, int pname, IntBuffer param) {
        gl.samplerParameterf(samplers.get(sampler), pname, param.get());
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, Buffer pixels) {
        if(pixels == null) {
            gl.texImage3D(target, level, internalformat, width, height, depth, border, format, type, (ArrayBufferView)null);
            return;
        }
        boolean isUnsigned = type == WebGLRenderingContext.UNSIGNED_BYTE || type == WebGLRenderingContext.UNSIGNED_SHORT || type == GL_UNSIGNED_SHORT_5_6_5 || type == GL_UNSIGNED_SHORT_4_4_4_4;
        ArrayBufferView arrayBuffer = TypedArrays.getTypedArray(isUnsigned, pixels);
        gl.texImage3D(target, level, internalformat, width, height, depth, border, format, type, arrayBuffer);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, Buffer pixels) {
        boolean isUnsigned = type == WebGLRenderingContext.UNSIGNED_BYTE || type == WebGLRenderingContext.UNSIGNED_SHORT || type == GL_UNSIGNED_SHORT_5_6_5 || type == GL_UNSIGNED_SHORT_4_4_4_4;
        ArrayBufferView arrayBuffer = TypedArrays.getTypedArray(isUnsigned, pixels);
        gl.texSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, arrayBuffer);
    }

    @Override
    public void glTexImage3D(int target, int level, int internalformat, int width, int height, int depth, int border, int format,
                             int type, int offset) {
        gl.texImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type,
                                int offset) {
        gl.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, offset);
    }

    @Override
    public void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, int offset) {
        gl.texSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
    }

    @Override
    public void glTransformFeedbackVaryings(int program, String[] varyings, int bufferMode) {
        gl.transformFeedbackVaryings(programs.get(program), varyings, bufferMode);
    }

    @Override
    public void glUniform1uiv(int location, int count, IntBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniform1uiv(loc, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniform3uiv(int location, int count, IntBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniform3uiv(loc, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniform4uiv(int location, int count, IntBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniform4uiv(loc, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
        gl.uniformBlockBinding(programs.get(program), uniformBlockIndex, uniformBlockBinding);
    }

    @Override
    public void glUniformMatrix2x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniformMatrix2x3fv(loc, transpose, TypedArrays.getTypedArray(value));
    }

    @Override
    public void glUniformMatrix2x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniformMatrix2x4fv(loc, transpose, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniformMatrix3x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniformMatrix3x2fv(loc, transpose, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniformMatrix3x4fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniformMatrix3x4fv(loc, transpose, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniformMatrix4x2fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniformMatrix4x2fv(loc, transpose, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public void glUniformMatrix4x3fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocation loc = getUniformLocation(location);
        gl.uniformMatrix4x3fv(loc, transpose, TypedArrays.getTypedArray(value), 0, count);
    }

    @Override
    public boolean glUnmapBuffer(int target) {
        throw new UnsupportedOperationException("glUnmapBuffer not supported on WebGL2");
    }

    @Override
    public void glVertexAttribDivisor(int index, int divisor) {
        gl.vertexAttribDivisor(index, divisor);
    }

    @Override
    public void glVertexAttribI4i(int index, int x, int y, int z, int w) {
        gl.vertexAttribI4i(index, x, y, z, w);
    }

    @Override
    public void glVertexAttribI4ui(int index, int x, int y, int z, int w) {
        gl.vertexAttribI4ui(index, x, y, z, w);
    }

    @Override
    public void glVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
        gl.vertexAttribIPointer(index, size, type, stride, offset);
    }
}