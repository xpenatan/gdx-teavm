package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

/**
 * @author xpenatan
 */
public class btGeneric6DofSpring2Constraint extends btTypedConstraint {

    /*[-C++;-NATIVE]
        #include "BulletDynamics/ConstraintSolver/btGeneric6DofSpring2Constraint.h"
    */

    private btTranslationalLimitMotor2 translationalLimitMotor2 = new btTranslationalLimitMotor2();
    private btRotationalLimitMotor2 rotationLimitMotor2X = new btRotationalLimitMotor2();
    private btRotationalLimitMotor2 rotationLimitMotor2Y = new btRotationalLimitMotor2();
    private btRotationalLimitMotor2 rotationLimitMotor2Z = new btRotationalLimitMotor2();

    protected btGeneric6DofSpring2Constraint() {
    }

    public btGeneric6DofSpring2Constraint(btRigidBody rbA, btRigidBody rbB, Matrix4 frameInA, Matrix4 frameInB) {
        btTransform btframeInA = btTransform.TEMP_0;
        btTransform btframeInB = btTransform.TEMP_1;
        btTransform.convert(frameInA, btframeInA);
        btTransform.convert(frameInB, btframeInB);
        initObject(createNative(rbA.getCPointer(), rbB.getCPointer(), btframeInA.getCPointer(), btframeInB.getCPointer()), true);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btGeneric6DofSpring2Constraint(*((btRigidBody*)rbAAddr), *((btRigidBody*)rbBAddr), *((btTransform*)frameInAAddr), *((btTransform*)frameInBAddr));
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btGeneric6DofSpring2Constraint(rbAAddr, rbBAddr, frameInAAddr, frameInBAddr);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long rbAAddr, long rbBAddr, long frameInAAddr, long frameInBAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btGeneric6DofSpring2Constraint*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btGeneric6DofSpring2Constraint);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public btRotationalLimitMotor2 getRotationalLimitMotor (int index) {
        long pointer = getRotationalLimitMotorNATIVE(getCPointer(), index);
        if(index == 0) {
            rotationLimitMotor2X.setPointer(pointer);
            return rotationLimitMotor2X;
        }
        else if(index == 1) {
            rotationLimitMotor2Y.setPointer(pointer);
            return rotationLimitMotor2Y;
        }
        else if(index == 2) {
            rotationLimitMotor2Z.setPointer(pointer);
            return rotationLimitMotor2Z;
        }
        return null;
    }

    /*[-C++;-NATIVE]
        btGeneric6DofSpring2Constraint* nativeObject = (btGeneric6DofSpring2Constraint*)addr;
        btRotationalLimitMotor2 * returnObj = nativeObject->getRotationalLimitMotor(index);
        return (jlong)returnObj;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btGeneric6DofSpring2Constraint);
        var returnedJSObj = jsObj.getRotationalLimitMotor(index);
        return Bullet.getPointer(returnedJSObj);
    */
    private static native long getRotationalLimitMotorNATIVE(long addr, long index);

    public btTranslationalLimitMotor2 getTranslationalLimitMotor () {
        translationalLimitMotor2.setPointer(getTranslationalLimitMotorNATIVE(getCPointer()));
        return translationalLimitMotor2;
    }

    /*[-C++;-NATIVE]
        btGeneric6DofSpring2Constraint* nativeObject = (btGeneric6DofSpring2Constraint*)addr;
        btTranslationalLimitMotor2 * returnObj = nativeObject->getTranslationalLimitMotor();
        return (jlong)returnObj;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btGeneric6DofSpring2Constraint);
        var returnedJSObj = jsObj.getTranslationalLimitMotor();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native long getTranslationalLimitMotorNATIVE(long addr);

}