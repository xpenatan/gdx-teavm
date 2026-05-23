package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.text.DecimalFormat;

public class SpriteBatchTest2 extends ApplicationAdapter {
    private static final String TAG = "SpriteBatchTest";
    private static final boolean BENCHMARK_SWEEP = false;
    private static final boolean BENCHMARK_EXIT_AFTER_MEASURE = true;
    private static final int BENCHMARK_WARMUP_SECONDS = 1;
    private static final int BENCHMARK_MEASURE_SECONDS = 12;
    private static final int MODE_DEFAULT = 0;
    private static final int MODE_DIRECT_SPRITE_GETTERS = 1;
    private static final int MODE_DIRECT_ARRAY_STATE = 2;
    private static final int MODE_SIMPLE_DIRECT = 3;
    private static final int MODE_PRECOMPUTED_ARRAYCOPY = 4;
    private static final int MODE_FRAME_ONLY = 5;
    private static final int MODE_CLEAR_ONLY = 6;
    private static final int MODE_EMPTY_LOOP = 7;
    private static final int MODE_ARRAY_READ_LOOP = 8;
    private static final int MODE_MANUAL_VERTEX_WRITE = 9;
    private static final int MODE_BEGIN_END_ONLY = 10;
    private static final int[] BENCHMARK_MODES = {
            MODE_DEFAULT,
            MODE_FRAME_ONLY,
            MODE_CLEAR_ONLY,
            MODE_EMPTY_LOOP,
            MODE_ARRAY_READ_LOOP,
            MODE_MANUAL_VERTEX_WRITE,
            MODE_BEGIN_END_ONLY,
            MODE_SIMPLE_DIRECT
    };

    DecimalFormat df = new DecimalFormat("000.0000000000");
    int SPRITES = 8191;

    long startTime = TimeUtils.nanoTime();
    int frames = 0;
    Texture texture;
    SpriteBatch spriteBatch;
    ScreenViewport viewport;
    Sprite[] sprites = new Sprite[SPRITES];
    float[] spriteX = new float[SPRITES];
    float[] spriteY = new float[SPRITES];
    float[] spriteRotation = new float[SPRITES];
    float[] sharedVertices = new float[20];
    float[] manualVertices = new float[SPRITES * 20];
    float ROTATION_SPEED = 20;
    float scale = 1;
    float SCALE_SPEED = -1;
    float sharedRotation = 0;
    float sharedX1, sharedY1, sharedX2, sharedY2, sharedX3, sharedY3, sharedX4, sharedY4;
    float sharedU, sharedV, sharedU2, sharedV2, sharedColor;
    int benchmarkMode = MODE_DEFAULT;
    int benchmarkModeIndex;
    int benchmarkIntSink;
    float benchmarkFloatSink;
    long modeStartTime;
    long measureStartTime;
    int measuredFrames;
    boolean measuringMode;
    StringBuilder benchmarkResults = new StringBuilder();

    @Override
    public void create() {
        Gdx.app.log(TAG, "[" + this.hashCode() + "] create() START"); // Log instance and start

        viewport = new ScreenViewport();
        spriteBatch = new SpriteBatch(SPRITES);

        // TODO need to support texture constructor that calls native ETC1 methods
        texture = new Texture(Gdx.files.internal("data/badlogicsmall.jpg"));

    }

    private void generateSprites(int screenWidth, int screenHeight) {
        int width = 32;
        int height = 32;

        try {
            for (int i = 0; i < SPRITES; i++) {
                int x = (int) (Math.random() * screenWidth);
                int y = (int) (Math.random() * screenHeight);
                if (sprites[i] == null) {
                    sprites[i] = new Sprite(texture, width, height);
                    sprites[i].setOrigin(width * 0.5f, height * 0.5f);
                }
                sprites[i].setOriginBasedPosition(x, y);
                spriteX[i] = x - width * 0.5f;
                spriteY[i] = y - height * 0.5f;
                spriteRotation[i] = 0;

            }
        } catch (Throwable t) {
            Gdx.app.error(TAG, "[" + this.hashCode() + "] Error during sprite creation!", t);
            throw t; // Re-throw to ensure failure is visible
        }
        Gdx.app.log(TAG, "[" + this.hashCode() + "] create() END"); // Log instance and end
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Resize: " + width + " x " + height);
        viewport.update(width, height, true);
        generateSprites(width, height);
    }

