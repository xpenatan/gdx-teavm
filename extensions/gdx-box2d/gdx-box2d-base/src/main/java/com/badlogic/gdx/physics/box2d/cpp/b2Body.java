package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2Body extends Box2DBase {

    /*[-teaVM;-REMOVE]*/
    public native b2BodyType GetType();

    /*[-teaVM;-REMOVE]*/
    public native void SetType(b2BodyType type);

    /*[-teaVM;-REMOVE]*/
    public native b2Fixture CreateFixture(b2FixtureDef def);

    /*[-teaVM;-REMOVE]*/
    public native b2Fixture CreateFixture(b2Shape shape, float density);

    /*[-teaVM;-REMOVE]*/
    public native void DestroyFixture(b2Fixture fixture);

    /*[-teaVM;-REMOVE]*/
    public native void GetMassData(b2MassData data);

    /*[-teaVM;-REMOVE]*/
    public native void SetMassData(b2MassData data);

    /*[-teaVM;-REMOVE]*/
    public native b2ContactEdge GetContactList();

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

    public long createFixture(
            b2Shape shape, float friction, float restitution, float density,
            boolean isSensor, short filterCategoryBits, short filterMaskBits, short filterGroupIndex
    ) {
        return createFixtureNATIVE(getCPointer(), shape.getCPointer(), friction, restitution, density, isSensor,
                filterCategoryBits, filterMaskBits, filterGroupIndex);
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Body);
        var shape = Box2D.wrapPointer(shapeAddr, Box2D.b2Shape);
        var fixtureDef = new Box2D.b2FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.density = density;
        fixtureDef.isSensor = isSensor;
        fixtureDef.filter.maskBits = filterMaskBits;
        fixtureDef.filter.categoryBits = filterCategoryBits;
        fixtureDef.filter.groupIndex = filterGroupIndex;
        var fixture = body.CreateFixture(fixtureDef);
        return Box2D.getPointer(fixture);
    */
    private static native long createFixtureNATIVE(
            long addr, long shapeAddr, float friction, float restitution, float density,
            boolean isSensor, short filterCategoryBits, short filterMaskBits, short filterGroupIndex
    );

    public long createFixture(b2Shape shape, float density) {
        return CreateFixtureNATIVE(getCPointer(), shape.getCPointer(), density);
    }

    /*[-teaVM;-NATIVE]
        var body = Box2D.wrapPointer(addr, Box2D.b2Body);
        var shape = Box2D.wrapPointer(shapeAddr, Box2D.b2Shape);
        var fixture = body.CreateFixture(shape, density);
        return Box2D.getPointer(fixture);
    */
    private static native long CreateFixtureNATIVE(long addr, long shapeAddr, float density);

    public native boolean IsActive();

    public native boolean IsAwake();

    public native b2Transform GetTransform();

    public native b2Vec2 GetPosition();

    public native float GetAngle();

    public native b2Vec2 GetLinearVelocity();
}