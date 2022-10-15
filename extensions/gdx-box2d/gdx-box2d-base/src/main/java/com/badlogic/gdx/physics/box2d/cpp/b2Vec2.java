package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Vec2 extends Box2DBase {

    public b2Vec2() {
        initObject(createNative(), true);
    }

    public b2Vec2(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2Vec2();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public native void Set(float x, float y);

    public native float x();

    public native float y();
}