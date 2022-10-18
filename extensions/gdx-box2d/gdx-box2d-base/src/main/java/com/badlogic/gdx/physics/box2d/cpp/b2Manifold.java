package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Manifold extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    public native b2ManifoldType type();

    /*[-teaVM;-REMOVE]*/
    public native void type(b2ManifoldType type);
}