    @Override
    public void render() {
        if (modeStartTime == 0) {
            if(BENCHMARK_SWEEP)
                startBenchmarkMode(0);
            else
                startBenchmarkMode(benchmarkMode);
        }

        float begin = 0;
        float end = 0;
        float draw1 = 0;
        float drawText = 0;
        long start;

        if (benchmarkMode == MODE_FRAME_ONLY) {
            // Intentionally empty: measures application loop and benchmark bookkeeping.
        }
        else if (benchmarkMode == MODE_CLEAR_ONLY) {
            ScreenUtils.clear(0, 0, 0, 1);
        }
        else if (benchmarkMode == MODE_EMPTY_LOOP) {
            start = TimeUtils.nanoTime();
            runEmptyLoop();
            draw1 = (TimeUtils.nanoTime() - start) / 1000000000.0f;
        }
        else if (benchmarkMode == MODE_ARRAY_READ_LOOP) {
            start = TimeUtils.nanoTime();
            runArrayReadLoop();
            draw1 = (TimeUtils.nanoTime() - start) / 1000000000.0f;
        }
        else if (benchmarkMode == MODE_MANUAL_VERTEX_WRITE) {
            start = TimeUtils.nanoTime();
            runManualVertexWriteLoop();
            draw1 = (TimeUtils.nanoTime() - start) / 1000000000.0f;
        }
        else {
            ScreenUtils.clear(0, 0, 0, 1);
            viewport.apply();
            spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

            start = TimeUtils.nanoTime();
            spriteBatch.begin();
            begin = (TimeUtils.nanoTime() - start) / 1000000000.0f;

            float angleInc = ROTATION_SPEED * Gdx.graphics.getDeltaTime();
            scale += SCALE_SPEED * Gdx.graphics.getDeltaTime();
            if (scale < 0.5f) {
                scale = 0.5f;
                SCALE_SPEED = 1;
            }
            if (scale > 1.0f) {
                scale = 1.0f;
                SCALE_SPEED = -1;
            }
            if (benchmarkMode == MODE_PRECOMPUTED_ARRAYCOPY) {
                prepareSharedTransform(angleInc);
            }

            start = TimeUtils.nanoTime();
            if (benchmarkMode == MODE_BEGIN_END_ONLY) {
                // Nothing to draw; isolates SpriteBatch begin/end and projection setup.
            }
            else {
                for (int i = 0; i < SPRITES; i++) {
                    drawSprite(i, angleInc);
                }
            }
            draw1 = (TimeUtils.nanoTime() - start) / 1000000000.0f;

            start = TimeUtils.nanoTime();
            drawText = (TimeUtils.nanoTime() - start) / 1000000000.0f;

            start = TimeUtils.nanoTime();
            spriteBatch.end();
            end = (TimeUtils.nanoTime() - start) / 1000000000.0f;
        }

        if (TimeUtils.nanoTime() - startTime > 1000000000) {
            Gdx.app.log(TAG,
                    "mode: " + benchmarkModeName(benchmarkMode) + ", fps: " + frames + ", render calls: " + spriteBatch.renderCalls + ", begin: " + df.format(begin)
                            + ", " + df.format(draw1) + ", " + df.format(drawText) + ", end: " + df.format(end));
            frames = 0;
            startTime = TimeUtils.nanoTime();
        }
        if (BENCHMARK_SWEEP || BENCHMARK_EXIT_AFTER_MEASURE) {
            updateBenchmark();
        }
        frames++;
    }

    private void runEmptyLoop() {
        int acc = benchmarkIntSink;
        for (int i = 0; i < SPRITES; i++) {
            acc += i;
        }
        benchmarkIntSink = acc;
    }

