package com.badlogic.gdx.physics.box2d.cpp;

import com.badlogic.gdx.physics.box2d.Box2DBase;

public class b2World extends Box2DBase {

    public b2World(b2Vec2 vec2) {
        initObject(createNative(vec2.getCPointer()), true);
    }

    public b2World(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-REMOVE]*/
    private native b2Contact GetContactList();

    /*[-teaVM;-REMOVE]*/
    public native void QueryAABB(b2QueryCallback callback, b2AABB aabb);

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
        var body = Box2D.wrapPointer(bodyAddr, Box2D.b2Body);
        world.DestroyBody(body);
    */
    private static native void DestroBodyNATIVE(long addr, long bodyAddr);

    public void destroyFixture(b2Body body, b2Fixture fixture) {
        DestroyFixtureNATIVE(getCPointer(), body.getCPointer(), fixture.getCPointer());
    }

    //TODO add missing methods
    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var body = Box2D.wrapPointer(bodyAddr, Box2D.b2World);
        var fixture = Box2D.wrapPointer(bodyAddr, Box2D.b2Fixture);
        body.DestroyFixture(fixture);
    */
    private static native void DestroyFixtureNATIVE(long addr, long bodyAddr, long fixtureAddr);

    public void destroyJoint(b2Joint joint) {
        DestroyJointNATIVE(getCPointer(), joint.getCPointer());
    }

    //TODO add missing methods
    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var joint = Box2D.wrapPointer(bodyAddr, Box2D.b2Joint);
        body.DestroyJoint(joint);
    */
    private static native void DestroyJointNATIVE(long addr, long jointAddr);

    public native int GetContactCount();

    /*[-teaVM;-REPLACE]
        public void GetContactList(int[] contacts) {
            GetContactListNATIVE(getCPointer(), contacts);
        }
     */
    public void GetContactList(long[] contacts) {
        GetContactListNATIVE(getCPointer(), contacts);
    }

    //TODO need to test if this is working
    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var contact = world.GetContactList();
        var contactAddr = Box2D.getPointer(contact);
        var i = 0;
        while(contactAddr != 0) {
            contacts[i] = contactAddr;
            contact = contact.GetNext();
            contactAddr = Box2D.getPointer(contact);
            i++;
        }
    */
    private static native void GetContactListNATIVE(long addr, long[] contacts);

    public long CreateMouseJoint(b2Body bodyA, b2Body bodyB, boolean collideConnected, float targetX, float targetY,
                                 float maxForce, float frequencyHz, float dampingRatio) {
        return jniCreateMouseJointNATIVE(getCPointer(), bodyA.getCPointer(), bodyB.getCPointer(), collideConnected, targetX, targetY, maxForce, frequencyHz, dampingRatio);
    }

    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);
        var bodyA = Box2D.wrapPointer(bodyAAddr, Box2D.b2Body);
        var bodyB = Box2D.wrapPointer(bodyBAddr, Box2D.b2Body);

        var def = new Box2D.b2MouseJointDef();
        def.bodyA = bodyA;
        def.bodyB = bodyB;
        def.collideConnected = collideConnected;
        def.target = new Box2D.b2Vec2( targetX, targetY );
        def.maxForce = maxForce;
        def.frequencyHz = frequencyHz;
        def.dampingRatio = dampingRatio;
        var joint = world.CreateJoint(def);
        return Box2D.getPointer(joint);
    */
    private static native long jniCreateMouseJointNATIVE(long addr, long bodyAAddr, long bodyBAddr, boolean collideConnected,
                                                         float targetX, float targetY, float maxForce, float frequencyHz, float dampingRatio);

    /*[-teaVM;-REPLACE]
        @org.teavm.jso.JSFunctor
        public interface AABBFunction extends org.teavm.jso.JSObject {
            boolean ReportFixture(int b2FixtureAddr);
        }
    */
    public interface AABBFunction {
        boolean ReportFixture(long b2FixtureAddr);
    }

    public void QueryAABB(AABBFunction aabbFunction, float lowX, float lowY, float upX, float upY) {
        QueryAABBNATIVE(getCPointer(), aabbFunction, lowX, lowY, upX, upY);
    }

    /*[-teaVM;-NATIVE]
        var world = Box2D.wrapPointer(addr, Box2D.b2World);

        var myQueryCallback = new Box2D.JSQueryCallback();

        var aabb = new Box2D.b2AABB();
        aabb.lowerBound = new Box2D.b2Vec2(lowX, lowY);
        aabb.upperBound = new Box2D.b2Vec2(upX, upY);

        world.QueryAABB(myQueryCallback, aabb);
    */
    private static native void QueryAABBNATIVE(long addr, AABBFunction function, float lowX, float lowY, float upX, float upY);
}