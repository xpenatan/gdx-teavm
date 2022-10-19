package com.badlogic.gdx.physics.box2d;

public interface QueryCallback {
    public boolean reportFixture(Fixture fixture);
}