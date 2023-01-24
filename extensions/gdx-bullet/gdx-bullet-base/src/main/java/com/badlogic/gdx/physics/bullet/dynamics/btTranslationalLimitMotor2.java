package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

public class btTranslationalLimitMotor2 extends BulletBase {

    private boolean [] tempBoolean = new boolean[3];
    private byte [] tempByte = new byte[3];

    /*[-C++;-NATIVE]
        #include "BulletDynamics/ConstraintSolver/btGeneric6DofSpring2Constraint.h"
    */

    protected btTranslationalLimitMotor2() {
    }

    public void setLowerLimit(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setLowerLimitNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_lowerLimit = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_lowerLimit(valueAddr);
    */
    private static native void setLowerLimitNATIVE(long addr, long valueAddr);

    public Vector3 getLowerLimit() {
        btVector3.convert(getLowerLimitNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_lowerLimit;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_lowerLimit());
    */
    private static native long getLowerLimitNATIVE(long addr);

    public void setUpperLimit(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setUpperLimitNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_upperLimit = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_upperLimit(valueAddr);
    */
    private static native void setUpperLimitNATIVE(long addr, long valueAddr);

    public Vector3 getUpperLimit() {
        btVector3.convert(getUpperLimitNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_upperLimit;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_upperLimit());
    */
    private static native long getUpperLimitNATIVE(long addr);

    public void setBounce(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setBounceNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_bounce = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_bounce(valueAddr);
    */
    private static native void setBounceNATIVE(long addr, long valueAddr);

    public Vector3 getBounce() {
        btVector3.convert(getBounceNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_bounce;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_bounce());
    */
    private static native long getBounceNATIVE(long addr);

    public void setStopERP(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setStopERPNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_stopERP = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_stopERP(valueAddr);
    */
    private static native void setStopERPNATIVE(long addr, long valueAddr);

    public Vector3 getStopERP() {
        btVector3.convert(getStopERPNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_stopERP;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_stopERP());
    */
    private static native long getStopERPNATIVE(long addr);

    public void setStopCFM(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setStopCFMNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_stopCFM = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_stopCFM(valueAddr);
    */
    private static native void setStopCFMNATIVE(long addr, long valueAddr);

    public Vector3 getStopCFM() {
        btVector3.convert(getStopCFMNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_stopCFM;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_stopCFM());
    */
    private static native long getStopCFMNATIVE(long addr);

    public void setMotorERP(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setMotorERPNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_motorERP = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_motorERP(valueAddr);
    */
    private static native void setMotorERPNATIVE(long addr, long valueAddr);

    public Vector3 getMotorERP() {
        btVector3.convert(getMotorERPNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_motorERP;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_motorERP());
    */
    private static native long getMotorERPNATIVE(long addr);

    public void setMotorCFM(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setMotorCFMNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_motorCFM = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_motorCFM(valueAddr);
    */
    private static native void setMotorCFMNATIVE(long addr, long valueAddr);

    public Vector3 getMotorCFM() {
        btVector3.convert(getMotorCFMNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_motorCFM;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_motorCFM());
    */
    private static native long getMotorCFMNATIVE(long addr);

    public void setEnableMotor(boolean[] value) {
        Bullet.set(tempBoolean, tempByte);
        setEnableMotorNATIVE(getCPointer(), tempByte);
    }

    //TODO test javascript enable motor

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_enableMotor[0] = value[0];
        nativeObject->m_enableMotor[1] = value[1];
        nativeObject->m_enableMotor[2] = value[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var array = jsObj.get_m_enableMotor();
        array[0] = value[0];
        array[1] = value[1];
        array[2] = value[2];
    */
    private static native void setEnableMotorNATIVE(long addr, byte[] value);

    public boolean[] getEnableMotor() {
        getEnableMotorNATIVE(getCPointer(), tempByte);
        Bullet.set(tempByte, tempBoolean);
        return tempBoolean;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        value[0] = nativeObject->m_enableMotor[0];
        value[1] = nativeObject->m_enableMotor[1];
        value[2] = nativeObject->m_enableMotor[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var boolArray = jsObj.get_m_enableMotor();
        value[0] = boolArray[0];
        value[1] = boolArray[1];
        value[2] = boolArray[2];
    */
    private static native void getEnableMotorNATIVE(long addr, byte[] value);

    public void setServoMotor(boolean[] value) {
        Bullet.set(value, tempByte);
        setServoMotorNATIVE(getCPointer(), tempByte);
    }

    //TODO test javascript enable motor

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_servoMotor[0] = value[0];
        nativeObject->m_servoMotor[1] = value[1];
        nativeObject->m_servoMotor[2] = value[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var array = jsObj.get_m_servoMotor();
        array[0] = value[0];
        array[1] = value[1];
        array[2] = value[2];
    */
    private static native void setServoMotorNATIVE(long addr, byte[] value);

    public boolean[] getServoMotor() {
        getServoMotorNATIVE(getCPointer(), tempByte);
        Bullet.set(tempByte, tempBoolean);
        return tempBoolean;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        value[0] = nativeObject->m_servoMotor[0];
        value[1] = nativeObject->m_servoMotor[1];
        value[2] = nativeObject->m_servoMotor[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var boolArray = jsObj.get_m_servoMotor();
        value[0] = boolArray[0];
        value[1] = boolArray[1];
        value[2] = boolArray[2];
    */
    private static native void getServoMotorNATIVE(long addr, byte[] value);

    public void setEnableSpring(boolean[] value) {
        Bullet.set(value, tempByte);
        setEnableSpringNATIVE(getCPointer(), tempByte);
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_enableSpring[0] = value[0];
        nativeObject->m_enableSpring[1] = value[1];
        nativeObject->m_enableSpring[2] = value[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var array = jsObj.get_m_enableSpring();
        array[0] = value[0];
        array[1] = value[1];
        array[2] = value[2];
    */
    private static native void setEnableSpringNATIVE(long addr, byte[] value);

    public boolean[] getEnableSpring() {
        getEnableSpringNATIVE(getCPointer(), tempByte);
        Bullet.set(tempByte, tempBoolean);
        return tempBoolean;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        value[0] = nativeObject->m_enableSpring[0];
        value[1] = nativeObject->m_enableSpring[1];
        value[2] = nativeObject->m_enableSpring[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var boolArray = jsObj.get_m_enableSpring();
        value[0] = boolArray[0];
        value[1] = boolArray[1];
        value[2] = boolArray[2];
    */
    private static native void getEnableSpringNATIVE(long addr, byte[] value);

    public void setServoTarget(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setServoTargetNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_servoTarget = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_servoTarget(valueAddr);
    */
    private static native void setServoTargetNATIVE(long addr, long valueAddr);

    public Vector3 getServoTarget() {
        btVector3.convert(getServoTargetNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_servoTarget;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_servoTarget());
    */
    private static native long getServoTargetNATIVE(long addr);

    public void setSpringStiffness(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setSpringStiffnessNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_springStiffness = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_springStiffness(valueAddr);
    */
    private static native void setSpringStiffnessNATIVE(long addr, long valueAddr);

    public Vector3 getSpringStiffness() {
        btVector3.convert(getSpringStiffnessTargetNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_springStiffness;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_springStiffness());
    */
    private static native long getSpringStiffnessTargetNATIVE(long addr);

    public void setSpringStiffnessLimited(boolean[] value) {
        Bullet.set(value, tempByte);
        setSpringStiffnessLimitedNATIVE(getCPointer(), tempByte);
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_springStiffnessLimited[0] = value[0];
        nativeObject->m_springStiffnessLimited[1] = value[1];
        nativeObject->m_springStiffnessLimited[2] = value[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var array = jsObj.get_m_springStiffnessLimited();
        array[0] = value[0];
        array[1] = value[1];
        array[2] = value[2];
    */
    private static native void setSpringStiffnessLimitedNATIVE(long addr, byte[] value);

    public boolean[] getSpringStiffnessLimited() {
        getSpringStiffnessLimitedNATIVE(getCPointer(), tempByte);
        Bullet.set(tempByte, tempBoolean);
        return tempBoolean;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        value[0] = nativeObject->m_springStiffnessLimited[0];
        value[1] = nativeObject->m_springStiffnessLimited[1];
        value[2] = nativeObject->m_springStiffnessLimited[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var boolArray = jsObj.get_m_springStiffnessLimited();
        value[0] = boolArray[0];
        value[1] = boolArray[1];
        value[2] = boolArray[2];
    */
    private static native void getSpringStiffnessLimitedNATIVE(long addr, byte[] value);

    public void setSpringDamping(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setSpringDampingNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_springDamping = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_springDamping(valueAddr);
    */
    private static native void setSpringDampingNATIVE(long addr, long valueAddr);

    public Vector3 getSpringDamping() {
        btVector3.convert(getSpringDampingNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_springDamping;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_springDamping());
    */
    private static native long getSpringDampingNATIVE(long addr);

    public void setSpringDampingLimited(boolean[] value) {
        Bullet.set(value, tempByte);
        setSpringDampingLimitedNATIVE(getCPointer(), tempByte);
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_springDampingLimited[0] = value[0];
        nativeObject->m_springDampingLimited[1] = value[1];
        nativeObject->m_springDampingLimited[2] = value[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var array = jsObj.get_m_springDampingLimited();
        array[0] = value[0];
        array[1] = value[1];
        array[2] = value[2];
    */
    private static native void setSpringDampingLimitedNATIVE(long addr, byte[] value);

    public boolean[] getSpringDampingLimited() {
        getSpringDampingLimitedNATIVE(getCPointer(), tempByte);
        Bullet.set(tempByte, tempBoolean);
        return tempBoolean;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        value[0] = nativeObject->m_springDampingLimited[0];
        value[1] = nativeObject->m_springDampingLimited[1];
        value[2] = nativeObject->m_springDampingLimited[2];
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        var boolArray = jsObj.get_m_springDampingLimited();
        value[0] = boolArray[0];
        value[1] = boolArray[1];
        value[2] = boolArray[2];
    */
    private static native void getSpringDampingLimitedNATIVE(long addr, byte[] value);

    public void setEquilibriumPoint(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setEquilibriumPointNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_equilibriumPoint = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_equilibriumPoint(valueAddr);
    */
    private static native void setEquilibriumPointNATIVE(long addr, long valueAddr);

    public Vector3 getEquilibriumPoint() {
        btVector3.convert(getEquilibriumPointNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_equilibriumPoint;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_equilibriumPoint());
    */
    private static native long getEquilibriumPointNATIVE(long addr);

    public void setTargetVelocity(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setTargetVelocityNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_targetVelocity = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_targetVelocity(valueAddr);
    */
    private static native void setTargetVelocityNATIVE(long addr, long valueAddr);

    public Vector3 getTargetVelocity() {
        btVector3.convert(getTargetVelocityNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_targetVelocity;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_targetVelocity());
    */
    private static native long getTargetVelocityNATIVE(long addr);

    public void setMaxMotorForce(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_0);
        setMaxMotorForceNATIVE(getCPointer(), btVector3.TEMP_0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        nativeObject->m_maxMotorForce = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        jsObj.set_m_maxMotorForce(valueAddr);
    */
    private static native void setMaxMotorForceNATIVE(long addr, long valueAddr);

    public Vector3 getMaxMotorForce() {
        btVector3.convert(getMaxMotorForceNATIVE(getCPointer()), btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTranslationalLimitMotor2* nativeObject = (btTranslationalLimitMotor2*)addr;
        return (jlong)&nativeObject->m_maxMotorForce;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTranslationalLimitMotor2);
        return Bullet.getPointer(jsObj.get_m_maxMotorForce());
    */
    private static native long getMaxMotorForceNATIVE(long addr);
}
