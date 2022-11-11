package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2EdgeShape;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class EdgeShape extends Shape<b2EdgeShape> {

    public EdgeShape() {
        b2Shape = new b2EdgeShape();
    }

    EdgeShape(long addr) {
        b2Shape = new b2EdgeShape(false);
        b2Shape.setPointer(addr);
    }

    public void set(Vector2 v1, Vector2 v2) {
        set(v1.x, v1.y, v2.x, v2.y);
    }

    public void set(float v1X, float v1Y, float v2X, float v2Y) {
        b2Shape.set(v1X, v1Y, v2X, v2Y);
    }

    public void getVertex0(Vector2 vec) {
        b2Vec2 b2Vec2 = b2Shape.m_vertex0();
        vec.set(b2Vec2.x(), b2Vec2.y());
    }

    public void setVertex0(Vector2 vec) {
        b2Vec2.TMP_01.Set(vec.x, vec.y);
        b2Shape.m_vertex0(b2Vec2.TMP_01);
    }

    public void setVertex0(float x, float y) {
        b2Vec2.TMP_01.Set(x, y);
        b2Shape.m_vertex0(b2Vec2.TMP_01);
    }

    public void getVertex1(Vector2 vec) {
        b2Vec2 b2Vec2 = b2Shape.m_vertex1();
        vec.set(b2Vec2.x(), b2Vec2.y());
    }

    public void setVertex1(Vector2 vec) {
        b2Vec2.TMP_01.Set(vec.x, vec.y);
        b2Shape.m_vertex1(b2Vec2.TMP_01);
    }

    public void setVertex1(float x, float y) {
        b2Vec2.TMP_01.Set(x, y);
        b2Shape.m_vertex1(b2Vec2.TMP_01);
    }

    public void getVertex2(Vector2 vec) {
        b2Vec2 b2Vec2 = b2Shape.m_vertex2();
        vec.set(b2Vec2.x(), b2Vec2.y());
    }

    public void setVertex2(Vector2 vec) {
        b2Vec2.TMP_01.Set(vec.x, vec.y);
        b2Shape.m_vertex2(b2Vec2.TMP_01);
    }

    public void setVertex2(float x, float y) {
        b2Vec2.TMP_01.Set(x, y);
        b2Shape.m_vertex2(b2Vec2.TMP_01);
    }

    public void getVertex3(Vector2 vec) {
        b2Vec2 b2Vec2 = b2Shape.m_vertex3();
        vec.set(b2Vec2.x(), b2Vec2.y());
    }

    public void setVertex3(Vector2 vec) {
        b2Vec2.TMP_01.Set(vec.x, vec.y);
        b2Shape.m_vertex3(b2Vec2.TMP_01);
    }

    public void setVertex3(float x, float y) {
        b2Vec2.TMP_01.Set(x, y);
        b2Shape.m_vertex3(b2Vec2.TMP_01);
    }
}