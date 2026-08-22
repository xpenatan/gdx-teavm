package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Browser regression test for uploading the active {@link FloatBuffer} range through
 * {@link GL20#glBufferData(int, int, java.nio.Buffer, int)} and
 * {@link GL20#glBufferSubData(int, int, int, java.nio.Buffer)}.
 *
 * <p>Each probe uploads the same partial direct-buffer range twice and changes its contents between uploads. This
 * also validates that a reused WebGL 1 range view remains zero-copy. The left half reports the
 * {@code glBufferData} result and the right half reports the {@code glBufferSubData} result. Green means pass and
 * red means fail. Detailed results are also logged.</p>
 */
public class WebGLBufferRangeTest extends ApplicationAdapter {
    private static final String TAG = "WebGLBufferRangeTest";
    private static final int ACTIVE_FLOAT_OFFSET = 6;
    private static final int ACTIVE_FLOAT_COUNT = 6;
    private static final int ACTIVE_BYTE_COUNT = ACTIVE_FLOAT_COUNT * Float.BYTES;

    private ShaderProgram shader;
    private int positionAttribute;
    private int bufferDataHandle;
    private int bufferSubDataHandle;
    private int bufferDataError;
    private int bufferSubDataAllocationError;
    private int bufferSubDataError;
    private boolean bufferDataSourceStatePreserved;
    private boolean bufferSubDataSourceStatePreserved;
    private boolean bufferDataPassed;
    private boolean bufferSubDataPassed;
    private boolean evaluated;

    @Override
    public void create() {
        shader = createShader();
        positionAttribute = shader.getAttributeLocation("a_position");
        if(positionAttribute < 0) {
            throw new GdxRuntimeException("Shader attribute a_position was not found");
        }

        bufferDataHandle = Gdx.gl20.glGenBuffer();
        bufferSubDataHandle = Gdx.gl20.glGenBuffer();
        setupBufferDataProbe();
        setupBufferSubDataProbe();
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
    }

    private void setupBufferDataProbe() {
        FloatBuffer source = createSourceBuffer(new float[] {
                2f, 2f, 2f, 3f, 3f, 2f,
                2f, 2f, 2f, 3f, 3f, 2f,
                -2f, -2f
        });

        clearErrors();
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferDataHandle);
        Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, ACTIVE_BYTE_COUNT, source, GL20.GL_STATIC_DRAW);
        replaceActiveRange(source, new float[] {-0.9f, -0.4f, -0.1f, -0.4f, -0.5f, 0.4f});
        Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, ACTIVE_BYTE_COUNT, source, GL20.GL_STATIC_DRAW);
        bufferDataError = Gdx.gl20.glGetError();
        bufferDataSourceStatePreserved = isSourceStatePreserved(source);
    }

    private void setupBufferSubDataProbe() {
        FloatBuffer source = createSourceBuffer(new float[] {
                2f, 2f, 2f, 3f, 3f, 2f,
                2f, 2f, 2f, 3f, 3f, 2f,
                -2f, -2f
        });

        clearErrors();
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferSubDataHandle);
        Gdx.gl20.glBufferData(GL20.GL_ARRAY_BUFFER, ACTIVE_BYTE_COUNT, null, GL20.GL_STATIC_DRAW);
        bufferSubDataAllocationError = Gdx.gl20.glGetError();
        Gdx.gl20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, ACTIVE_BYTE_COUNT, source);
        replaceActiveRange(source, new float[] {0.1f, -0.4f, 0.9f, -0.4f, 0.5f, 0.4f});
        Gdx.gl20.glBufferSubData(GL20.GL_ARRAY_BUFFER, 0, ACTIVE_BYTE_COUNT, source);
        bufferSubDataError = Gdx.gl20.glGetError();
        bufferSubDataSourceStatePreserved = isSourceStatePreserved(source);
    }

    private FloatBuffer createSourceBuffer(float[] values) {
        FloatBuffer source = BufferUtils.newFloatBuffer(values.length);
        source.put(values);
        source.position(ACTIVE_FLOAT_OFFSET);
        source.mark();
        source.limit(ACTIVE_FLOAT_OFFSET + ACTIVE_FLOAT_COUNT);
        return source;
    }

    private void replaceActiveRange(FloatBuffer source, float[] values) {
        for(int i = 0; i < values.length; i++) {
            source.put(ACTIVE_FLOAT_OFFSET + i, values[i]);
        }
    }

    private boolean isSourceStatePreserved(FloatBuffer source) {
        if(source.position() != ACTIVE_FLOAT_OFFSET
                || source.limit() != ACTIVE_FLOAT_OFFSET + ACTIVE_FLOAT_COUNT) {
            return false;
        }

        try {
            source.position(ACTIVE_FLOAT_OFFSET + 1);
            source.reset();
            return source.position() == ACTIVE_FLOAT_OFFSET;
        }
        catch(Exception ignored) {
            return false;
        }
    }

    @Override
    public void render() {
        if(!evaluated) {
            evaluateUploads();
            evaluated = true;
        }
        renderResult();
    }

    private void evaluateUploads() {
        int width = Gdx.graphics.getBackBufferWidth();
        int height = Gdx.graphics.getBackBufferHeight();

        Gdx.gl20.glViewport(0, 0, width, height);
        Gdx.gl20.glDisable(GL20.GL_BLEND);
        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
        Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shader.bind();
        Gdx.gl20.glEnableVertexAttribArray(positionAttribute);
        drawProbe(bufferDataHandle);
        drawProbe(bufferSubDataHandle);
        Gdx.gl20.glDisableVertexAttribArray(positionAttribute);
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);

        int[] bufferDataPixel = readPixel(width * 0.25f, height * 0.433f, width, height);
        int[] bufferSubDataPixel = readPixel(width * 0.75f, height * 0.433f, width, height);

        bufferDataPassed = bufferDataError == GL20.GL_NO_ERROR
                && bufferDataSourceStatePreserved
                && isWhite(bufferDataPixel);
        bufferSubDataPassed = bufferSubDataAllocationError == GL20.GL_NO_ERROR
                && bufferSubDataError == GL20.GL_NO_ERROR
                && bufferSubDataSourceStatePreserved
                && isWhite(bufferSubDataPixel);

        logResult("glBufferData", bufferDataPassed, bufferDataError,
                bufferDataSourceStatePreserved, bufferDataPixel);
        logResult("glBufferSubData", bufferSubDataPassed, bufferSubDataError,
                bufferSubDataSourceStatePreserved, bufferSubDataPixel);
        if(bufferSubDataAllocationError != GL20.GL_NO_ERROR) {
            Gdx.app.error(TAG, "glBufferSubData allocation GL error: " + bufferSubDataAllocationError);
        }
        Gdx.app.log(TAG, bufferDataPassed && bufferSubDataPassed
                ? "PASS: both uploads respected FloatBuffer.position() and limit()"
                : "FAIL: left=glBufferData, right=glBufferSubData; green=pass, red=fail");
    }

    private void drawProbe(int bufferHandle) {
        Gdx.gl20.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
        Gdx.gl20.glVertexAttribPointer(positionAttribute, 2, GL20.GL_FLOAT, false, 0, 0);
        Gdx.gl20.glDrawArrays(GL20.GL_TRIANGLES, 0, 3);
    }

    private int[] readPixel(float x, float y, int width, int height) {
        int pixelX = Math.max(0, Math.min(width - 1, (int)x));
        int pixelY = Math.max(0, Math.min(height - 1, (int)y));
        ByteBuffer pixel = BufferUtils.newByteBuffer(4);
        Gdx.gl20.glReadPixels(pixelX, pixelY, 1, 1, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixel);
        return new int[] {
                Byte.toUnsignedInt(pixel.get(0)),
                Byte.toUnsignedInt(pixel.get(1)),
                Byte.toUnsignedInt(pixel.get(2)),
                Byte.toUnsignedInt(pixel.get(3))
        };
    }

    private boolean isWhite(int[] pixel) {
        return pixel[0] > 200 && pixel[1] > 200 && pixel[2] > 200;
    }

    private void logResult(String operation, boolean passed, int error, boolean sourceStatePreserved, int[] pixel) {
        String message = operation + ": " + (passed ? "PASS" : "FAIL")
                + ", GL error=" + error
                + ", source state preserved=" + sourceStatePreserved
                + ", sampled RGBA=" + pixel[0] + "," + pixel[1] + "," + pixel[2] + "," + pixel[3];
        if(passed) {
            Gdx.app.log(TAG, message);
        }
        else {
            Gdx.app.error(TAG, message);
        }
    }

    private void renderResult() {
        int width = Gdx.graphics.getBackBufferWidth();
        int height = Gdx.graphics.getBackBufferHeight();
        int leftWidth = width / 2;

        Gdx.gl20.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl20.glScissor(0, 0, leftWidth, height);
        clearResultColor(bufferDataPassed);
        Gdx.gl20.glScissor(leftWidth, 0, width - leftWidth, height);
        clearResultColor(bufferSubDataPassed);
        Gdx.gl20.glDisable(GL20.GL_SCISSOR_TEST);
    }

    private void clearResultColor(boolean passed) {
        if(passed) {
            Gdx.gl20.glClearColor(0f, 0.65f, 0f, 1f);
        }
        else {
            Gdx.gl20.glClearColor(0.75f, 0f, 0f, 1f);
        }
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void clearErrors() {
        for(int i = 0; i < 16 && Gdx.gl20.glGetError() != GL20.GL_NO_ERROR; i++) {
            // Drain errors so each probe reports only its own operation.
        }
    }

    private ShaderProgram createShader() {
        String vertexShader;
        String fragmentShader;
        if(Gdx.gl30 != null) {
            vertexShader = "#version 300 es\n"
                    + "in vec2 a_position;\n"
                    + "void main() { gl_Position = vec4(a_position, 0.0, 1.0); }\n";
            fragmentShader = "#version 300 es\n"
                    + "precision mediump float;\n"
                    + "out vec4 fragmentColor;\n"
                    + "void main() { fragmentColor = vec4(1.0); }\n";
        }
        else {
            vertexShader = "attribute vec2 a_position;\n"
                    + "void main() { gl_Position = vec4(a_position, 0.0, 1.0); }\n";
            fragmentShader = "#ifdef GL_ES\n"
                    + "precision mediump float;\n"
                    + "#endif\n"
                    + "void main() { gl_FragColor = vec4(1.0); }\n";
        }

        ShaderProgram result = new ShaderProgram(vertexShader, fragmentShader);
        if(!result.isCompiled()) {
            throw new GdxRuntimeException("Shader compilation failed: " + result.getLog());
        }
        return result;
    }

    @Override
    public void dispose() {
        if(bufferDataHandle != 0) {
            Gdx.gl20.glDeleteBuffer(bufferDataHandle);
        }
        if(bufferSubDataHandle != 0) {
            Gdx.gl20.glDeleteBuffer(bufferSubDataHandle);
        }
        if(shader != null) {
            shader.dispose();
        }
    }
}
