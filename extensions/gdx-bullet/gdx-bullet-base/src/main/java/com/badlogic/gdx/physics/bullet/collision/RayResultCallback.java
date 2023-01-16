package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class RayResultCallback extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    private btCollisionWorld world;

    void init(btCollisionWorld world) {
        this.world = world;
    }

    public void setCollisionObject(btCollisionObject value) {
        setCollisionObject(cPointer, value != null ? value.getCPointer() : 0);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        nativeObject->m_collisionObject = collisionObjAddr == 0 ? NULL : (btCollisionObject*)collisionObjAddr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        if(collisionObjAddr == 0) {
            jsObj.set_m_collisionObject(0);
        }
        else {
            var jsColObj = Bullet.wrapPointer(collisionObjAddr, Bullet.btCollisionObject);
            jsObj.set_m_collisionObject(jsColObj);
        }
    */
    private static native void setCollisionObject(long addr, long collisionObjAddr);

    public btCollisionObject getCollisionObject() {
        long collisionObjectAddr = getCollisionObject(cPointer);
        if(collisionObjectAddr != 0) {
            return world.bodies.get(collisionObjectAddr);
        }
        return null;
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        return (jlong)nativeObject->m_collisionObject;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return Bullet.getPointer(jsObj.get_m_collisionObject());
    */
    private static native long getCollisionObject(long addr);

    public void setClosestHitFraction(float value) {
        setClosestHitFraction(cPointer, value);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        nativeObject->m_closestHitFraction = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_closestHitFraction(value);
    */
    private static native void setClosestHitFraction(long addr, float value);

    public float getClosestHitFraction() {
        return getClosestHitFraction(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        return nativeObject->m_closestHitFraction;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_closestHitFraction();
    */
    private static native float getClosestHitFraction(long addr);

    public void setCollisionFilterGroup(int value) {
        setCollisionFilterGroup(cPointer, value);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        nativeObject->m_collisionFilterGroup = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_collisionFilterGroup(value);
    */
    private static native void setCollisionFilterGroup(long addr, int value);

    public int getCollisionFilterGroup() {
        return getCollisionFilterGroup(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        return nativeObject->m_collisionFilterGroup;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_collisionFilterGroup();
    */
    private static native int getCollisionFilterGroup(long addr);

    public void setCollisionFilterMask(int value) {
        setCollisionFilterMask(cPointer, value);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        nativeObject->m_collisionFilterMask = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_collisionFilterMask(value);
    */
    private static native void setCollisionFilterMask(long addr, int value);

    public int getCollisionFilterMask() {
        return getCollisionFilterMask(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        return nativeObject->m_collisionFilterMask;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_collisionFilterMask();
    */
    private static native int getCollisionFilterMask(long addr);

    /*[-teaVM;-REPLACE]
        public void setFlags (long value) {
            setFlags(cPointer, (int)value);
        }
    */
    public void setFlags(long value) {
        setFlags(cPointer, value);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        nativeObject->m_flags = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_flags(value);
    */
    private static native void setFlags(long addr, long value);

    public long getFlags() {
        return getFlags(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        return nativeObject->m_flags;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_flags();
    */
    private static native long getFlags(long addr);

    public boolean hasHit() {
        return hasHitNATIVE(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        return nativeObject->hasHit();
    */
    private static native boolean hasHitNATIVE(long addr);

    public float addSingleResult(LocalRayResult rayResult, boolean normalInWorldSpace) {
        return addSingleResultNATIVE(cPointer, (int) rayResult.getCPointer(), normalInWorldSpace);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::RayResultCallback* nativeObject = (btCollisionWorld::RayResultCallback*)addr;
        btCollisionWorld::LocalRayResult * result = (btCollisionWorld::LocalRayResult*)rayResultAddr;
        return nativeObject->addSingleResult(*result, normalInWorldSpace);
    */
    private static native float addSingleResultNATIVE(long addr, long rayResultAddr, boolean normalInWorldSpace);
}