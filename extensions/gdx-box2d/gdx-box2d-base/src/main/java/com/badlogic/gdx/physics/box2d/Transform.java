package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2Rot;
import com.badlogic.gdx.physics.box2d.cpp.b2Transform;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class Transform {
    public static final int POS_X = 0;
    public static final int POS_Y = 1;
    public static final int COS = 2;
    public static final int SIN = 3;

    public float[] vals = new float[4];

    private b2Transform b2Transform;

    private Vector2 position = new Vector2();
    private Vector2 orientation = new Vector2();

    public Transform() {
        b2Transform = new b2Transform();
    }

    public Transform(boolean cMemoryOwn) {
        b2Transform = new b2Transform(cMemoryOwn);
    }

    public Transform(Vector2 position, float angle) {
        setPosition(position);
        setRotation(angle);
    }

    public Transform(Vector2 position, Vector2 orientation) {
        setPosition(position);
        setOrientation(orientation);
    }

    public void setAddr(long addr) {
        b2Transform.setPointer(addr);
        if(addr != 0) {
            b2Rot b2Rot = b2Transform.q();
            b2Vec2 b2Vec2 = b2Transform.p();
            float c = b2Rot.c();
            float s = b2Rot.s();
            float x = b2Vec2.x();
            float y = b2Vec2.y();
            vals[POS_X] = x;
            vals[POS_Y] = y;
            vals[COS] = c;
            vals[SIN] = s;
        }
    }

    public Vector2 mul(Vector2 v) {
        b2Rot b2Rot = b2Transform.q();
        b2Vec2 b2Vec2 = b2Transform.p();
        float c = b2Rot.c();
        float s = b2Rot.s();
        float x = b2Vec2.x() + c * v.x + -s * v.y;
        float y = b2Vec2.y() + s * v.x + c * v.y;
        v.x = x;
        v.y = y;
        return v;
    }

    public Vector2 getPosition() {
        b2Vec2 b2Vec2 = b2Transform.p();
        float x = b2Vec2.x();
        float y = b2Vec2.y();
        vals[POS_X] = x;
        vals[POS_Y] = y;
        return position.set(x, y);
    }

    public void setPosition(Vector2 pos) {
        float x = pos.x;
        float y = pos.y;
        b2Vec2.TMP_01.Set(x, y);
        vals[POS_X] = x;
        vals[POS_Y] = y;
        b2Transform.p(b2Vec2.TMP_01);
    }

    public float getRotation() {
        b2Rot b2Rot = b2Transform.q();
        float c = b2Rot.c();
        float s = b2Rot.s();
        vals[COS] = c;
        vals[SIN] = s;
        return (float)Math.atan2(s, c);
    }

    public void setRotation(float angle) {
        float c = (float)Math.cos(angle), s = (float)Math.sin(angle);
        b2Rot.TMP_01.c(c);
        b2Rot.TMP_01.s(s);
        vals[COS] = c;
        vals[SIN] = s;
        b2Transform.q(b2Rot.TMP_01);
    }

    public Vector2 getOrientation() {
        b2Rot b2Rot = b2Transform.q();
        float c = b2Rot.c();
        float s = b2Rot.s();
        vals[COS] = c;
        vals[SIN] = s;
        return orientation.set(c, s);
    }

    public void setOrientation(Vector2 orientation) {
        float c = orientation.x;
        float s = orientation.y;
        b2Rot.TMP_01.c(c);
        b2Rot.TMP_01.s(s);
        vals[COS] = c;
        vals[SIN] = s;
        b2Transform.q(b2Rot.TMP_01);
    }
}