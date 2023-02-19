package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

/**
 * @author xpenatan
 */
public class btFixedConstraint extends btGeneric6DofSpring2Constraint {

    /*[-C++;-NATIVE]
        #include "BulletDynamics/ConstraintSolver/btFixedConstraint.h"
    */

    public btFixedConstraint(btRigidBody rbA, btRigidBody rbB, Matrix4 frameInA, Matrix4 frameInB) {
        btTransform btframeInA = btTransform.TEMP_0;
        btTransform btframeInB = btTransform.TEMP_1;
        btTransform.convert(frameInA, btframeInA);
        btTransform.convert(frameInB, btframeInB);
        initObject(createNative(rbA.getCPointer(), rbB.getCPointer(), btframeInA.getCPointer(), btframeInB.getCPointer()), true);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btFixedConstraint(*((btRigidBody*)rbAAddr), *((btRigidBody*)rbBAddr), *((btTransform*)frameInAAddr), *((btTransform*)frameInBAddr));
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btFixedConstraint(rbAAddr, rbBAddr, frameInAAddr, frameInBAddr);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long rbAAddr, long rbBAddr, long frameInAAddr, long frameInBAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btFixedConstraint*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btFixedConstraint);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);
}