package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Contact extends Box2DBase {

    public native b2Manifold GetManifold();

    //TODO need to check if this works
    public native void GetWorldManifold(b2WorldManifold manifold);

    public native b2Fixture GetFixtureA();

    public native b2Fixture GetFixtureB();
}