package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2World extends Box2DBase {

    public b2World(b2Vec2 vec2) {
        initObject(createNative(vec2.getCPointer()), true);
    }

    public b2World(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var vec2 = Box2D.wrapPointer(b2VecGravityAddr, Box2D.b2Vec2);
        var jsObj = new Box2D.b2World(vec2);
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative(long b2VecGravityAddr);

    public native b2Vec2 GetGravity();

    public native void Step(float timeStep, int velocityIterations, int positionIterations);
}