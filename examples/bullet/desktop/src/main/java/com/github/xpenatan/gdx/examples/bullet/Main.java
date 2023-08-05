package com.github.xpenatan.gdx.examples.bullet;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = false;
        config.foregroundFPS = 500;
        new LwjglApplication(new BulletGame(), config);
    }
}
