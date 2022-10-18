package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Rot extends Box2DBase {

    public static final b2Rot TMP_01 = new b2Rot();

    public b2Rot() {
        initObject(createNative(), true);
    }

    public b2Rot(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2Rot();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public native float s();

    public native void s(float s);

    public native float c();

    public native void c(float c);
}