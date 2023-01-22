package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;

public class btRotationalLimitMotor2 extends BulletBase {

    /*[-C++;-NATIVE]
        #include "BulletDynamics/ConstraintSolver/btGeneric6DofSpring2Constraint.h"
    */

    protected btRotationalLimitMotor2() {
    }
//
//    public void setLoLimit (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_loLimit_set(swigCPtr, this, value);
//    }
//
    public float getLoLimit () {
        return getLoLimitNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_loLimit;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_loLimit();
    */
    private static native float getLoLimitNATIVE(long addr);
//
//    public void setHiLimit (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_hiLimit_set(swigCPtr, this, value);
//    }
//
    public float getHiLimit () {
        return getHiLimitNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_hiLimit;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_hiLimit();
    */
    private static native float getHiLimitNATIVE(long addr);
//
//    public void setBounce (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_bounce_set(swigCPtr, this, value);
//    }
//
//    public float getBounce () {
//        return DynamicsJNI.btRotationalLimitMotor2_bounce_get(swigCPtr, this);
//    }
//
//    public void setStopERP (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_stopERP_set(swigCPtr, this, value);
//    }
//
//    public float getStopERP () {
//        return DynamicsJNI.btRotationalLimitMotor2_stopERP_get(swigCPtr, this);
//    }
//
//    public void setStopCFM (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_stopCFM_set(swigCPtr, this, value);
//    }
//
//    public float getStopCFM () {
//        return DynamicsJNI.btRotationalLimitMotor2_stopCFM_get(swigCPtr, this);
//    }
//
//    public void setMotorERP (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_motorERP_set(swigCPtr, this, value);
//    }
//
//    public float getMotorERP () {
//        return DynamicsJNI.btRotationalLimitMotor2_motorERP_get(swigCPtr, this);
//    }
//
//    public void setMotorCFM (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_motorCFM_set(swigCPtr, this, value);
//    }
//
//    public float getMotorCFM () {
//        return DynamicsJNI.btRotationalLimitMotor2_motorCFM_get(swigCPtr, this);
//    }
//
//    public void setEnableMotor (boolean value) {
//        DynamicsJNI.btRotationalLimitMotor2_enableMotor_set(swigCPtr, this, value);
//    }
//
//    public boolean getEnableMotor () {
//        return DynamicsJNI.btRotationalLimitMotor2_enableMotor_get(swigCPtr, this);
//    }
//
//    public void setTargetVelocity (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_targetVelocity_set(swigCPtr, this, value);
//    }
//
//    public float getTargetVelocity () {
//        return DynamicsJNI.btRotationalLimitMotor2_targetVelocity_get(swigCPtr, this);
//    }
//
//    public void setMaxMotorForce (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_maxMotorForce_set(swigCPtr, this, value);
//    }
//
//    public float getMaxMotorForce () {
//        return DynamicsJNI.btRotationalLimitMotor2_maxMotorForce_get(swigCPtr, this);
//    }
//
//    public void setServoMotor (boolean value) {
//        DynamicsJNI.btRotationalLimitMotor2_servoMotor_set(swigCPtr, this, value);
//    }
//
//    public boolean getServoMotor () {
//        return DynamicsJNI.btRotationalLimitMotor2_servoMotor_get(swigCPtr, this);
//    }
//
//    public void setServoTarget (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_servoTarget_set(swigCPtr, this, value);
//    }
//
//    public float getServoTarget () {
//        return DynamicsJNI.btRotationalLimitMotor2_servoTarget_get(swigCPtr, this);
//    }
//
//    public void setEnableSpring (boolean value) {
//        DynamicsJNI.btRotationalLimitMotor2_enableSpring_set(swigCPtr, this, value);
//    }
//
//    public boolean getEnableSpring () {
//        return DynamicsJNI.btRotationalLimitMotor2_enableSpring_get(swigCPtr, this);
//    }
//
//    public void setSpringStiffness (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_springStiffness_set(swigCPtr, this, value);
//    }
//
//    public float getSpringStiffness () {
//        return DynamicsJNI.btRotationalLimitMotor2_springStiffness_get(swigCPtr, this);
//    }
//
//    public void setSpringStiffnessLimited (boolean value) {
//        DynamicsJNI.btRotationalLimitMotor2_springStiffnessLimited_set(swigCPtr, this, value);
//    }
//
//    public boolean getSpringStiffnessLimited () {
//        return DynamicsJNI.btRotationalLimitMotor2_springStiffnessLimited_get(swigCPtr, this);
//    }
//
//    public void setSpringDamping (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_springDamping_set(swigCPtr, this, value);
//    }
//
//    public float getSpringDamping () {
//        return DynamicsJNI.btRotationalLimitMotor2_springDamping_get(swigCPtr, this);
//    }
//
//    public void setSpringDampingLimited (boolean value) {
//        DynamicsJNI.btRotationalLimitMotor2_springDampingLimited_set(swigCPtr, this, value);
//    }
//
//    public boolean getSpringDampingLimited () {
//        return DynamicsJNI.btRotationalLimitMotor2_springDampingLimited_get(swigCPtr, this);
//    }
//
//    public void setEquilibriumPoint (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_equilibriumPoint_set(swigCPtr, this, value);
//    }
//
//    public float getEquilibriumPoint () {
//        return DynamicsJNI.btRotationalLimitMotor2_equilibriumPoint_get(swigCPtr, this);
//    }
//
//    public void setCurrentLimitError (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_currentLimitError_set(swigCPtr, this, value);
//    }
//
//    public float getCurrentLimitError () {
//        return DynamicsJNI.btRotationalLimitMotor2_currentLimitError_get(swigCPtr, this);
//    }
//
//    public void setCurrentLimitErrorHi (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_currentLimitErrorHi_set(swigCPtr, this, value);
//    }
//
//    public float getCurrentLimitErrorHi () {
//        return DynamicsJNI.btRotationalLimitMotor2_currentLimitErrorHi_get(swigCPtr, this);
//    }
//
//    public void setCurrentPosition (float value) {
//        DynamicsJNI.btRotationalLimitMotor2_currentPosition_set(swigCPtr, this, value);
//    }
//
//    public float getCurrentPosition () {
//        return DynamicsJNI.btRotationalLimitMotor2_currentPosition_get(swigCPtr, this);
//    }
//
//    public void setCurrentLimit (int value) {
//        DynamicsJNI.btRotationalLimitMotor2_currentLimit_set(swigCPtr, this, value);
//    }
//
//    public int getCurrentLimit () {
//        return DynamicsJNI.btRotationalLimitMotor2_currentLimit_get(swigCPtr, this);
//    }
//
//    public boolean isLimited () {
//        return DynamicsJNI.btRotationalLimitMotor2_isLimited(swigCPtr, this);
//    }
//
//    public void testLimitValue (float test_value) {
//        DynamicsJNI.btRotationalLimitMotor2_testLimitValue(swigCPtr, this, test_value);
//    }
}
