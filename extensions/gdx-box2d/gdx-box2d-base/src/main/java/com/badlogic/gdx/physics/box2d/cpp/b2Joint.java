package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Joint extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    public native b2JointType GetType();

    public native b2Vec2 GetAnchorA();

    public native b2Vec2 GetAnchorB();

    public native b2Body GetBodyA();

    public native b2Body GetBodyB();

    public int getType() {
        return getTypeNATIVE(getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Joint);
        return body.GetType();
    */
    private static native int getTypeNATIVE(long addr);
}