package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class ContactResultCallback extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public float addSingleResult(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0, int index0, btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
        return addSingleResultNATIVE(cPointer, (int) cp.getCPointer(), (int) colObj0Wrap.getCPointer(), partId0, index0, (int) colObj1Wrap.getCPointer(), partId1, index1);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::ContactResultCallback* nativeObject = (btCollisionWorld::ContactResultCallback*)addr;
        btManifoldPoint * manifold = (btManifoldPoint*)cpAddr;
        return nativeObject->addSingleResult(*manifold, (btCollisionObjectWrapper*)colObj0WrapAddr, partId0, index0, (btCollisionObjectWrapper*)colObj1WrapAddr, partId1, index1);
    */
    private static native float addSingleResultNATIVE(long addr, long cpAddr, long colObj0WrapAddr, int partId0, int index0, long colObj1WrapAddr, int partId1, int index1);

    public boolean needsCollision(btBroadphaseProxy proxy0) {
        return needsCollisionNATIVE(cPointer, (int) proxy0.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::ContactResultCallback* nativeObject = (btCollisionWorld::ContactResultCallback*)addr;
        return nativeObject->needsCollision((btBroadphaseProxy*)proxy0Addr);
    */
    private static native boolean needsCollisionNATIVE(long addr, long proxy0Addr);
}