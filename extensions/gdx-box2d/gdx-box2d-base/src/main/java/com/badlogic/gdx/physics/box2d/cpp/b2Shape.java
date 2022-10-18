package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Shape extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    private native void GetType();

    /*[-teaVM;-REMOVE]*/
    public native b2ShapeType m_type();

    /*[-teaVM;-REMOVE]*/
    public native void m_type(b2ShapeType type);

    public int getType() {
        return getTypeNATIVE(getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Shape);
        return body.GetType();
    */
    private static native int getTypeNATIVE(long addr);

    public native float m_radius();

    public native void m_radius(float m_radius);
}