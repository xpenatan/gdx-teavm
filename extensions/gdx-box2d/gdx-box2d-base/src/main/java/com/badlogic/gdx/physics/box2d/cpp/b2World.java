package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2World extends Box2DBase {

    public b2World(b2Vec2 vec2) {
        initObject(createNative(vec2.getCPointer()), true);
    }

    public b2World(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var vec2 = Box2D.wrapPointer(b2VecGravityAddr, Box2D.b2Vec2);
        var jsObj = new Box2D.b2World(vec2);
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative(long b2VecGravityAddr);

    public native b2Vec2 GetGravity();

    public native void Step(float timeStep, int velocityIterations, int positionIterations);

    /*[-teaVM;-REMOVE]*/
    private native b2Body CreateBody(b2BodyDef def);

    public long CreateBody(
            int type, float positionX, float positionY, float angle, float linearVelocityX,
            float linearVelocityY, float angularVelocity, float linearDamping, float angularDamping, boolean allowSleep,
            boolean awake, boolean fixedRotation, boolean bullet, boolean active, float inertiaScale) {
        return CreateBodyNative(getCPointer(), type, positionX, positionY, angle, linearVelocityX, linearVelocityY,
                angularVelocity, linearDamping, angularDamping, allowSleep, awake, fixedRotation, bullet, active, inertiaScale);
    }

    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var bodyDef = new Box2D.b2BodyDef();
        bodyDef.type = type;
        bodyDef.position.Set(positionX, positionY);
        bodyDef.angle = angle;
        bodyDef.linearVelocity.Set(linearVelocityX, linearVelocityY);
        bodyDef.angularVelocity = angularVelocity;
        bodyDef.linearDamping = linearDamping;
        bodyDef.angularDamping = angularDamping;
        bodyDef.allowSleep = allowSleep;
        bodyDef.awake = awake;
        bodyDef.fixedRotation = fixedRotation;
        bodyDef.bullet = bullet;
        bodyDef.active = active;
        bodyDef.gravityScale = inertiaScale;
        var body = world.CreateBody(bodyDef);
        return Box2D.getPointer(body);
    */
    private static native long CreateBodyNative(
            long addr, int type, float positionX, float positionY, float angle, float linearVelocityX,
            float linearVelocityY, float angularVelocity, float linearDamping, float angularDamping, boolean allowSleep,
            boolean awake, boolean fixedRotation, boolean bullet, boolean active, float inertiaScale);

    public void DestroyBody(b2Body body) {
        DestroBodyNATIVE(getCPointer(), body.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var body = Box2D.wrapPointer(bodyAddr, Box2D.b2World);
        world.DestroyBody(body);
    */
    private static native void DestroBodyNATIVE(long addr, long bodyAddr);

    public void destroyFixture(b2Body body, b2Fixture fixture) {
        DestroyFixtureNATIVE(getCPointer(), body.getCPointer(), fixture.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var body = Box2D.wrapPointer(bodyAddr, Box2D.b2World);
        var fixture = Box2D.wrapPointer(bodyAddr, Box2D.b2Fixture);
        body.DestroyFixture(fixture);
    */
    private static native void DestroyFixtureNATIVE(long addr, long bodyAddr, long fixtureAddr);
}