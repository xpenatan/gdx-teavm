package com.github.xpenatan.gdx.examples.bullet;

import com.badlogic.gdx.ScreenAdapter;
import bullet.Bullet;

public class BulletInitScreen extends ScreenAdapter {

    private BulletGame game;

    private boolean bulletInit = false;

    public BulletInitScreen(BulletGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        Bullet.init(() -> bulletInit = true);
    }

    @Override
    public void render(float delta) {
        if(bulletInit) {
            bulletInit = false;
            game.setScreen(new BulletTestScreen());
        }
    }
}
