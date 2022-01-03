package com.github.xpenatan.gdx.examples.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.github.xpenatan.gdx.examples.bullet.BulletTest;

public class MainBullet {

    public static void main(String[] args) {
        new LwjglApplication(new BulletTest());
    }
}
