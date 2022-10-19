package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2WorldManifold extends Box2DBase {

    public b2WorldManifold() {
        initObject(createNative(), true);
    }

    public b2WorldManifold(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2WorldManifold();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    /*[-teaVM;-REMOVE]*/
    public native void points(b2Vec2[] points);

    /*[-teaVM;-REMOVE]*/
    public native b2Vec2[] points();

    /*[-teaVM;-REMOVE]*/
    public native void separations(float[] separations);
}