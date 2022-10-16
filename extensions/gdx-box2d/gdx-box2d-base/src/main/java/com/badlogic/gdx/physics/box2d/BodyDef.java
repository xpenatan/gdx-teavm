package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;

public class BodyDef {

    public enum BodyType {
        StaticBody(0), KinematicBody(1), DynamicBody(2);

        private int value;

        private BodyType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public BodyType type = BodyType.StaticBody;
    public final Vector2 position = new Vector2();
    public float angle = 0;
    public final Vector2 linearVelocity = new Vector2();
    public float angularVelocity = 0;
    public float linearDamping = 0;
    public float angularDamping = 0;
    public boolean allowSleep = true;
    public boolean awake = true;
    public boolean fixedRotation = false;
    public boolean bullet = false;
    public boolean active = true;
    public float gravityScale = 1;
}