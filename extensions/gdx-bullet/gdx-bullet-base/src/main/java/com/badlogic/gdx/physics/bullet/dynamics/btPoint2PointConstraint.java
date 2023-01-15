package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

public class btPoint2PointConstraint extends btTypedConstraint {

    /*[-C++;-NATIVE]
        #include "btBulletDynamicsCommon.h"
    */

    public btPoint2PointConstraint (btRigidBody rbA, btRigidBody rbB, Vector3 pivotInA, Vector3 pivotInB) {
        btVector3 btPivotInA = btVector3.TEMP_0;
        btVector3 btPivotInB= btVector3.TEMP_1;
        btVector3.convert(pivotInA, btPivotInA);
        btVector3.convert(pivotInB, btPivotInB);
        initObject(createNative(rbA.getCPointer(), rbB.getCPointer(), btPivotInA.getCPointer(), btPivotInB.getCPointer()), true);
    }

    public btPoint2PointConstraint (btRigidBody rbA, Vector3 pivotInA) {
        btVector3 btPivotInA = btVector3.TEMP_0;
        btVector3.convert(pivotInA, btPivotInA);
        initObject(createNative(rbA.getCPointer(), btPivotInA.getCPointer()), true);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btPoint2PointConstraint(*((btRigidBody*)rbAAddr), *((btRigidBody*)rbBAddr), *((btVector3*)pivotInAAddr), *((btVector3*)pivotInBAddr));
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btPoint2PointConstraint(rbAAddr, rbBAddr, pivotInAAddr, pivotInBAddr);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long rbAAddr, long rbBAddr, long pivotInAAddr, long pivotInBAddr);

    /*[-C++;-NATIVE]
        return (jlong)new btPoint2PointConstraint(*((btRigidBody*)rbAAddr), *((btVector3*)pivotInAAddr));
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btPoint2PointConstraint(rbAAddr, pivotInAAddr);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long rbAAddr, long pivotInAAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btPoint2PointConstraint*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btPoint2PointConstraint);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public btConstraintSetting getSetting () {
        btConstraintSetting.WRAPPER_GEN_01.setPointer(getSettingNATIVE(getCPointer()));
        return btConstraintSetting.WRAPPER_GEN_01;
    }

    //TODO need to check if its working

    /*[-C++;-NATIVE]
        btPoint2PointConstraint* nativeObject = (btPoint2PointConstraint*)addr;
        return (jlong)&nativeObject->m_setting;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btPoint2PointConstraint);
        return Bullet.getPointer(nativeObject.get_m_setting());
    */
    private static native long getSettingNATIVE(long addr);

    public void setSetting (btConstraintSetting value) {
        setSettingNATIVE(getCPointer(), value.getCPointer());
    }

    /*[-C++;-NATIVE]
        btPoint2PointConstraint* nativeObject = (btPoint2PointConstraint*)addr;
        btConstraintSetting* settingsObject = (btConstraintSetting*)settingsAddr;
        nativeObject->m_setting = *settingsObject;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btPoint2PointConstraint);
        nativeObject.set_m_setting(settingsAddr)
    */
    private static native void setSettingNATIVE(long addr, long settingsAddr);
}