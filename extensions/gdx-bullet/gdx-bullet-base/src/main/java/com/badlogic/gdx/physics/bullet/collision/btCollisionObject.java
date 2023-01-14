package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.VoidPtr;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btCollisionObject extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public static btCollisionObject temp01 = new btCollisionObject(0);

    public static btCollisionObject WRAPPER_GEN_01 = new btCollisionObject(false);

    protected btCollisionObject(boolean cMemoryOwn) {
    }

    public btCollisionObject() {
        //TODO add native code
    }

    protected btCollisionObject(long cPointer) {
        initObject(cPointer, false);
    }

    btCollisionShape collisionShape;

    public void setCollisionShape(btCollisionShape collisionShape) {
        this.collisionShape = collisionShape;
        setCollisionShape(cPointer, collisionShape != null ? collisionShape.getCPointer() : 0);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        jsObj.setCollisionShape(collisionShapeAddr);
    */
    private static native void setCollisionShape(long addr, long collisionShapeAddr);

    public btCollisionShape getCollisionShape() {
        return collisionShape;
    }

    public void setUserPointer(VoidPtr userPointer) {
    }

    public void setUserPointer(long userPointer) {
        setUserPointerNATIVE(cPointer, userPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionObject* nativeObject = (btCollisionObject*)addr;
        nativeObject->setUserPointer((void*)userPointer);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        jsObj.setUserPointer(userPointer);
    */
    private static native void setUserPointerNATIVE(long addr, long userPointer);

    /*[-teaVM;-REPLACE]
    public long getUserPointer () {
        int userPointer = getUserPointer(cPointer);
        long longPointer = (long)userPointer;
        return longPointer;
    }
    */
    public long getUserPointer() {
        long userPointer = getUserPointer(cPointer);
        return userPointer;
    }

    /*[-C++;-NATIVE]
        btCollisionObject* nativeObject = (btCollisionObject*)addr;
        return (jlong)nativeObject->getUserPointer();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        var intPointer = jsObj.getUserPointer();
        return intPointer;
    */
    private static native long getUserPointer(long addr);

    public void getWorldTransform(Matrix4 out) {
        // Gdx method
        int worldTransformAddr = (int)getWorldTransformAddr(cPointer);
        btTransform.convert(worldTransformAddr, out);
    }

    /*[-C++;-NATIVE]
        btCollisionObject* nativeObject = (btCollisionObject*)addr;
        return (jlong)&nativeObject->getWorldTransform();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        var jsTransform = jsObj.getWorldTransform();
        return Bullet.getPointer(jsTransform);
    */
    private static native long getWorldTransformAddr(long addr);

    public void setInterpolationAngularVelocity(btVector3 angvel) {
        setInterpolationAngularVelocityNATIVE(cPointer, (int) angvel.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionObject* nativeObject = (btCollisionObject*)addr;
        nativeObject->setInterpolationAngularVelocity(*((btVector3*)angvelAddr));
    */
    private static native void setInterpolationAngularVelocityNATIVE(long addr, long angvelAddr);

    public void setInterpolationAngularVelocity(Vector3 angvelGDX) {
        btVector3.convert(angvelGDX, btVector3.TEMP_0);
        btVector3 angvel = btVector3.TEMP_0;
        setInterpolationAngularVelocityNATIVE(cPointer, (int) angvel.getCPointer());
    }

    public void setInterpolationLinearVelocity(btVector3 linvel) {
        setInterpolationLinearVelocityNATIVE(cPointer, (int) linvel.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionObject* nativeObject = (btCollisionObject*)addr;
        nativeObject->setInterpolationLinearVelocity(*((btVector3*)linvelAddr));
    */
    private static native void setInterpolationLinearVelocityNATIVE(long addr, long linvelAddr);

    public void setInterpolationLinearVelocity(Vector3 linvelGDX) {
        btVector3.convert(linvelGDX, btVector3.TEMP_0);
        btVector3 linvel = btVector3.TEMP_0;
        setInterpolationLinearVelocityNATIVE(cPointer, (int) linvel.getCPointer());
    }

    public void setInterpolationWorldTransform(btTransform trans) {
        setInterpolationWorldTransformNATIVE(cPointer, (int) trans.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionObject* nativeObject = (btCollisionObject*)addr;
        nativeObject->setInterpolationWorldTransform(*((btTransform*)transAddr));
    */
    private static native void setInterpolationWorldTransformNATIVE(long addr, long transAddr);

    public void setInterpolationWorldTransform(Matrix4 transGDX) {
        btTransform.convert(transGDX, btTransform.TEMP_0);
        btTransform trans = btTransform.TEMP_0;
        setInterpolationWorldTransformNATIVE(cPointer, (int) trans.getCPointer());
    }
}