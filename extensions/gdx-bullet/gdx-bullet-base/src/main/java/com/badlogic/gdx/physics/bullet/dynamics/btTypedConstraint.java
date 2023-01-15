package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.linearmath.btTypedObject;

/**
 * @author xpenatan
 */
public class btTypedConstraint extends btTypedObject {

    /*[-C++;-NATIVE]
        #include "BulletDynamics/ConstraintSolver/btHingeConstraint.h"
    */

    public static btTypedConstraint WRAPPER_GEN_01 = new btTypedConstraint(false);

    protected btTypedConstraint() {
    }

    protected btTypedConstraint(boolean cMemoryOwn) {
    }

    public btRigidBody getRigidBodyA() {
        int pointer = getRigidBodyANATIVE(cPointer);
        btRigidBody.WRAPPER_GEN_01.setPointer(pointer);
        return btRigidBody.WRAPPER_GEN_01;
    }

    /*[-C++;-NATIVE]
        btTypedConstraint* nativeObject = (btTypedConstraint*)addr;
        btRigidBody & body = nativeObject->getRigidBodyA();
        return (jlong)&body;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTypedConstraint);
        var returnedJSObj = jsObj.getRigidBodyA();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native int getRigidBodyANATIVE(long addr);

    public btRigidBody getRigidBodyB() {
        int pointer = getRigidBodyBNATIVE(cPointer);
        btRigidBody.WRAPPER_GEN_02.setPointer(pointer);
        return btRigidBody.WRAPPER_GEN_02;
    }

    /*[-C++;-NATIVE]
        btTypedConstraint* nativeObject = (btTypedConstraint*)addr;
        btRigidBody & body = nativeObject->getRigidBodyB();
        return (jlong)&body;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTypedConstraint);
        var returnedJSObj = jsObj.getRigidBodyB();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native int getRigidBodyBNATIVE(long addr);
}