    private void runArrayReadLoop() {
        float acc = benchmarkFloatSink;
        float[] xs = spriteX;
        float[] ys = spriteY;
        for (int i = 0; i < SPRITES; i++) {
            acc += xs[i] * 0.25f + ys[i] * 0.75f;
        }
        benchmarkFloatSink = acc;
    }

    private void runManualVertexWriteLoop() {
        float[] vertices = manualVertices;
        float[] xs = spriteX;
        float[] ys = spriteY;
        float color = 1;
        int idx = 0;
        for (int i = 0; i < SPRITES; i++) {
            float x = xs[i];
            float y = ys[i];
            float x2 = x + 32;
            float y2 = y + 32;

            vertices[idx] = x;
            vertices[idx + 1] = y;
            vertices[idx + 2] = color;
            vertices[idx + 3] = 0;
            vertices[idx + 4] = 1;

            vertices[idx + 5] = x;
            vertices[idx + 6] = y2;
            vertices[idx + 7] = color;
            vertices[idx + 8] = 0;
            vertices[idx + 9] = 0;

            vertices[idx + 10] = x2;
            vertices[idx + 11] = y2;
            vertices[idx + 12] = color;
            vertices[idx + 13] = 1;
            vertices[idx + 14] = 0;

            vertices[idx + 15] = x2;
            vertices[idx + 16] = y;
            vertices[idx + 17] = color;
            vertices[idx + 18] = 1;
            vertices[idx + 19] = 1;

            idx += 20;
        }
        benchmarkFloatSink = vertices[idx - 20];
    }

    private void drawSprite(int i, float angleInc) {
        if (benchmarkMode == MODE_DEFAULT) {
            if (angleInc != 0)
                sprites[i].rotate(angleInc);
            if (scale != 1)
                sprites[i].setScale(scale);
            sprites[i].draw(spriteBatch);
        }
        else if (benchmarkMode == MODE_DIRECT_SPRITE_GETTERS) {
            Sprite sprite = sprites[i];
            if (angleInc != 0)
                sprite.rotate(angleInc);
            if (scale != 1)
                sprite.setScale(scale);
            spriteBatch.draw(texture, sprite.getX(), sprite.getY(), sprite.getOriginX(), sprite.getOriginY(),
                    sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation(),
                    0, 0, 32, 32, false, false);
        }
        else if (benchmarkMode == MODE_DIRECT_ARRAY_STATE) {
            float rotation = spriteRotation[i] + angleInc;
            spriteRotation[i] = rotation;
            spriteBatch.draw(texture, spriteX[i], spriteY[i], 16, 16, 32, 32, scale, scale, rotation,
                    0, 0, 32, 32, false, false);
        }
        else if (benchmarkMode == MODE_PRECOMPUTED_ARRAYCOPY) {
            setSharedVertices(spriteX[i] + 16, spriteY[i] + 16);
            spriteBatch.draw(texture, sharedVertices, 0, 20);
        }
        else {
            spriteBatch.draw(texture, spriteX[i], spriteY[i], 32, 32);
        }
    }

    private void prepareSharedTransform(float angleInc) {
        sharedRotation += angleInc;
        float fx = -16 * scale;
        float fy = -16 * scale;
        float fx2 = 16 * scale;
        float fy2 = 16 * scale;
        float cos = MathUtils.cosDeg(sharedRotation);
        float sin = MathUtils.sinDeg(sharedRotation);
        sharedX1 = cos * fx - sin * fy;
        sharedY1 = sin * fx + cos * fy;
        sharedX2 = cos * fx - sin * fy2;
        sharedY2 = sin * fx + cos * fy2;
        sharedX3 = cos * fx2 - sin * fy2;
        sharedY3 = sin * fx2 + cos * fy2;
        sharedX4 = sharedX1 + (sharedX3 - sharedX2);
        sharedY4 = sharedY3 - (sharedY2 - sharedY1);
        sharedU = 0;
        sharedV = 32.0f / texture.getHeight();
        sharedU2 = 32.0f / texture.getWidth();
        sharedV2 = 0;
        sharedColor = spriteBatch.getPackedColor();
    }

