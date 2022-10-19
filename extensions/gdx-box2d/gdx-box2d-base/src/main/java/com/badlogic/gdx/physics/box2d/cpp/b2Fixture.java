package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Fixture extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    public native void GetUserData();

    /*[-teaVM;-REMOVE]*/
    public native void SetFilterData(b2Filter filter);

    /*[-teaVM;-REMOVE]*/
    public native b2Filter GetFilterData();

    /*[-teaVM;-REMOVE]*/
    public native boolean RayCast(b2RayCastOutput output, b2RayCastInput input, int childIndex);

    /*[-teaVM;-REMOVE]*/
    public native void GetMassData(b2MassData massData);

    /*[-teaVM;-REMOVE]*/
    public native b2ShapeType GetType();

    /*[-teaVM;-REMOVE]*/
    public native b2AABB GetAABB(int childIndex);

    /*[-teaVM;-REMOVE]*/
    public native b2Body GetBody();

    public int getType() {
        return getTypeNATIVE(getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Fixture);
        return body.GetType();
    */
    private static native int getTypeNATIVE(long addr);

    public native b2Shape GetShape();

    public native boolean TestPoint(b2Vec2 p);

    public long getBody() {
        return GetBodyNATIVE(getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var fixture = Box2D.wrapPointer(addr, Box2D.b2Fixture);
        var body = fixture.GetBody();
        return Box2D.getPointer(body);
    */
    private static native long GetBodyNATIVE(long addr);
}