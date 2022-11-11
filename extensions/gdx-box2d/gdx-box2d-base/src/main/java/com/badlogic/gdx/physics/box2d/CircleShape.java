package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2CircleShape;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class CircleShape extends Shape<b2CircleShape> {

    private final Vector2 position = new Vector2();

    public CircleShape() {
        b2Shape = new b2CircleShape();
    }

    CircleShape(long addr) {
        b2Shape = new b2CircleShape(false);
        b2Shape.setPointer(addr);
    }

    public Type getType() {
        return Type.Circle;
    }

    public Vector2 getPosition() {
        b2Vec2 b2Vec2 = b2Shape.m_p();
        position.x = b2Vec2.x();
        position.y = b2Vec2.y();
        return position;
    }
}