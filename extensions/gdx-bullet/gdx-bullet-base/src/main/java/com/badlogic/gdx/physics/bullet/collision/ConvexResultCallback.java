package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class ConvexResultCallback extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public boolean hasHit() {
        return hasHitNATIVE(cPointer);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld::ConvexResultCallback* nativeObject = (btCollisionWorld::ConvexResultCallback*)addr;
        return nativeObject->hasHit();
    */
    private static native boolean hasHitNATIVE(long addr);
}