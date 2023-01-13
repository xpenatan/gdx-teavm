package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btRigidBody extends btCollisionObject {

    public static btRigidBody WRAPPER_GEN_01 = new btRigidBody(false);
    public static btRigidBody WRAPPER_GEN_02 = new btRigidBody(false);

    /*[-C++;-NATIVE]
        #include "btBulletDynamicsCommon.h"
    */

    public btRigidBody(boolean cMemoryOwn) {
        super(0);
    }

    public btRigidBody(btRigidBodyConstructionInfo constructionInfo) {
        super(0);
        btVector3 out = new btVector3();
        btVector3.convert(constructionInfo.localInertia, out);
        initObject(createNative(constructionInfo.mass, (int)constructionInfo.motionStateAddr, (int)constructionInfo.collisionShapeAddr, out.getCPointer()), true);
    }

    public btRigidBody(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
        super(0);
        btVector3 out = new btVector3();
        btVector3.convert(localInertia != null ? localInertia : btVector3.TEMP_GDX_01.setZero(), out);
        long motionStatePointer = motionState != null ? motionState.getCPointer() : 0;
        long shapePointer = collisionShape != null ? collisionShape.getCPointer() : 0;
        long aNative = createNative(mass, motionStatePointer, shapePointer, out.getCPointer());
        initObject(aNative, true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btRigidBody(mass, (btMotionState*)motionStateAddr, ((btCollisionShape*)collisionShapeAddr), *((btVector3*)localInertiaAddr));
    */
    /*[-teaVM;-NATIVE]
        var motionStateJSObj = Bullet.wrapPointer(motionStateAddr, Bullet.btMotionState);
        var collisionShapeJSObj = Bullet.wrapPointer(collisionShapeAddr, Bullet.btCollisionShape);
        var localInertiaJSObj = Bullet.wrapPointer(localInertiaAddr, Bullet.btVector3);
        var jsObj = new Bullet.btRigidBody(mass, motionStateJSObj, collisionShapeJSObj, localInertiaJSObj);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(float mass, long motionStateAddr, long collisionShapeAddr, long localInertiaAddr);

    /*[-C++;-NATIVE]
        delete (btRigidBody*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public Quaternion getOrientation() {
        getOrientationNATIVE(cPointer, BulletBase.FLOAT_4);
        btQuaternion.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2], BulletBase.FLOAT_4[3]);
        return btQuaternion.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btRigidBody* nativeObject = (btRigidBody*)addr;
        btQuaternion quat = nativeObject->getOrientation();
        array[0] = quat.getX();
        array[1] = quat.getY();
        array[2] = quat.getZ();
        array[3] = quat.getW();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        var quat = nativeObject.getOrientation();
        array[0] = quat.getX();
        array[1] = quat.getY();
        array[2] = quat.getZ();
        array[3] = quat.getW();
    */
    private static native void getOrientationNATIVE(long addr, float [] array);

    public Vector3 getVelocityInLocalPoint(Vector3 relPos) {
        btVector3.convert(relPos, btVector3.TEMP_0);
        return getVelocityInLocalPoint(btVector3.TEMP_0);
    }

    public Vector3 getVelocityInLocalPoint(btVector3 relPos) {
        getVelocityInLocalPointNATIVE(cPointer, relPos.getCPointer(), BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btRigidBody* nativeObject = (btRigidBody*)addr;
        btVector3 & relPos = *((btVector3*)relPosAddr);
        btVector3 vec3 = nativeObject->getVelocityInLocalPoint(relPos);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        var relPos = Bullet.wrapPointer(addr, Bullet.btVector3);
        var vec3 = nativeObject.getVelocityInLocalPoint(relPos);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    private static native void getVelocityInLocalPointNATIVE(long addr, long relPosAddr, float [] array);

    public Vector3 computeGyroscopicImpulseImplicit_World(float dt) {
        computeGyroscopicImpulseImplicit_WorldNATIVE(cPointer, dt, BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btRigidBody* nativeObject = (btRigidBody*)addr;
        btVector3 vec3 = nativeObject->computeGyroscopicImpulseImplicit_World(dt);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        var vec3 = nativeObject.computeGyroscopicImpulseImplicit_World(dt);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    private static native void computeGyroscopicImpulseImplicit_WorldNATIVE(long addr, float dt, float [] array);

    public Vector3 computeGyroscopicImpulseImplicit_Body(float step) {
        computeGyroscopicImpulseImplicit_BodyNATIVE(cPointer, step, BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btRigidBody* nativeObject = (btRigidBody*)addr;
        btVector3 vec3 = nativeObject->computeGyroscopicImpulseImplicit_Body(step);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        var vec3 = nativeObject.computeGyroscopicImpulseImplicit_Body(step);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    private static native void computeGyroscopicImpulseImplicit_BodyNATIVE(long addr, float step, float [] array);

    public Vector3 computeGyroscopicForceExplicit(float maxGyroscopicForce) {
        computeGyroscopicForceExplicitNATIVE(cPointer, maxGyroscopicForce, BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btRigidBody* nativeObject = (btRigidBody*)addr;
        btVector3 vec3 = nativeObject->computeGyroscopicForceExplicit(maxGyroscopicForce);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        var vec3 = nativeObject.computeGyroscopicForceExplicit(maxGyroscopicForce);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    private static native void computeGyroscopicForceExplicitNATIVE(long addr, float maxGyroscopicForce, float [] array);

    public Vector3 getLocalInertia() {
        getLocalInertiaNATIVE(cPointer, BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btRigidBody* nativeObject = (btRigidBody*)addr;
        btVector3 vec3 = nativeObject->getLocalInertia();
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        var vec3 = nativeObject.getLocalInertia();
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    private static native void getLocalInertiaNATIVE(long addr, float [] array);

    static public class btRigidBodyConstructionInfo extends BulletBase {

        public float mass;
        public long motionStateAddr;
        public long collisionShapeAddr;
        public Vector3 localInertia = new Vector3();

        public btRigidBodyConstructionInfo(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
            this.mass = mass;
            this.motionStateAddr = motionState != null ? motionState.getCPointer() : 0;
            this.collisionShapeAddr = collisionShape != null ? collisionShape.getCPointer() : 0;
            this.localInertia.set(localInertia);
        }

        public btRigidBodyConstructionInfo(float mass, btMotionState motionState, btCollisionShape collisionShape) {
            this.mass = mass;
            this.motionStateAddr = motionState != null ? motionState.getCPointer() : 0;
            this.collisionShapeAddr = collisionShape != null ? collisionShape.getCPointer() : 0;
        }
    }
}