package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class IOSSpriteBatchTest extends SpriteBatchTest {
    @Override
    public void create() {
        spriteBatch = new com.badlogic.gdx.graphics.g2d.SpriteBatch(1000);

        FileHandle file = Gdx.files.internal("data/badlogicsmall.jpg");
        byte[] imageBytes = file.readBytes();
        if(imageBytes.length < 2 || (imageBytes[0] & 0xff) != 0xff || (imageBytes[1] & 0xff) != 0xd8) {
            throw new GdxRuntimeException("Invalid sprite batch image bytes: " + file.path());
        }

        Pixmap pixmap = new Pixmap(imageBytes, 0, imageBytes.length);
        texture = new Texture(32, 32, Pixmap.Format.RGB565);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        texture.draw(pixmap, 0, 0);
        pixmap.dispose();

        pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 0, 0.5f);
        pixmap.fill();
        texture2 = new Texture(pixmap);
        pixmap.dispose();

        for(int i = 0; i < sprites.length; i += 6) {
            sprites[i] = (int)(Math.random() * (Gdx.graphics.getWidth() - 32));
            sprites[i + 1] = (int)(Math.random() * (Gdx.graphics.getHeight() - 32));
            sprites[i + 2] = 0;
            sprites[i + 3] = 0;
            sprites[i + 4] = 32;
            sprites[i + 5] = 32;
            sprites2[i] = (int)(Math.random() * (Gdx.graphics.getWidth() - 32));
            sprites2[i + 1] = (int)(Math.random() * (Gdx.graphics.getHeight() - 32));
            sprites2[i + 2] = 0;
            sprites2[i + 3] = 0;
            sprites2[i + 4] = 32;
            sprites2[i + 5] = 32;
        }

        for(int i = 0; i < SPRITES * 2; i++) {
            int x = (int)(Math.random() * (Gdx.graphics.getWidth() - 32));
            int y = (int)(Math.random() * (Gdx.graphics.getHeight() - 32));

            if(i >= SPRITES) {
                sprites3[i] = new Sprite(texture2, 32, 32);
            }
            else {
                sprites3[i] = new Sprite(texture, 32, 32);
            }
            sprites3[i].setPosition(x, y);
            sprites3[i].setOrigin(16, 16);
        }

        Gdx.input.setInputProcessor(this);
    }
}
