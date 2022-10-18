package com.badlogic.gdx.physics.box2d;

public class FixtureDef {
    public Shape shape;
    public float friction = 0.2f;
    public float restitution = 0;
    public float density = 0;
    public boolean isSensor = false;
    /**
     * Contact filtering data.
     **/
    public final Filter filter = new Filter();
}