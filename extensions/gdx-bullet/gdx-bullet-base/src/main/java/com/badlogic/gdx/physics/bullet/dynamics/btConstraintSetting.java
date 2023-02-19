package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;

public class btConstraintSetting extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletDynamicsCommon.h"
    */

    public static btConstraintSetting WRAPPER_GEN_01 = new btConstraintSetting(false);

    protected btConstraintSetting(boolean cMemoryOwn) {
    }

    public btConstraintSetting() {
        initObject(createNative(), true);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btConstraintSetting();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConstraintSetting();
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btConstraintSetting*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);


    public void setTau (float value) {
        setTauNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btConstraintSetting* nativeObject = (btConstraintSetting*)addr;
        nativeObject->m_tau = value;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        nativeObject.m_tau = value;
    */
    private static native void setTauNATIVE(long addr, float value);

    public float getTau () {
        return getTauNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btConstraintSetting* nativeObject = (btConstraintSetting*)addr;
        return nativeObject->m_tau;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        return nativeObject.m_tau;
    */
    private static native float getTauNATIVE(long addr);

    public void setDamping (float value) {
        setDampingNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btConstraintSetting* nativeObject = (btConstraintSetting*)addr;
        nativeObject->m_damping = value;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        nativeObject.m_damping = value;
    */
    private static native void setDampingNATIVE(long addr, float value);

    public float getDamping () {
        return getDampingNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btConstraintSetting* nativeObject = (btConstraintSetting*)addr;
        return nativeObject->m_damping;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        return nativeObject.m_damping;
    */
    private static native float getDampingNATIVE(long addr);

    public void setImpulseClamp (float value) {
        setImpulseClampNATIVE(getCPointer(), value);
    }

    /*[-C++;-NATIVE]
        btConstraintSetting* nativeObject = (btConstraintSetting*)addr;
        nativeObject->m_impulseClamp = value;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        nativeObject.m_impulseClamp = value;
    */
    private static native void setImpulseClampNATIVE(long addr, float value);

    public float getImpulseClamp () {
        return getImpulseClampNATIVE(getCPointer());
    }

    /*[-C++;-NATIVE]
        btConstraintSetting* nativeObject = (btConstraintSetting*)addr;
        return nativeObject->m_impulseClamp;
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConstraintSetting);
        return nativeObject.m_impulseClamp;
    */
    private static native float getImpulseClampNATIVE(long addr);
}