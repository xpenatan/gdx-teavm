package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Uint8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLActiveInfoWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLBufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLFramebufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLProgramWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLRenderbufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLShaderWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLTextureWrapper;
import com.github.xpenatan.gdx.backends.teavm.gl.WebGLUniformLocationWrapper;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Uint8Array;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
@Emulate(TeaGL20.class)
public class TeaGL20 implements GL20 {

    @JSClass
    static class CustomIntMap<T extends JSObject> implements JSObject {
        @JSBody(script = "return [undefined];")
        public static native <T extends JSObject> CustomIntMap<T> create();

        @JSBody(params = { "key" }, script = "if(this[key] === undefined) return null; return this[key];")
        public native T get (int key);

        @JSBody(params = { "key", "value" }, script = "this[key] = value;")
        public native void put (int key, T value);

        @JSBody(params = { "value" }, script = "this.push(value); return this.length - 1")
        public native int add (T value);

        @JSBody(params = { "key" }, script = "var value = this[key]; delete this[key]; return value;")
        public native T remove (int key);

        @JSBody(params = { "value" }, script = "for (i = 0; i < this.length; i++) { if (value === this[i]) { return i; } }")
        public native int getKey (T value);
    }

    protected WebGLRenderingContextWrapper gl;

    final CustomIntMap<WebGLProgramWrapper> programs = CustomIntMap.create();
    final CustomIntMap<WebGLShaderWrapper> shaders = CustomIntMap.create();
    final CustomIntMap<WebGLBufferWrapper> buffers = CustomIntMap.create();
    final CustomIntMap<WebGLFramebufferWrapper> frameBuffers = CustomIntMap.create();
    final CustomIntMap<WebGLRenderbufferWrapper> renderBuffers = CustomIntMap.create();
    final CustomIntMap<WebGLTextureWrapper> textures = CustomIntMap.create();
    final CustomIntMap<CustomIntMap<WebGLUniformLocationWrapper>> uniforms = CustomIntMap.create();
    private int currProgram = 0;

    public TeaGL20(WebGLRenderingContextWrapper gl) {
        this.gl = gl;
        this.gl.pixelStorei(WebGLRenderingContextWrapper.UNPACK_PREMULTIPLY_ALPHA_WEBGL, 0);
    }

    public Float32ArrayWrapper copy(FloatBuffer buffer) {
        ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray(buffer);
        return TypedArrays.createFloat32Array(typedArray.getBuffer(), buffer.position(), buffer.remaining());
    }

    public Int16ArrayWrapper copy(ShortBuffer buffer) {
        ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray(buffer);
        return TypedArrays.createInt16Array(typedArray.getBuffer(), buffer.position(), buffer.remaining());
    }

    public Int32ArrayWrapper copy(IntBuffer buffer) {
        ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray(buffer);
        return TypedArrays.createInt32Array(typedArray.getBuffer(), buffer.position(), buffer.remaining());
    }

    public Int8ArrayWrapper copy(ByteBuffer buffer) {
        return (Int8ArrayWrapper)TypedArrays.getTypedArray(buffer);
    }

    protected WebGLUniformLocationWrapper getUniformLocation(int location) {
        return uniforms.get(currProgram).get(location);
    }

    @Override
    public void glActiveTexture(int texture) {
        gl.activeTexture(texture);
    }

    @Override
    public void glAttachShader(int program, int shader) {
        WebGLProgramWrapper glProgram = programs.get(program);
        WebGLShaderWrapper glShader = shaders.get(shader);
        gl.attachShader(glProgram, glShader);
    }

    @Override
    public void glBindAttribLocation(int program, int index, String name) {
        WebGLProgramWrapper glProgram = programs.get(program);
        gl.bindAttribLocation(glProgram, index, name);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        gl.bindBuffer(target, buffers.get(buffer));
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        gl.bindFramebuffer(target, frameBuffers.get(framebuffer));
    }

    @Override
    public void glBindRenderbuffer(int target, int renderbuffer) {
        gl.bindRenderbuffer(target, renderBuffers.get(renderbuffer));
    }

    @Override
    public void glBindTexture(int target, int texture) {
        gl.bindTexture(target, textures.get(texture));
    }

    @Override
    public void glBlendColor(float red, float green, float blue, float alpha) {
        gl.blendColor(red, green, blue, alpha);
    }

