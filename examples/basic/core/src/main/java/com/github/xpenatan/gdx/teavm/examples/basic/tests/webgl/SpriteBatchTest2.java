package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.text.DecimalFormat;

public class SpriteBatchTest2 extends ApplicationAdapter {
    private static final String TAG = "SpriteBatchTest";
    DecimalFormat df = new DecimalFormat("000.0000000000");
    int SPRITES = 8191;

    long startTime = TimeUtils.nanoTime();
    int frames = 0;
    Texture texture;
    SpriteBatch spriteBatch;
    ScreenViewport viewport;
    Sprite[] sprites = new Sprite[SPRITES];
    float ROTATION_SPEED = 20;
    float scale = 1;
    float SCALE_SPEED = -1;

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
        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        float begin = 0;
        float end = 0;
        float draw1 = 0;
        float drawText = 0;

        long start = TimeUtils.nanoTime();
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

        start = TimeUtils.nanoTime();
        for (int i = 0; i < SPRITES; i++) {
            if (angleInc != 0)
                sprites[i].rotate(angleInc);
            if (scale != 1)
                sprites[i].setScale(scale);
            sprites[i].draw(spriteBatch);
        }
        draw1 = (TimeUtils.nanoTime() - start) / 1000000000.0f;

        start = TimeUtils.nanoTime();
        drawText = (TimeUtils.nanoTime() - start) / 1000000000.0f;

        start = TimeUtils.nanoTime();
        spriteBatch.end();
        end = (TimeUtils.nanoTime() - start) / 1000000000.0f;

        if (TimeUtils.nanoTime() - startTime > 1000000000) {
            Gdx.app.log(TAG,
                    "fps: " + frames + ", render calls: " + spriteBatch.renderCalls + ", begin: " + df.format(begin)
                            + ", " + df.format(draw1) + ", " + df.format(drawText) + ", end: " + df.format(end));
            frames = 0;
            startTime = TimeUtils.nanoTime();
        }
        frames++;
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
