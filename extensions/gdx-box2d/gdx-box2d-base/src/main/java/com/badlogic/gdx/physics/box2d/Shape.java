package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.physics.box2d.cpp.b2Shape;

public class Shape<T extends b2Shape> {

    public enum Type {
        Circle, Edge, Polygon, Chain,
    }

    public T b2Shape;

    public void dispose() {
        b2Shape.dispose();
    }

    public float getRadius() {
        return b2Shape.m_radius();
    }

    public void setRadius(float radius) {
        b2Shape.m_radius(radius);
    }
}