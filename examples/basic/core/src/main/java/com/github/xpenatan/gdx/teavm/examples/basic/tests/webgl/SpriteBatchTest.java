package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Random;

public class SpriteBatchTest extends ApplicationAdapter {
    private static final String LOG_TAG = "SpriteBatchTest";
    private static final String TEXTURE_PATH = "data/badlogicsmall.jpg";
    private static final int MAX_SPRITES = 8191;
    private static final int SPRITE_SIZE = 32;
    private static final long POSITION_SEED = 0x51F15E2DL;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final float ROTATION_SPEED = 20f;
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 1f;
    private static final float SCALE_SPEED = 0.5f;

    private SpriteBatch spriteBatch;
    private Texture texture;
    private Sprite[] sprites;
    private float scale = MAX_SCALE;
    private float scaleDirection = -1f;
    private long fpsLogStartNanos;
    private int renderedFrames;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch(MAX_SPRITES);
        texture = createTexture();
        sprites = new Sprite[MAX_SPRITES];

        for(int i = 0; i < sprites.length; i++) {
            Sprite sprite = new Sprite(texture, SPRITE_SIZE, SPRITE_SIZE);
            sprite.setOrigin(SPRITE_SIZE * 0.5f, SPRITE_SIZE * 0.5f);
            sprite.setRotation(360f * i / sprites.length);
            sprites[i] = sprite;
        }

        layoutSprites(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fpsLogStartNanos = TimeUtils.nanoTime();
        Gdx.app.log(LOG_TAG, "started sprites=" + sprites.length + " batchCapacity=" + MAX_SPRITES);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.08f, 0.08f, 0.1f, 1f);

        float delta = Gdx.graphics.getDeltaTime();
        float rotationDelta = ROTATION_SPEED * delta;
        updateScale(delta);

        spriteBatch.begin();
        for(int i = 0; i < sprites.length; i++) {
            Sprite sprite = sprites[i];
            sprite.rotate(rotationDelta);
            sprite.setScale(scale);
            sprite.draw(spriteBatch);
        }
        spriteBatch.end();

        logFps();
    }

    @Override
    public void resize(int width, int height) {
        if(spriteBatch == null || sprites == null) {
            return;
        }
        spriteBatch.getProjectionMatrix().setToOrtho2D(0f, 0f, width, height);
        layoutSprites(width, height);
        Gdx.app.log(LOG_TAG, "resized width=" + width + " height=" + height);
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

    protected Pixmap loadSpritePixmap() {
        return new Pixmap(Gdx.files.internal(TEXTURE_PATH));
    }

    private Texture createTexture() {
        Pixmap pixmap = loadSpritePixmap();
        Texture spriteTexture = new Texture(SPRITE_SIZE, SPRITE_SIZE, Format.RGB565);
        spriteTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        spriteTexture.draw(pixmap, 0, 0);
        pixmap.dispose();
        return spriteTexture;
    }

    private void updateScale(float delta) {
        scale += scaleDirection * SCALE_SPEED * delta;
        if(scale <= MIN_SCALE) {
            scale = MIN_SCALE;
            scaleDirection = 1f;
        }
        else if(scale >= MAX_SCALE) {
            scale = MAX_SCALE;
            scaleDirection = -1f;
        }
    }

    private void layoutSprites(int width, int height) {
        Random random = new Random(POSITION_SEED);
        float availableWidth = Math.max(0, width - SPRITE_SIZE);
        float availableHeight = Math.max(0, height - SPRITE_SIZE);
        for(int i = 0; i < sprites.length; i++) {
            sprites[i].setPosition(random.nextFloat() * availableWidth, random.nextFloat() * availableHeight);
        }
    }

    private void logFps() {
        renderedFrames++;
        long now = TimeUtils.nanoTime();
        long elapsed = now - fpsLogStartNanos;
        if(elapsed < NANOS_PER_SECOND) {
            return;
        }

        long fps = ((long)renderedFrames * NANOS_PER_SECOND + elapsed / 2L) / elapsed;
        Gdx.app.log(LOG_TAG, "fps=" + fps
                + " sprites=" + sprites.length
                + " renderCalls=" + spriteBatch.renderCalls
                + " maxSpritesInBatch=" + spriteBatch.maxSpritesInBatch
                + " scale=" + scale);
        renderedFrames = 0;
        fpsLogStartNanos = now;
    }
}
