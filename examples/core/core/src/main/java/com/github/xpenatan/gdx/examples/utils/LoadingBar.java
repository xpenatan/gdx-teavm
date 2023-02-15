package com.github.xpenatan.gdx.examples.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * From https://github.com/Matsemann/libgdx-loading-screen
 */
public class LoadingBar extends Actor {

    Animation<TextureRegion> animation;
    TextureRegion reg;
    float stateTime;

    public LoadingBar(Animation<TextureRegion> animation) {
        this.animation = animation;
        reg = animation.getKeyFrame(0);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
        reg = animation.getKeyFrame(stateTime);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(reg, getX(), getY());
    }
}