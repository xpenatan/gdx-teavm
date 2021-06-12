package com.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.tests.dragome.examples.AnimationTest;
import com.badlogic.gdx.tests.dragome.examples.DragomeLossyPremultipliedAlphaTest;
import com.badlogic.gdx.tests.dragome.examples.GearsDemo;
import com.badlogic.gdx.tests.dragome.examples.UITest;

public class Main {

	static int BULLET_TEST = 1;
	static int BULLET_COLLECTION_TEST = 2;
	static int GEARS_TEST = 3;
	static int ANIMATION_TEST = 4;
	static int UI_TEST = 5;
	static int FREETYPE_TEST = 6;

	public static void main(String[] args)
	{
		ApplicationListener appTest = null;
		int i = UI_TEST;
		if(i == GEARS_TEST)
			appTest = new GearsDemo();
//		else if(i == BULLET_TEST)
//			appTest = new BulletTest();
//		else if(i == BULLET_COLLECTION_TEST)
//			appTest = new BulletTestCollection();
		else if(i == ANIMATION_TEST)
			appTest = new AnimationTest();
		else if(i == UI_TEST)
			appTest = new UITest();
//		else if(i == FREETYPE_TEST)
//			appTest = new FreeTypePackTest();

		if(appTest != null)
			new LwjglApplication(appTest);
	}
}
