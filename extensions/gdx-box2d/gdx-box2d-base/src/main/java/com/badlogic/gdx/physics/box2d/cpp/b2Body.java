package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Body extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    public native b2BodyType GetType();
    /*[-teaVM;-REMOVE]*/
    public native void SetType(b2BodyType type);

    public int getType() {
        return getTypeNATIVE(getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Body);
        return body.GetType();
    */
    private static native int getTypeNATIVE(long addr);

    public void setType(int type) {
        setTypeNATIVE(getCPointer(), type);
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Body);
        return body.SetType();
    */
    private static native void setTypeNATIVE(long addr, int type);
}