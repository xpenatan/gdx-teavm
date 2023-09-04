package com.github.xpenatan.imgui.example.tests;


import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tests.gles3.GL30Texture3DTest;

public class Main {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1444;
        config.height = 800;
        config.title = "gdx-tests";
        config.useGL30 = true;
//        new LwjglApplication(new TeaVMTestWrapper(), config);
        new LwjglApplication(new GL30Texture3DTest(), config);
    }
}