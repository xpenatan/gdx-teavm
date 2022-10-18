package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2ChainShape;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class ChainShape extends Shape<b2ChainShape> {

    public ChainShape() {
        b2Shape = new b2ChainShape();
    }

    ChainShape(long addr) {
        b2Shape = new b2ChainShape(false);
        b2Shape.setPointer(addr);
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