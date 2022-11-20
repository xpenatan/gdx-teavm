package com.github.xpenatan.gdx.examples.desktop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.xpenatan.gdx.examples.tests.AnimationTest;
import com.github.xpenatan.gdx.examples.tests.GearsDemo;
import com.github.xpenatan.gdx.examples.tests.UITest;
import com.github.xpenatan.gdx.examples.tests.freetype.FreeTypeAtlasTest;
import com.github.xpenatan.gdx.examples.tests.reflection.ReflectionTest;

public class Main {

    static int BULLET_TEST = 1;
    static int BULLET_COLLECTION_TEST = 2;
    static int GEARS_TEST = 3;
    static int ANIMATION_TEST = 4;
    static int UI_TEST = 5;
    static int FREETYPE_TEST = 6;
    static int REFLECTION_TEST = 7;

    public static void main(String[] args) {
        ApplicationListener appTest = null;
        int i = GEARS_TEST;
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
        else if(i == FREETYPE_TEST) {
            appTest = new FreeTypeAtlasTest();
//			appTest = new FreeTypeMetricsTest();
//			appTest = new FreeTypePackTest();
//			appTest = new FreeTypeTest();
        }
        else if(i == REFLECTION_TEST)
            appTest = new ReflectionTest();

        if(appTest != null)
            new LwjglApplication(appTest);
    }
}
