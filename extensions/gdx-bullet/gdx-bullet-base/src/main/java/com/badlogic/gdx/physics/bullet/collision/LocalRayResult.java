package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class LocalRayResult extends BulletBase {

    public static LocalRayResult temp01 = new LocalRayResult();

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public LocalRayResult() {
        initObject(0, false);
    }

    public btCollisionObject getCollisionObject() {
        btCollisionObject.temp01.setPointer(getCollisionObject(cPointer));
        return btCollisionObject.temp01;
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::LocalRayResult* nativeObject = (btCollisionWorld::LocalRayResult*)addr;
        return (jlong)nativeObject->m_collisionObject;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.LocalRayResult);
        var colJSObj = jsObj.get_m_collisionObject();
        return Bullet.getPointer(colJSObj);
     */
    private static native long getCollisionObject(long addr);

    public btVector3 getHitNormalLocal() {
        btVector3.emptyTransform.setPointer(getHitNormalLocal(cPointer));
        return btVector3.emptyTransform;
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::LocalRayResult* nativeObject = (btCollisionWorld::LocalRayResult*)addr;
        return (jlong)&nativeObject->m_hitNormalLocal;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.LocalRayResult);
        var vec3JSObj = jsObj.get_m_hitNormalLocal();
        return Bullet.getPointer(vec3JSObj);
     */
    private static native long getHitNormalLocal(long addr);

    public void setHitFraction(float value) {
        setHitFraction(cPointer, value);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::LocalRayResult* nativeObject = (btCollisionWorld::LocalRayResult*)addr;
        nativeObject->m_hitFraction = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.LocalRayResult);
        jsObj.set_m_hitFraction(value);
     */
    private static native void setHitFraction(long addr, float value);

    public float getHitFraction() {
        return getHitFraction(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::LocalRayResult* nativeObject = (btCollisionWorld::LocalRayResult*)addr;
        return nativeObject->m_hitFraction;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.LocalRayResult);
        return jsObj.get_m_hitFraction();
     */
    private static native float getHitFraction(long addr);
}