package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btDispatcher extends BulletBase {

    /*[-C++;-NATIVE]
        #include "BulletCollision/BroadphaseCollision/btDispatcher.h"
    */

    public static btDispatcher WRAPPER_GEN_01 = new btDispatcher(false);

    public btPersistentManifold manifold = new btPersistentManifold(false);

    protected btDispatcher() {
    }

    protected btDispatcher(boolean cMemoryOwn) {
    }

    public void init(btCollisionWorld world) {
        manifold.init(world);
    }

    public btPersistentManifold getManifoldByIndexInternal(int index) {
        long pointer = getManifoldByIndexInternalNATIVE(cPointer, index);
        manifold.setPointer(pointer);
        return manifold;
    }

    /*[-C++;-NATIVE]
        btDispatcher* nativeObject = (btDispatcher*)addr;
        return (jlong)nativeObject->getManifoldByIndexInternal(index);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDispatcher);
        var returnedJSObj = jsObj.getManifoldByIndexInternal(index);
        return Bullet.getPointer(returnedJSObj);
    */
    private static native long getManifoldByIndexInternalNATIVE(long addr, int index);
}