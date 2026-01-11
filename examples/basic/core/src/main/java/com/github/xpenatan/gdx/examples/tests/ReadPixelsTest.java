package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.BufferUtils;
import java.nio.ByteBuffer;

/**
 * @author xpenatan
 */
public class ReadPixelsTest implements ApplicationListener {

    SpriteBatch batch;
    Texture badlogic;
    Color color = new Color();

    OrthographicCamera camera;

    @Override
    public void create() {
        Gdx.input.setCatchKey(Input.Keys.F1, true);

        batch = new SpriteBatch();
        badlogic = new Texture(Gdx.files.internal("custom/badlogic.jpg"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
    }

    public void readPixel(int x, int y) {
        float windowH = Gdx.graphics.getHeight();
        int x1 = x;
        int y1 = y;
        y1 = (int)(windowH - y1);

        int w = 1;
        int h = 1;

        int sizeBytes = w * h * 4;
        final ByteBuffer pixelBuffer = BufferUtils.newByteBuffer(sizeBytes);

        Gdx.gl.glReadPixels(x1, y1, 1, 1, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer);
        byte r = pixelBuffer.get(0);
        byte g = pixelBuffer.get(1);
        byte b = pixelBuffer.get(2);
        byte a = pixelBuffer.get(3);
        int rr = Byte.toUnsignedInt(r);
        int gg = Byte.toUnsignedInt(g);
        int bb = Byte.toUnsignedInt(b);
        int aa = Byte.toUnsignedInt(a);
        float red = rr / 255f;
        float green = gg / 255f;
        float blue = bb / 255f;
        float alpha = aa / 255f;
        color.set(red, green, blue, alpha);
    }

    @Override
    public void render() {
        camera.update();
        Gdx.gl.glClearColor(color.r, color.g, color.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(badlogic, 0, 0);
        batch.end();

        readPixel(Gdx.input.getX(), Gdx.input.getY());
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }
}
