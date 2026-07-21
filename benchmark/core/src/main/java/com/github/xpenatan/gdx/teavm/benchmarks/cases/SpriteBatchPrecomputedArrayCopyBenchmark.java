package com.github.xpenatan.gdx.teavm.benchmarks.cases;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkCase;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;
import java.util.Random;

public class SpriteBatchPrecomputedArrayCopyBenchmark implements BenchmarkCase {
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final float ROTATION_SPEED = 20f;

    private BenchmarkConfig config;
    private ScreenViewport viewport;
    private SpriteBatch spriteBatch;
    private Texture texture;
    private float[] spriteX;
    private float[] spriteY;
    private final float[] sharedVertices = new float[20];
    private float scale = 1f;
    private float scaleSpeed = -1f;
    private float sharedRotation;
    private float sharedX1;
    private float sharedY1;
    private float sharedX2;
    private float sharedY2;
    private float sharedX3;
    private float sharedY3;
    private float sharedX4;
    private float sharedY4;
    private float sharedU;
    private float sharedV;
    private float sharedU2;
    private float sharedV2;
    private float sharedColor;
    private boolean generatedSprites;

    @Override
    public String getName() {
        return "spritebatch_precomputed_arraycopy";
    }

    @Override
    public void create(BenchmarkBackend backend, BenchmarkConfig config) {
        this.config = config;
        viewport = new ScreenViewport();
        spriteBatch = (SpriteBatch)backend.createSpriteBatch(config.spriteBatchSize());
        texture = backend.createTexture("data/badlogicsmall.jpg");
        spriteX = new float[config.sprites];
        spriteY = new float[config.sprites];
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        generateSprites(width, height);
    }

    @Override
    public void render() {
        if(!generatedSprites) {
            generateSprites(Math.max(1, Gdx.graphics.getWidth()), Math.max(1, Gdx.graphics.getHeight()));
        }
        if(config.clear) {
            ScreenUtils.clear(0, 0, 0, 1);
        }
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        float angleInc = config.rotate ? ROTATION_SPEED * Gdx.graphics.getDeltaTime() : 0f;
        updateScale();

        spriteBatch.begin();
        prepareSharedTransform(angleInc);
        for(int i = 0; i < spriteX.length; i++) {
            setSharedVertices(spriteX[i] + SPRITE_WIDTH * 0.5f, spriteY[i] + SPRITE_HEIGHT * 0.5f);
            spriteBatch.draw(texture, sharedVertices, 0, 20);
        }
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        if(spriteBatch != null) {
            spriteBatch.dispose();
            spriteBatch = null;
        }
        if(texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    private void updateScale() {
        if(!config.scale) {
            scale = 1f;
            return;
        }
        scale += scaleSpeed * Gdx.graphics.getDeltaTime();
        if(scale < 0.5f) {
            scale = 0.5f;
            scaleSpeed = 1f;
        }
        else if(scale > 1f) {
            scale = 1f;
            scaleSpeed = -1f;
        }
    }

    private void prepareSharedTransform(float angleInc) {
        sharedRotation += angleInc;
        float drawScale = config.scale ? scale : 1f;
        float fx = -SPRITE_WIDTH * 0.5f * drawScale;
        float fy = -SPRITE_HEIGHT * 0.5f * drawScale;
        float fx2 = SPRITE_WIDTH * 0.5f * drawScale;
        float fy2 = SPRITE_HEIGHT * 0.5f * drawScale;
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
        sharedU = 0f;
        sharedV = (float)SPRITE_HEIGHT / texture.getHeight();
        sharedU2 = (float)SPRITE_WIDTH / texture.getWidth();
        sharedV2 = 0f;
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

    private void generateSprites(int screenWidth, int screenHeight) {
        Random random = new Random(0x51f15e2dL);
        for(int i = 0; i < spriteX.length; i++) {
            int x = random.nextInt(Math.max(1, screenWidth));
            int y = random.nextInt(Math.max(1, screenHeight));
            spriteX[i] = x - SPRITE_WIDTH * 0.5f;
            spriteY[i] = y - SPRITE_HEIGHT * 0.5f;
        }
        generatedSprites = true;
    }
}
