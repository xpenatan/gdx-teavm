package com.github.xpenatan.gdx.examples.bullet;

import com.badlogic.gdx.Game;

public class BulletGame extends Game {

    @Override
    public void create() {
        setScreen(new BulletInitScreen(this));
    }
}
