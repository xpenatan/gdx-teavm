package com.github.xpenatan.gdx.examples.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.xpenatan.gdx.examples.tests.FilesTest;
import com.github.xpenatan.gdx.examples.tests.GLTFQuickStartExample;
import com.github.xpenatan.gdx.examples.tests.GearsDemo;
import com.github.xpenatan.gdx.examples.tests.LoadingTest;
import com.github.xpenatan.gdx.examples.tests.ReadPixelsTest;
import com.github.xpenatan.gdx.examples.tests.ReflectionTest;
import com.github.xpenatan.gdx.examples.tests.TeaVMInputTest;

public class Main {

    public static void main(String[] args) {
//        new LwjglApplication(new GearsDemo());
//        new LwjglApplication(new TeaVMInputTest());
//        new LwjglApplication(new ReflectionTest());
//        new LwjglApplication(new ReadPixelsTest());
//        new LwjglApplication(new FilesTest());
//        new LwjglApplication(new LoadingTest());
        new LwjglApplication(new GLTFQuickStartExample());
    }
}
