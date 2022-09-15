package com.github.xpenatan.gdx.examples.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.xpenatan.gdx.examples.bullet.BulletTest;

public class MainBullet {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = false;
        config.foregroundFPS = 500;
        new LwjglApplication(new BulletTest(), config);
    }
}
