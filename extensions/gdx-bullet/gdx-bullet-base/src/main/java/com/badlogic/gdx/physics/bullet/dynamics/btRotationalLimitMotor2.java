package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;

public class btRotationalLimitMotor2 extends BulletBase {

    /*[-C++;-NATIVE]
        #include "BulletDynamics/ConstraintSolver/btGeneric6DofSpring2Constraint.h"
    */

    protected btRotationalLimitMotor2() {
    }

    public void setLoLimit (float value) {
        setLoLimitNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_loLimit = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_loLimit(value);
    */
    private static native void setLoLimitNATIVE(long addr, float value);

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

    public void setHiLimit (float value) {
        setHiLimitNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_hiLimit = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_hiLimit(value);
    */
    private static native void setHiLimitNATIVE(long addr, float value);

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

    public void setBounce (float value) {
        setBounceNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_bounce = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_bounce(value);
    */
    private static native void setBounceNATIVE(long addr, float value);

    public float getBounce () {
        return getBounceNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_bounce;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_bounce();
    */
    private static native float getBounceNATIVE(long addr);

    public void setStopERP (float value) {
        setStopERPNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_stopERP = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_stopERP(value);
    */
    private static native void setStopERPNATIVE(long addr, float value);

    public float getStopERP () {
        return getStopERPNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_stopERP;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_stopERP();
    */
    private static native float getStopERPNATIVE(long addr);

    public void setStopCFM (float value) {
        setStopCFMNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_stopCFM = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_stopCFM(value);
    */
    private static native void setStopCFMNATIVE(long addr, float value);

    public float getStopCFM () {
        return getStopCFMNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_stopCFM;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_stopCFM();
    */
    private static native float getStopCFMNATIVE(long addr);

    public void setMotorERP (float value) {
        setStopCFMNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_motorERP = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_motorERP(value);
    */
    private static native void setMotorERPNATIVE(long addr, float value);

    public float getMotorERP () {
        return getMotorERPNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_motorERP;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_motorERP();
    */
    private static native float getMotorERPNATIVE(long addr);

    public void setMotorCFM (float value) {
        setMotorCFMNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_motorCFM = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_motorCFM(value);
    */
    private static native void setMotorCFMNATIVE(long addr, float value);

    public float getMotorCFM () {
        return getMotorCFMNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_motorCFM;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_motorCFM();
    */
    private static native float getMotorCFMNATIVE(long addr);

    public void setEnableMotor (boolean value) {
        setEnableMotorNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_enableMotor = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_enableMotor(value);
    */
    private static native void setEnableMotorNATIVE(long addr, boolean value);

    public boolean getEnableMotor () {
        return getEnableMotorNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_enableMotor;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_enableMotor();
    */
    private static native boolean getEnableMotorNATIVE(long addr);

    public void setTargetVelocity (float value) {
        setTargetVelocityNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_targetVelocity = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_targetVelocity(value);
    */
    private static native void setTargetVelocityNATIVE(long addr, float value);

    public float getTargetVelocity () {
        return getTargetVelocityNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_targetVelocity;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_targetVelocity();
    */
    private static native float getTargetVelocityNATIVE(long addr);

    public void setMaxMotorForce (float value) {
        setMaxMotorForceNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_maxMotorForce = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_maxMotorForce(value);
    */
    private static native void setMaxMotorForceNATIVE(long addr, float value);

    public float getMaxMotorForce () {
        return getMaxMotorForceNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_maxMotorForce;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_maxMotorForce();
    */
    private static native float getMaxMotorForceNATIVE(long addr);

    public void setServoMotor (boolean value) {
        setServoMotorNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_servoMotor = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_servoMotor(value);
    */
    private static native void setServoMotorNATIVE(long addr, boolean value);

    public boolean getServoMotor () {
        return getServoMotorNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_servoMotor;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_servoMotor();
    */
    private static native boolean getServoMotorNATIVE(long addr);

    public void setServoTarget (float value) {
        setServoTargetNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_servoTarget = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_servoTarget(value);
    */
    private static native void setServoTargetNATIVE(long addr, float value);

    public float getServoTarget () {
        return getServoTargetNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_servoTarget;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_servoTarget();
    */
    private static native float getServoTargetNATIVE(long addr);

    public void setEnableSpring (boolean value) {
        setEnableSpringNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_enableSpring = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_enableSpring(value);
    */
    private static native void setEnableSpringNATIVE(long addr, boolean value);

    public boolean getEnableSpring () {
        return getEnableSpringNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_enableSpring;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_enableSpring();
    */
    private static native boolean getEnableSpringNATIVE(long addr);

    public void setSpringStiffness (float value) {
        setSpringStiffnessNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_springStiffness = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_springStiffness(value);
    */
    private static native void setSpringStiffnessNATIVE(long addr, float value);

    public float getSpringStiffness () {
        return getSpringStiffnessNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_springStiffness;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_springStiffness();
    */
    private static native float getSpringStiffnessNATIVE(long addr);

    public void setSpringStiffnessLimited (boolean value) {
        setSpringStiffnessLimitedNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_springStiffnessLimited = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_springStiffnessLimited(value);
    */
    private static native void setSpringStiffnessLimitedNATIVE(long addr, boolean value);

    public boolean getSpringStiffnessLimited () {
        return getSpringStiffnessLimitedNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_springStiffnessLimited;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_springStiffnessLimited();
    */
    private static native boolean getSpringStiffnessLimitedNATIVE(long addr);

    public void setSpringDamping (float value) {
        setSpringDampingNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_springDamping = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_springDamping(value);
    */
    private static native void setSpringDampingNATIVE(long addr, float value);

    public float getSpringDamping () {
        return getSpringDampingNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_springDamping;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_springDamping();
    */
    private static native float getSpringDampingNATIVE(long addr);

    public void setSpringDampingLimited (boolean value) {
        setSpringDampingLimitedNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_springDampingLimited = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_springDampingLimited(value);
    */
    private static native void setSpringDampingLimitedNATIVE(long addr, boolean value);

    public boolean getSpringDampingLimited () {
        return getSpringDampingLimitedNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_springDampingLimited;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_springDampingLimited();
    */
    private static native boolean getSpringDampingLimitedNATIVE(long addr);

    public void setEquilibriumPoint (float value) {
        setEquilibriumPointNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        nativeObject->m_equilibriumPoint = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        jsObj.set_m_equilibriumPoint(value);
    */
    private static native void setEquilibriumPointNATIVE(long addr, float value);

    public float getEquilibriumPoint () {
        return getEquilibriumPointNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btRotationalLimitMotor2* nativeObject = (btRotationalLimitMotor2*)addr;
        return nativeObject->m_equilibriumPoint;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRotationalLimitMotor2);
        return jsObj.get_m_equilibriumPoint();
    */
    private static native float getEquilibriumPointNATIVE(long addr);

}
