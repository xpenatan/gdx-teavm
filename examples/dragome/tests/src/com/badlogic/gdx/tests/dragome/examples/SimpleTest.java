package com.badlogic.gdx.tests.dragome.examples;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class SimpleTest implements ApplicationListener
{
	Random random;
	
	int i = 0;
	
	float r, g, b;
	
	@Override
	public void create() {
		System.out.println("Create");
		random = new Random();
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("Resize: " + width + " : " + height);
	}

	@Override
	public void render() {
		
		if(i > 100)
		{
			r = random.nextFloat();
			g = random.nextFloat();
			b = random.nextFloat();
			i = 0;
		}
		Gdx.gl.glClearColor(r, g, b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		i++;
	}

	@Override
	public void pause() {
		System.out.println("Pause");
	}

	@Override
	public void resume() {
		System.out.println("Resume");
	}

	@Override
	public void dispose() {
		System.out.println("Disposed");
	}

}
