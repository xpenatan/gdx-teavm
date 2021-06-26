package com.github.xpenatan.gdx.html5.generator;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.xpenatan.gdx.html5.generator.core.view.MainApplication;

public class Main {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Gdx-Html5-Generator";
        new LwjglApplication(new MainApplication(), config);
    }
}