package com.github.xpenatan.gdx.teavm.example.basic.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.xpenatan.gdx.example.basic.tests.GLTFQuickStartExample;

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
