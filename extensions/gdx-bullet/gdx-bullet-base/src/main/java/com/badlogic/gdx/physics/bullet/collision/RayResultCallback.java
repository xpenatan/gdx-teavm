package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class RayResultCallback extends BulletBase {

    public void setCollisionObject (btCollisionObject value) {
        setCollisionObject(cPointer, value != null ? value.getCPointer() : 0);
    }

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

    public btCollisionObject getCollisionObject () {
        long collisionObjectAddr = getCollisionObject(cPointer);
        if(collisionObjectAddr != 0) {
            btCollisionObject.temp01.setPointer(collisionObjectAddr);
            return btCollisionObject.temp01;
        }
        return null;
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return Bullet.getPointer(jsObj.get_m_collisionObject());
     */
    private static native long getCollisionObject(long addr);

    public void setClosestHitFraction (float value) {
        setClosestHitFraction(cPointer, value);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_closestHitFraction(value);
     */
    private static native void setClosestHitFraction(long addr, float value);

    public float getClosestHitFraction () {
        return getClosestHitFraction(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_closestHitFraction();
     */
    private static native float getClosestHitFraction(long addr);

    public void setCollisionFilterGroup (int value) {
        setCollisionFilterGroup(cPointer, value);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_collisionFilterGroup(value);
     */
    private static native void setCollisionFilterGroup(long addr, int value);

    public int getCollisionFilterGroup () {
        return getCollisionFilterGroup(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_collisionFilterGroup();
     */
    private static native int getCollisionFilterGroup(long addr);

    public void setCollisionFilterMask (int value) {
        setCollisionFilterMask(cPointer, value);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_collisionFilterMask(value);
     */
    private static native void setCollisionFilterMask(long addr, int value);

    public int getCollisionFilterMask () {
        return getCollisionFilterMask(cPointer);
    }

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
    public void setFlags (long value) {
        setFlags(cPointer, value);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        jsObj.set_m_flags(value);
     */
    private static native void setFlags(long addr, long value);

    public long getFlags () {
        return getFlags(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback);
        return jsObj.get_m_flags();
     */
    private static native long getFlags(long addr);
}