    private void setSharedVertices(float centerX, float centerY) {
        sharedVertices[0] = centerX + sharedX1;
        sharedVertices[1] = centerY + sharedY1;
        sharedVertices[2] = sharedColor;
        sharedVertices[3] = sharedU;
        sharedVertices[4] = sharedV;
        sharedVertices[5] = centerX + sharedX2;
        sharedVertices[6] = centerY + sharedY2;
        sharedVertices[7] = sharedColor;
        sharedVertices[8] = sharedU;
        sharedVertices[9] = sharedV2;
        sharedVertices[10] = centerX + sharedX3;
        sharedVertices[11] = centerY + sharedY3;
        sharedVertices[12] = sharedColor;
        sharedVertices[13] = sharedU2;
        sharedVertices[14] = sharedV2;
        sharedVertices[15] = centerX + sharedX4;
        sharedVertices[16] = centerY + sharedY4;
        sharedVertices[17] = sharedColor;
        sharedVertices[18] = sharedU2;
        sharedVertices[19] = sharedV;
    }

    private void updateBenchmark() {
        long now = TimeUtils.nanoTime();
        if (!measuringMode && now - modeStartTime >= BENCHMARK_WARMUP_SECONDS * 1000000000L) {
            measuringMode = true;
            measuredFrames = 0;
            measureStartTime = now;
        }
        if (measuringMode) {
            measuredFrames++;
            if (now - measureStartTime >= BENCHMARK_MEASURE_SECONDS * 1000000000L) {
                float fps = measuredFrames * 1000000000.0f / (now - measureStartTime);
                benchmarkResults.append(benchmarkModeName(benchmarkMode)).append(": ").append(fps).append('\n');
                if (BENCHMARK_SWEEP && benchmarkModeIndex + 1 < BENCHMARK_MODES.length) {
                    startBenchmarkMode(benchmarkModeIndex + 1);
                }
                else {
                    Gdx.files.local("spritebatch-benchmark.txt").writeString(benchmarkResults.toString(), false);
                    Gdx.app.exit();
                }
            }
        }
    }

    private void startBenchmarkMode(int mode) {
        if (BENCHMARK_SWEEP) {
            benchmarkModeIndex = mode;
            benchmarkMode = BENCHMARK_MODES[mode];
        }
        else {
            benchmarkModeIndex = 0;
            benchmarkMode = mode;
        }
        modeStartTime = TimeUtils.nanoTime();
        measureStartTime = 0;
        measuredFrames = 0;
        measuringMode = false;
        startTime = modeStartTime;
        frames = 0;
        scale = 1;
        SCALE_SPEED = -1;
        sharedRotation = 0;
        for (int i = 0; i < SPRITES; i++) {
            spriteRotation[i] = 0;
            if (sprites[i] != null) {
                sprites[i].setRotation(0);
                sprites[i].setScale(1);
            }
        }
        Gdx.app.log(TAG, "Benchmark mode: " + benchmarkModeName(benchmarkMode));
    }

    private String benchmarkModeName(int mode) {
        if (mode == MODE_DEFAULT)
            return "default";
        if (mode == MODE_DIRECT_SPRITE_GETTERS)
            return "direct_sprite_getters";
        if (mode == MODE_DIRECT_ARRAY_STATE)
            return "direct_array_state";
        if (mode == MODE_SIMPLE_DIRECT)
            return "simple_direct";
        if (mode == MODE_PRECOMPUTED_ARRAYCOPY)
            return "precomputed_arraycopy";
        if (mode == MODE_FRAME_ONLY)
            return "frame_only";
        if (mode == MODE_CLEAR_ONLY)
            return "clear_only";
        if (mode == MODE_EMPTY_LOOP)
            return "empty_loop";
        if (mode == MODE_ARRAY_READ_LOOP)
            return "array_read_loop";
        if (mode == MODE_MANUAL_VERTEX_WRITE)
            return "manual_vertex_write";
        if (mode == MODE_BEGIN_END_ONLY)
            return "begin_end_only";
        return "unknown";
    }

    @Override
    public void dispose() {
        Gdx.app.log(TAG, "[" + this.hashCode() + "] dispose() called.");
        if (spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
