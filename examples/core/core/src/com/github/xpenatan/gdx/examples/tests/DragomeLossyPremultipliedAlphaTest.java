package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FileTextureData;

/**
 * This tests is used to illustrate how backing the Pixmap in GWT with a Canvas can cause problems.
 * @author barkholt
 *
 */
public class DragomeLossyPremultipliedAlphaTest implements ApplicationListener {
	private SpriteBatch batch;
	private Texture goodTexture;
	private Texture badTexture;

	@Override
	public void create () {
		batch = new SpriteBatch();
		// Load a texture with premultiplied alpha
		FileTextureData data = new FileTextureData(Gdx.files.internal("data/premultiplied_alpha_test.png"), null, null, false);
		goodTexture = new Texture(data);

		// Load the texture again. But this time, force the GWT implementation of Pixmap to move to a Canvas representation of the image
		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/premultiplied_alpha_test.png"));
		pixmap.getPixel(0, 0);
		FileTextureData data1 = new FileTextureData(null, pixmap, null, false);
		badTexture = new Texture(data1);
	}

	@Override
	public void render () {
		 Gdx.gl.glClearColor(0, 0, 0, 1);
       Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

       batch.begin();
       batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
       batch.draw(badTexture,
               0,
               Gdx.graphics.getHeight(),
               Gdx.graphics.getWidth() * 0.5f,
               -Gdx.graphics.getHeight());

       batch.draw(goodTexture,
               Gdx.graphics.getWidth() * 0.5f,
               Gdx.graphics.getHeight(),
               Gdx.graphics.getWidth() * 0.5f,
               -Gdx.graphics.getHeight());

       batch.end();
	}

	public boolean needsGL20 () {
		return false;
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
