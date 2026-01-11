package com.github.xpenatan.gdx.teavm.example.freetype;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * @author xpenatan
 */
public class FreetypeDemo implements ApplicationListener {
    BitmapFont font;
    SpriteBatch batch;
    BitmapFont ftFont;

    @Override
    public void create() {
        boolean flip = false;
        batch = new SpriteBatch();
        if(flip) {
            OrthographicCamera cam = new OrthographicCamera();
            cam.setToOrtho(flip);
            cam.update();
            batch.setProjectionMatrix(cam.combined);
        }
        font = new BitmapFont(Gdx.files.internal("data/lsans-15.fnt"), flip);
        FileHandle fontFile = Gdx.files.internal("data/lsans.ttf");

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 15;
        parameter.flip = flip;
        parameter.genMipMaps = true;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = -4;
        // parameter.shadowColor = Color.GREEN;
        // parameter.borderWidth = 1f;
        // parameter.borderColor = Color.PURPLE;

        FreeTypeFontGenerator.FreeTypeBitmapFontData fontData = generator.generateData(parameter);
        ftFont = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        batch.begin();
        font.setColor(Color.RED);
        font.draw(batch, "This is a test 1\nAnd another line\n()����$%&/!12390#", 100, 172);
        ftFont.draw(batch, "This is a test 2\nAnd another line\n()����$%&/!12390#", 100, 102);
// batch.disableBlending();
        batch.draw(ftFont.getRegion(), 350, 0);
// batch.enableBlending();
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
