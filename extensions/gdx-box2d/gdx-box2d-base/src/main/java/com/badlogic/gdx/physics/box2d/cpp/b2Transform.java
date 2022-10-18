package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Transform extends Box2DBase {

    public b2Transform() {
        initObject(createNative(), true);
    }

    public b2Transform(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2Transform();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public native b2Vec2 p();

    public native void p(b2Vec2 p);

    public native b2Rot q();

    public native void q(b2Rot q);
}