    @Override
    public void glBlendEquation(int mode) {
        gl.blendEquation(mode);
    }

    @Override
    public void glBlendEquationSeparate(int modeRGB, int modeAlpha) {
        gl.blendEquationSeparate(modeRGB, modeAlpha);
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        gl.blendFunc(sfactor, dfactor);
    }

    @Override
    public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        gl.blendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        if(data instanceof FloatBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((FloatBuffer)data);
            gl.bufferData(target, typedArray, usage);
        }
        else if(data instanceof IntBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((IntBuffer)data);
            gl.bufferData(target, typedArray, usage);
        }
        else if(data instanceof ShortBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((ShortBuffer)data);
            gl.bufferData(target, typedArray, usage);
        }
        else if(data instanceof ByteBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((ByteBuffer)data);
            gl.bufferData(target, typedArray, usage);
        }
        else if(data == null) {
            gl.bufferData(target, size, usage);
        }
        else {
            throw new GdxRuntimeException("Can only cope with FloatBuffer and ShortBuffer at the moment");
        }
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        if(data instanceof FloatBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((FloatBuffer)data);
            gl.bufferSubData(target, offset, typedArray);
        }
        else if(data instanceof IntBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((IntBuffer)data);
            gl.bufferSubData(target, offset, typedArray);
        }
        else if(data instanceof ShortBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((ShortBuffer)data);
            gl.bufferSubData(target, offset, typedArray);
        }
        else if(data instanceof ByteBuffer) {
            ArrayBufferViewWrapper typedArray = TypedArrays.getTypedArray((ByteBuffer)data);
            gl.bufferSubData(target, offset, typedArray);
        }
        else {
            throw new GdxRuntimeException("Can only cope with FloatBuffer and ShortBuffer at the moment");
        }
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        return gl.checkFramebufferStatus(target);
    }

    @Override
    public void glClear(int mask) {
        gl.clear(mask);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        gl.clearColor(red, green, blue, alpha);
    }

    @Override
    public void glClearDepthf(float depth) {
        gl.clearDepth(depth);
    }

    @Override
    public void glClearStencil(int s) {
        gl.clearStencil(s);
    }

    @Override
    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        gl.colorMask(red, green, blue, alpha);
    }

    @Override
    public void glCompileShader(int shader) {
        WebGLShaderWrapper glShader = shaders.get(shader);
        gl.compileShader(glShader);
    }

    @Override
    public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
                                       int imageSize, Buffer data) {
        throw new GdxRuntimeException("compressed textures not supported by WebGL backend");
    }

    @Override
    public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
                                          int imageSize, Buffer data) {
        throw new GdxRuntimeException("compressed textures not supported by WebGL backend");
    }

    @Override
    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height, int border) {
        gl.copyTexImage2D(target, level, internalformat, x, y, width, height, border);
    }

    @Override
    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
        gl.copyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
    }

    @Override
    public int glCreateProgram() {
        WebGLProgramWrapper program = gl.createProgram();
        return programs.add(program);
    }

    @Override
    public int glCreateShader(int type) {
        WebGLShaderWrapper shader = gl.createShader(type);
        return shaders.add(shader);
    }

    @Override
    public void glCullFace(int mode) {
        gl.cullFace(mode);
    }

    @Override
    public void glDeleteBuffer(int id) {
        WebGLBufferWrapper buffer = this.buffers.remove(id);
        gl.deleteBuffer(buffer);
    }

    @Override
    public void glDeleteBuffers(int n, IntBuffer buffers) {
        for(int i = 0; i < n; i++) {
            int id = buffers.get();
            WebGLBufferWrapper buffer = this.buffers.remove(id);
            gl.deleteBuffer(buffer);
        }
    }

    @Override
    public void glDeleteFramebuffer(int id) {
        WebGLFramebufferWrapper fb = this.frameBuffers.remove(id);
        gl.deleteFramebuffer(fb);
    }

    @Override
    public void glDeleteFramebuffers(int n, IntBuffer framebuffers) {
        for(int i = 0; i < n; i++) {
            int id = framebuffers.get();
            WebGLFramebufferWrapper fb = this.frameBuffers.remove(id);
            gl.deleteFramebuffer(fb);
        }
    }

    @Override
    public void glDeleteProgram(int program) {
        WebGLProgramWrapper prog = programs.get(program);
        programs.remove(program);
        uniforms.remove(program);
        gl.deleteProgram(prog);
    }

    @Override
    public void glDeleteRenderbuffer(int id) {
        WebGLRenderbufferWrapper rb = this.renderBuffers.remove(id);
        gl.deleteRenderbuffer(rb);
    }

    @Override
    public void glDeleteRenderbuffers(int n, IntBuffer renderbuffers) {
        for(int i = 0; i < n; i++) {
            int id = renderbuffers.get();
            WebGLRenderbufferWrapper rb = this.renderBuffers.remove(id);
            gl.deleteRenderbuffer(rb);
        }
    }

    @Override
    public void glDeleteShader(int shader) {
        WebGLShaderWrapper sh = shaders.remove(shader);
        gl.deleteShader(sh);
    }

    @Override
    public void glDeleteTexture(int id) {
        WebGLTextureWrapper texture = this.textures.remove(id);
        gl.deleteTexture(texture);
    }

    @Override
    public void glDeleteTextures(int n, IntBuffer textures) {
        for(int i = 0; i < n; i++) {
            int id = textures.get();
            WebGLTextureWrapper texture = this.textures.remove(id);
            gl.deleteTexture(texture);
        }
    }

    @Override
    public void glDepthFunc(int func) {
        gl.depthFunc(func);
    }

    @Override
    public void glDepthMask(boolean flag) {
        gl.depthMask(flag);
    }

    @Override
    public void glDepthRangef(float zNear, float zFar) {
        gl.depthRange(zNear, zFar);
    }

    @Override
    public void glDetachShader(int program, int shader) {
        gl.detachShader(programs.get(program), shaders.get(shader));
    }

    @Override
    public void glDisable(int cap) {
        gl.disable(cap);
    }

    @Override
    public void glDisableVertexAttribArray(int index) {
        gl.disableVertexAttribArray(index);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        gl.drawArrays(mode, first, count);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        gl.drawElements(mode, count, type, indices.position()); // FIXME this is assuming WebGL supports client side buffers...
    }

    @Override
    public void glDrawElements(int mode, int count, int type, int indices) {
        gl.drawElements(mode, count, type, indices);
    }

    @Override
    public void glEnable(int cap) {
        gl.enable(cap);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        gl.enableVertexAttribArray(index);
    }

    @Override
    public void glFinish() {
        gl.finish();
    }

    @Override
    public void glFlush() {
        gl.flush();
    }

    @Override
    public void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
        gl.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderBuffers.get(renderbuffer));
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        gl.framebufferTexture2D(target, attachment, textarget, textures.get(texture), level);
    }

    @Override
    public void glFrontFace(int mode) {
        gl.frontFace(mode);
    }

    @Override
    public int glGenBuffer() {
        WebGLBufferWrapper buffer = gl.createBuffer();
        return buffers.add(buffer);
    }

    @Override
    public void glGenBuffers(int n, IntBuffer buffers) {
        for(int i = 0; i < n; i++) {
            WebGLBufferWrapper buffer = gl.createBuffer();
            int id = this.buffers.add(buffer);
            buffers.put(id);
        }
        buffers.limit(n);
    }

    @Override
    public void glGenerateMipmap(int target) {
        gl.generateMipmap(target);
    }

    @Override
    public int glGenFramebuffer() {
        WebGLFramebufferWrapper fb = gl.createFramebuffer();
        return frameBuffers.add(fb);
    }

    @Override
    public void glGenFramebuffers(int n, IntBuffer framebuffers) {
        for(int i = 0; i < n; i++) {
            WebGLFramebufferWrapper fb = gl.createFramebuffer();
            int id = this.frameBuffers.add(fb);
            framebuffers.put(id);
        }
        framebuffers.limit(n);
    }

    @Override
    public int glGenRenderbuffer() {
        WebGLRenderbufferWrapper rb = gl.createRenderbuffer();
        return renderBuffers.add(rb);
    }

    @Override
    public void glGenRenderbuffers(int n, IntBuffer renderbuffers) {
        for(int i = 0; i < n; i++) {
            WebGLRenderbufferWrapper rb = gl.createRenderbuffer();
            int id = this.renderBuffers.add(rb);
            renderbuffers.put(id);
        }
        renderbuffers.limit(n);
    }

    @Override
    public int glGenTexture() {
        WebGLTextureWrapper texture = gl.createTexture();
        return textures.add(texture);
    }

    @Override
    public void glGenTextures(int n, IntBuffer textures) {
        for(int i = 0; i < n; i++) {
            WebGLTextureWrapper texture = gl.createTexture();
            int id = this.textures.add(texture);
            textures.put(id);
        }
        textures.limit(n);
    }

    @Override
    public String glGetActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        WebGLActiveInfoWrapper activeUniform = gl.getActiveAttrib(programs.get(program), index);
        size.put(activeUniform.getSize());
        size.flip();
        type.put(activeUniform.getType());
        type.flip();
        return activeUniform.getName();
    }

    @Override
    public String glGetActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        WebGLActiveInfoWrapper activeUniform = gl.getActiveUniform(programs.get(program), index);
        size.put(activeUniform.getSize());
        size.flip();
        type.put(activeUniform.getType());
        type.flip();
        return activeUniform.getName();
    }

    @Override
    public void glGetAttachedShaders(int program, int maxcount, Buffer count, IntBuffer shaders) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public int glGetAttribLocation(int program, String name) {
        WebGLProgramWrapper prog = programs.get(program);
        return gl.getAttribLocation(prog, name);
    }

    @Override
    public void glGetBooleanv(int pname, Buffer params) {
        throw new GdxRuntimeException("glGetBoolean not supported by WebGL backend");
    }

    @Override
    public void glGetBufferParameteriv(int target, int pname, IntBuffer params) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public int glGetError() {
        return gl.getError();
    }

    @Override
    public void glGetFloatv(int pname, FloatBuffer params) {
        if(pname == GL20.GL_DEPTH_CLEAR_VALUE || pname == GL20.GL_LINE_WIDTH || pname == GL20.GL_POLYGON_OFFSET_FACTOR
                || pname == GL20.GL_POLYGON_OFFSET_UNITS || pname == GL20.GL_SAMPLE_COVERAGE_VALUE) {
            params.put(0, gl.getParameterf(pname));
            params.limit(1);
        }
        else
            throw new GdxRuntimeException("glGetFloat not supported by WebGL backend");
    }

    @Override
    public void glGetFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        switch(pname) {
            case GL20.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE:
            case GL20.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL:
            case GL20.GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE:
                params.put(0, gl.getFramebufferAttachmentParameteri(target, attachment, pname));
                params.limit(1);
                break;
            case GL20.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME:
                WebGLTextureWrapper tex = (WebGLTextureWrapper)gl.getParametero(pname);
                if(tex == null) {
                    params.put(0);
                }
                else {
                    params.put(textures.getKey(tex));
                }
                params.flip();
                return;
            default:
                throw new GdxRuntimeException("glGetFramebufferAttachmentParameteriv Invalid enum for WebGL backend.");
        }
    }

    @Override
    public void glGetIntegerv(int pname, IntBuffer params) {
        if(pname == GL20.GL_ACTIVE_TEXTURE || pname == GL20.GL_ALPHA_BITS || pname == GL20.GL_BLEND_DST_ALPHA
                || pname == GL20.GL_BLEND_DST_RGB || pname == GL20.GL_BLEND_EQUATION_ALPHA || pname == GL20.GL_BLEND_EQUATION_RGB
                || pname == GL20.GL_BLEND_SRC_ALPHA || pname == GL20.GL_BLEND_SRC_RGB || pname == GL20.GL_BLUE_BITS
                || pname == GL20.GL_CULL_FACE_MODE || pname == GL20.GL_DEPTH_BITS || pname == GL20.GL_DEPTH_FUNC
                || pname == GL20.GL_FRONT_FACE || pname == GL20.GL_GENERATE_MIPMAP_HINT || pname == GL20.GL_GREEN_BITS
                || pname == GL20.GL_IMPLEMENTATION_COLOR_READ_FORMAT || pname == GL20.GL_IMPLEMENTATION_COLOR_READ_TYPE
                || pname == GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS || pname == GL20.GL_MAX_CUBE_MAP_TEXTURE_SIZE
                || pname == GL20.GL_MAX_FRAGMENT_UNIFORM_VECTORS || pname == GL20.GL_MAX_RENDERBUFFER_SIZE
                || pname == GL20.GL_MAX_TEXTURE_IMAGE_UNITS || pname == GL20.GL_MAX_TEXTURE_SIZE || pname == GL20.GL_MAX_VARYING_VECTORS
                || pname == GL20.GL_MAX_VERTEX_ATTRIBS || pname == GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS
                || pname == GL20.GL_MAX_VERTEX_UNIFORM_VECTORS || pname == GL20.GL_NUM_COMPRESSED_TEXTURE_FORMATS
                || pname == GL20.GL_PACK_ALIGNMENT || pname == GL20.GL_RED_BITS || pname == GL20.GL_SAMPLE_BUFFERS
                || pname == GL20.GL_SAMPLES || pname == GL20.GL_STENCIL_BACK_FAIL || pname == GL20.GL_STENCIL_BACK_FUNC
                || pname == GL20.GL_STENCIL_BACK_PASS_DEPTH_FAIL || pname == GL20.GL_STENCIL_BACK_PASS_DEPTH_PASS
                || pname == GL20.GL_STENCIL_BACK_REF || pname == GL20.GL_STENCIL_BACK_VALUE_MASK
                || pname == GL20.GL_STENCIL_BACK_WRITEMASK || pname == GL20.GL_STENCIL_BITS || pname == GL20.GL_STENCIL_CLEAR_VALUE
                || pname == GL20.GL_STENCIL_FAIL || pname == GL20.GL_STENCIL_FUNC || pname == GL20.GL_STENCIL_PASS_DEPTH_FAIL
                || pname == GL20.GL_STENCIL_PASS_DEPTH_PASS || pname == GL20.GL_STENCIL_REF || pname == GL20.GL_STENCIL_VALUE_MASK
                || pname == GL20.GL_STENCIL_WRITEMASK || pname == GL20.GL_SUBPIXEL_BITS || pname == GL20.GL_UNPACK_ALIGNMENT) {
            params.put(0, gl.getParameteri(pname));
            params.limit(1);
        }
        else if(pname == GL20.GL_VIEWPORT) {
            Int32ArrayWrapper array = (Int32ArrayWrapper)gl.getParameterv(pname);
            params.put(0, array.get(0));
            params.put(1, array.get(1));
            params.put(2, array.get(2));
            params.put(3, array.get(3));
            params.limit(4);
        }
        else if(pname == GL20.GL_FRAMEBUFFER_BINDING) {
            WebGLFramebufferWrapper fbo = (WebGLFramebufferWrapper)gl.getParametero(pname);
            if(fbo == null) {
                params.put(0);
            }
            else {
                params.put(frameBuffers.getKey(fbo));
            }
            params.flip();
        }
        else
            throw new GdxRuntimeException("glGetInteger not supported by WebGL backend");
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        return gl.getProgramInfoLog(programs.get(program));
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        if(pname == GL20.GL_DELETE_STATUS || pname == GL20.GL_LINK_STATUS || pname == GL20.GL_VALIDATE_STATUS) {
            boolean result = gl.getProgramParameterb(programs.get(program), pname);
            params.put(result ? GL20.GL_TRUE : GL20.GL_FALSE);
        }
        else {
            params.put(gl.getProgramParameteri(programs.get(program), pname));
        }
        params.flip();
    }

    @Override
    public void glGetRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        return gl.getShaderInfoLog(shaders.get(shader));
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        if(pname == GL20.GL_COMPILE_STATUS || pname == GL20.GL_DELETE_STATUS) {
            boolean result = gl.getShaderParameterb(shaders.get(shader), pname);
            params.put(result ? GL20.GL_TRUE : GL20.GL_FALSE);
        }
        else {
            int result = gl.getShaderParameteri(shaders.get(shader), pname);
            params.put(result);
        }
        params.flip();
    }

    @Override
    public void glGetShaderPrecisionFormat(int shadertype, int precisiontype, IntBuffer range, IntBuffer precision) {
        throw new GdxRuntimeException("glGetShaderPrecisionFormat not supported by WebGL backend");
    }

    @Override
    public String glGetString(int name) {
        return gl.getParameterString(name);
    }

    @Override
    public void glGetTexParameterfv(int target, int pname, FloatBuffer params) {
        throw new GdxRuntimeException("glGetTexParameter not supported by WebGL backend");
    }

    @Override
    public void glGetTexParameteriv(int target, int pname, IntBuffer params) {
        throw new GdxRuntimeException("glGetTexParameter not supported by WebGL backend");
    }

    @Override
    public void glGetUniformfv(int program, int location, FloatBuffer params) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public void glGetUniformiv(int program, int location, IntBuffer params) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        WebGLUniformLocationWrapper location = gl.getUniformLocation(programs.get(program), name);
        if(location == null)
            return -1;

        CustomIntMap<WebGLUniformLocationWrapper> progUniforms = uniforms.get(program);
        if(progUniforms == null) {
            progUniforms = CustomIntMap.create();
            uniforms.put(program, progUniforms);
        }
        int id = progUniforms.add(location);
        return id;
    }

    @Override
    public void glGetVertexAttribfv(int index, int pname, FloatBuffer params) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public void glGetVertexAttribiv(int index, int pname, IntBuffer params) {
        // FIXME
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public void glGetVertexAttribPointerv(int index, int pname, Buffer pointer) {
        throw new GdxRuntimeException("glGetVertexAttribPointer not supported by WebGL backend");
    }

    @Override
    public void glHint(int target, int mode) {
        gl.hint(target, mode);
    }

    @Override
    public boolean glIsBuffer(int buffer) {
        return gl.isBuffer(buffers.get(buffer));
    }

    @Override
    public boolean glIsEnabled(int cap) {
        return gl.isEnabled(cap);
    }

    @Override
    public boolean glIsFramebuffer(int framebuffer) {
        return gl.isFramebuffer(frameBuffers.get(framebuffer));
    }

    @Override
    public boolean glIsProgram(int program) {
        return gl.isProgram(programs.get(program));
    }

    @Override
    public boolean glIsRenderbuffer(int renderbuffer) {
        return gl.isRenderbuffer(renderBuffers.get(renderbuffer));
    }

    @Override
    public boolean glIsShader(int shader) {
        return gl.isShader(shaders.get(shader));
    }

    @Override
    public boolean glIsTexture(int texture) {
        return gl.isTexture(textures.get(texture));
    }

    @Override
    public void glLinkProgram(int program) {
        gl.linkProgram(programs.get(program));
    }

    @Override
    public void glLineWidth(float width) {
        gl.lineWidth(width);
    }

    @Override
    public void glPixelStorei(int pname, int param) {
        gl.pixelStorei(pname, param);
    }

    @Override
    public void glPolygonOffset(float factor, float units) {
        gl.polygonOffset(factor, units);
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
        // verify request
        if((format != WebGLRenderingContextWrapper.RGBA) || (type != WebGLRenderingContextWrapper.UNSIGNED_BYTE)) {
            throw new GdxRuntimeException(
                    "Only format RGBA and type UNSIGNED_BYTE are currently supported for glReadPixels(...). Create an issue when you need other formats.");
        }
        ArrayBufferViewWrapper typedArray = null;
        if(pixels instanceof ByteBuffer) {
            typedArray = TypedArrays.getTypedArray((ByteBuffer)pixels);
        }
        else if(pixels instanceof FloatBuffer) {
            typedArray = TypedArrays.getTypedArray((FloatBuffer)pixels);
        }
        else if(pixels instanceof ShortBuffer) {
            typedArray = TypedArrays.getTypedArray((ShortBuffer)pixels);
        }
        else if(pixels instanceof IntBuffer) {
            typedArray = TypedArrays.getTypedArray((IntBuffer)pixels);
        }
        else {
            throw new GdxRuntimeException("Inputed pixels buffer not supported.");
        }

        // create new ArrayBufferView (4 bytes per pixel)
        int size = 4 * width * height;
        Uint8ArrayWrapper buffer = TypedArrays.createUint8Array(typedArray.getBuffer(), typedArray.getByteOffset(), size);

        gl.readPixels(x, y, width, height, format, type, buffer);
    }

    @Override
    public void glReleaseShaderCompiler() {
        throw new GdxRuntimeException("not implemented");
    }

    @Override
    public void glRenderbufferStorage(int target, int internalformat, int width, int height) {
        gl.renderbufferStorage(target, internalformat, width, height);
    }

    @Override
    public void glSampleCoverage(float value, boolean invert) {
        gl.sampleCoverage(value, invert);
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        gl.scissor(x, y, width, height);
    }

    @Override
    public void glShaderBinary(int n, IntBuffer shaders, int binaryformat, Buffer binary, int length) {
        throw new GdxRuntimeException("glShaderBinary not supported by WebGL backend");
    }

    @Override
    public void glShaderSource(int shader, String source) {
        gl.shaderSource(shaders.get(shader), source);
    }

    @Override
    public void glStencilFunc(int func, int ref, int mask) {
        gl.stencilFunc(func, ref, mask);
    }

    @Override
    public void glStencilFuncSeparate(int face, int func, int ref, int mask) {
        gl.stencilFuncSeparate(face, func, ref, mask);
    }

    @Override
    public void glStencilMask(int mask) {
        gl.stencilMask(mask);
    }

    @Override
    public void glStencilMaskSeparate(int face, int mask) {
        gl.stencilMaskSeparate(face, mask);
    }

    @Override
    public void glStencilOp(int fail, int zfail, int zpass) {
        gl.stencilOp(fail, zfail, zpass);
    }

    @Override
    public void glStencilOpSeparate(int face, int fail, int zfail, int zpass) {
        gl.stencilOpSeparate(face, fail, zfail, zpass);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        if(pixels == null) {
            gl.texImage2D(target, level, internalformat, width, height, border, format, type, null);
            return;
        }

        ArrayBufferViewWrapper buffer;
        if(pixels instanceof ByteBuffer) {
            ArrayBufferViewWrapper typedArrayBuffer = TypedArrays.getTypedArray((ByteBuffer)pixels);
            int remainingBytes = pixels.remaining();
            int byteOffset = typedArrayBuffer.getByteOffset() + pixels.position();
            buffer = TypedArrays.createUint8Array(typedArrayBuffer.getBuffer(), byteOffset, remainingBytes);
            gl.texImage2D(target, level, internalformat, width, height, border, format, type, buffer);
        }
        else if(pixels instanceof FloatBuffer) {
            ArrayBufferViewWrapper typedArrayBuffer = TypedArrays.getTypedArray((FloatBuffer)pixels);
            int remainingBytes = pixels.remaining();
            int byteOffset = typedArrayBuffer.getByteOffset() + pixels.position();
            buffer = TypedArrays.createFloat32Array(typedArrayBuffer.getBuffer(), byteOffset, remainingBytes);
            gl.texImage2D(target, level, internalformat, width, height, border, format, type, buffer);
        }
        else {
            throw new GdxRuntimeException("Not supported buffer");
        }
    }

    @Override
    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, Buffer pixels) {
        ArrayBufferViewWrapper buffer;
        if(pixels instanceof ByteBuffer) {
            ArrayBufferViewWrapper typedArrayBuffer = TypedArrays.getTypedArray((ByteBuffer)pixels);
            int remainingBytes = pixels.remaining();
            int byteOffset = typedArrayBuffer.getByteOffset() + pixels.position();
            buffer = TypedArrays.createUint8Array(typedArrayBuffer.getBuffer(), byteOffset, remainingBytes);
            gl.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, buffer);
        }
        else if(pixels instanceof FloatBuffer) {
            ArrayBufferViewWrapper typedArrayBuffer = TypedArrays.getTypedArray((FloatBuffer)pixels);
            int remainingBytes = pixels.remaining();
            int byteOffset = typedArrayBuffer.getByteOffset() + pixels.position();
            buffer = TypedArrays.createFloat32Array(typedArrayBuffer.getBuffer(), byteOffset, remainingBytes);
            gl.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, buffer);
        }
        else {
            throw new GdxRuntimeException("Not supported buffer");
        }
    }

    @Override
    public void glTexParameterf(int target, int pname, float param) {
        gl.texParameterf(target, pname, param);
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        gl.texParameterf(target, pname, params.get());
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        gl.texParameterf(target, pname, param);
    }

    @Override
    public void glTexParameteriv(int target, int pname, IntBuffer params) {
        gl.texParameterf(target, pname, params.get());
    }

    @Override
    public void glUniform1f(int location, float x) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform1f(loc, x);
    }

    @Override
    public void glUniform1fv(int location, int count, FloatBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform1fv(loc, copy(v));
    }

    @Override
    public void glUniform1fv(int location, int count, float[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform1fv(loc, v);
    }

    @Override
    public void glUniform1i(int location, int x) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform1i(loc, x);
    }

    @Override
    public void glUniform1iv(int location, int count, IntBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform1iv(loc, copy(v));
    }

    @Override
    public void glUniform1iv(int location, int count, int[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform1iv(loc, v);
    }

    @Override
    public void glUniform2f(int location, float x, float y) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform2f(loc, x, y);
    }

    @Override
    public void glUniform2fv(int location, int count, FloatBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform2fv(loc, copy(v));
    }

    @Override
    public void glUniform2fv(int location, int count, float[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform2fv(loc, v);
    }

    @Override
    public void glUniform2i(int location, int x, int y) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform2i(loc, x, y);
    }

    @Override
    public void glUniform2iv(int location, int count, IntBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform2iv(loc, copy(v));
    }

    @Override
    public void glUniform2iv(int location, int count, int[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform2iv(loc, v);
    }

    @Override
    public void glUniform3f(int location, float x, float y, float z) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform3f(loc, x, y, z);
    }

    @Override
    public void glUniform3fv(int location, int count, FloatBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform3fv(loc, copy(v));
    }

    @Override
    public void glUniform3fv(int location, int count, float[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform3fv(loc, v);
    }

    @Override
    public void glUniform3i(int location, int x, int y, int z) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform3i(loc, x, y, z);
    }

    @Override
    public void glUniform3iv(int location, int count, IntBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform3iv(loc, copy(v));
    }

    @Override
    public void glUniform3iv(int location, int count, int[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform3iv(loc, v);
    }

    @Override
    public void glUniform4f(int location, float x, float y, float z, float w) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform4f(loc, x, y, z, w);
    }

    @Override
    public void glUniform4fv(int location, int count, FloatBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform4fv(loc, copy(v));
    }

    @Override
    public void glUniform4fv(int location, int count, float[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform4fv(loc, v);
    }

    @Override
    public void glUniform4i(int location, int x, int y, int z, int w) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform4i(loc, x, y, z, w);
    }

    @Override
    public void glUniform4iv(int location, int count, IntBuffer v) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform4iv(loc, copy(v));
    }

    @Override
    public void glUniform4iv(int location, int count, int[] v, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniform4iv(loc, v);
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniformMatrix2fv(loc, transpose, copy(value));
    }

    @Override
    public void glUniformMatrix2fv(int location, int count, boolean transpose, float[] value, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniformMatrix2fv(loc, transpose, value);
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniformMatrix3fv(loc, transpose, copy(value));
    }

    @Override
    public void glUniformMatrix3fv(int location, int count, boolean transpose, float[] value, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniformMatrix3fv(loc, transpose, value);
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer value) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniformMatrix4fv(loc, transpose, copy(value));
    }

    @Override
    public void glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int offset) {
        WebGLUniformLocationWrapper loc = getUniformLocation(location);
        gl.uniformMatrix4fv(loc, transpose, value);
    }

    @Override
    public void glUseProgram(int program) {
        currProgram = program;
        gl.useProgram(programs.get(program));
    }

    @Override
    public void glValidateProgram(int program) {
        gl.validateProgram(programs.get(program));
    }

    @Override
    public void glVertexAttrib1f(int indx, float x) {
        gl.vertexAttrib1f(indx, x);
    }

    @Override
    public void glVertexAttrib1fv(int indx, FloatBuffer values) {
        gl.vertexAttrib1fv(indx, copy(values));
    }

    @Override
    public void glVertexAttrib2f(int indx, float x, float y) {
        gl.vertexAttrib2f(indx, x, y);
    }

    @Override
    public void glVertexAttrib2fv(int indx, FloatBuffer values) {
        gl.vertexAttrib2fv(indx, copy(values));
    }

    @Override
    public void glVertexAttrib3f(int indx, float x, float y, float z) {
        gl.vertexAttrib3f(indx, x, y, z);
    }

    @Override
    public void glVertexAttrib3fv(int indx, FloatBuffer values) {
        gl.vertexAttrib3fv(indx, copy(values));
    }

    @Override
    public void glVertexAttrib4f(int indx, float x, float y, float z, float w) {
        gl.vertexAttrib4f(indx, x, y, z, w);
    }

    @Override
    public void glVertexAttrib4fv(int indx, FloatBuffer values) {
        gl.vertexAttrib4fv(indx, copy(values));
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, Buffer ptr) {
        throw new GdxRuntimeException("not implemented, vertex arrays aren't support in WebGL it seems");
    }

    @Override
    public void glVertexAttribPointer(int indx, int size, int type, boolean normalized, int stride, int ptr) {
        gl.vertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        gl.viewport(x, y, width, height);
    }
}