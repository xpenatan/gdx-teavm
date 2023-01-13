package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

public class btAxisSweep3 extends btAxisSweep3InternalShort{

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btAxisSweep3 (Vector3 worldAabbMin, Vector3 worldAabbMax, int maxHandles) {
        btVector3 btWorldAabbMin = new btVector3();
        btVector3 btWorldAabbMax = new btVector3();
        btVector3.convert(worldAabbMin, btWorldAabbMin);
        btVector3.convert(worldAabbMax, btWorldAabbMax);
        initObject(createNative(btWorldAabbMin.getCPointer(), btWorldAabbMax.getCPointer(), maxHandles), true);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btAxisSweep3(*((btVector3*)worldAabbMinAddr), *((btVector3*)worldAabbMaxAddr), maxHandles);
    */
    /*[-teaVM;-NATIVE]
        var btWorldAabbMin = Bullet.wrapPointer(worldAabbMinAddr, Bullet.btVector3);
        var btWorldAabbMax = Bullet.wrapPointer(worldAabbMaxAddr, Bullet.btVector3);
        var jsObj = new Bullet.btAxisSweep3(btWorldAabbMin, btWorldAabbMax, maxHandles);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long worldAabbMinAddr, long worldAabbMaxAddr, int maxHandles);

    /*[-C++;-NATIVE]
        delete (btAxisSweep3*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btAxisSweep3);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }
}