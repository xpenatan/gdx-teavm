package com.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.tests.dragome.examples.AnimationTest;
import com.badlogic.gdx.tests.dragome.examples.BulletTest;
import com.badlogic.gdx.tests.dragome.examples.GearsDemo;
import com.badlogic.gdx.tests.dragome.examples.UITest;
import com.badlogic.gdx.tests.dragome.examples.bullet.BulletTestCollection;

public class Main {

	static int BULLET_TEST = 1;
	static int BULLET_COLLECTION = 2;
	static int GEARS = 3;
	static int ANIMATION_TEST = 4;
	static int UITEST = 5;

	public static void main(String[] args)
	{
		ApplicationListener appTest = null;
		int i = GEARS;
		if(i == BULLET_TEST)
			appTest = new BulletTest();
		else if(i == BULLET_COLLECTION)
			appTest = new BulletTestCollection();
		else if(i == GEARS)
			appTest = new GearsDemo();
		else if(i == ANIMATION_TEST)
			appTest = new AnimationTest();
		else if(i == UITEST)
			appTest = new UITest();

		if(appTest != null)
			new LwjglApplication(appTest);
	}
}
