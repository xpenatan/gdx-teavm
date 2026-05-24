package com.github.xpenatan.gdx.teavm.benchmarks.cases;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkBackend;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkCase;
import com.github.xpenatan.gdx.teavm.benchmarks.BenchmarkConfig;
import java.util.Random;

public class SpriteBatchDirectArrayStateBenchmark implements BenchmarkCase {
    private static final int SPRITE_WIDTH = 32;
    private static final int SPRITE_HEIGHT = 32;
    private static final float ROTATION_SPEED = 20f;

    private BenchmarkConfig config;
    private ScreenViewport viewport;
    private SpriteBatch spriteBatch;
    private Texture texture;
    private float[] spriteX;
    private float[] spriteY;
    private float[] spriteRotation;
    private float scale = 1f;
    private float scaleSpeed = -1f;
    private boolean generatedSprites;

    @Override
    public String getName() {
        return "spritebatch_direct_array_state";
    }

    @Override
    public void create(BenchmarkBackend backend, BenchmarkConfig config) {
        this.config = config;
        viewport = new ScreenViewport();
        spriteBatch = (SpriteBatch)backend.createSpriteBatch(config.sprites);
        texture = backend.createTexture("data/badlogicsmall.jpg");
        spriteX = new float[config.sprites];
        spriteY = new float[config.sprites];
        spriteRotation = new float[config.sprites];
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
        float drawScale = config.scale ? scale : 1f;

        spriteBatch.begin();
        for(int i = 0; i < spriteX.length; i++) {
            float rotation = spriteRotation[i] + angleInc;
            spriteRotation[i] = rotation;
            spriteBatch.draw(texture, spriteX[i], spriteY[i], SPRITE_WIDTH * 0.5f, SPRITE_HEIGHT * 0.5f,
                    SPRITE_WIDTH, SPRITE_HEIGHT, drawScale, drawScale, rotation,
                    0, 0, SPRITE_WIDTH, SPRITE_HEIGHT, false, false);
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

    private void generateSprites(int screenWidth, int screenHeight) {
        Random random = new Random(0x51f15e2dL);
        for(int i = 0; i < spriteX.length; i++) {
            int x = random.nextInt(Math.max(1, screenWidth));
            int y = random.nextInt(Math.max(1, screenHeight));
            spriteX[i] = x - SPRITE_WIDTH * 0.5f;
            spriteY[i] = y - SPRITE_HEIGHT * 0.5f;
            spriteRotation[i] = 0f;
        }
        generatedSprites = true;
    }
}
