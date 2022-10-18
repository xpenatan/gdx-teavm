package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2PolygonShape;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class PolygonShape extends Shape<b2PolygonShape> {

    public PolygonShape() {
        b2Shape = new b2PolygonShape();
    }

    PolygonShape(long addr) {
        b2Shape = new b2PolygonShape(false);
        b2Shape.setPointer(addr);
    }

    public void setAsBox(float hx, float hy) {
        b2Shape.SetAsBox(hx, hy);
    }

    public int getVertexCount () {
        return b2Shape.GetVertexCount();
    }

    public void getVertex (int index, Vector2 vertex) {
        b2Vec2 b2Vec2 = b2Shape.GetVertex(index);
        vertex.x = b2Vec2.x();
        vertex.y = b2Vec2.y();
    }
}