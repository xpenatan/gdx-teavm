package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class IOSSpriteBatchTest extends SpriteBatchTest {
    @Override
    protected Pixmap loadSpritePixmap() {
        FileHandle file = Gdx.files.internal("data/badlogicsmall.jpg");
        byte[] imageBytes = file.readBytes();
        if(imageBytes.length < 2 || (imageBytes[0] & 0xff) != 0xff || (imageBytes[1] & 0xff) != 0xd8) {
            throw new GdxRuntimeException("Invalid sprite batch image bytes: " + file.path());
        }
        return new Pixmap(imageBytes, 0, imageBytes.length);
    }
}
