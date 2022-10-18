package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2WorldManifold extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    public native void points(b2Vec2[] points);

    /*[-teaVM;-REMOVE]*/
    public native void separations(float[] separations);
}