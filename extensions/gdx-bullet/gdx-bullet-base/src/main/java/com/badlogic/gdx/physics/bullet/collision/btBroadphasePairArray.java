package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btBroadphasePairArray extends BulletBase {
    /*[-C++;-NATIVE]
        #include "BulletCollision/BroadphaseCollision/btOverlappingPairCache.h"
    */

    public btBroadphasePair at(int n) {
        long pointer = atNATIVE(cPointer, n);
        btBroadphasePair.WRAPPER_GEN_01.setPointer(pointer);
        return btBroadphasePair.WRAPPER_GEN_01;
    }

    // TODO improve generator to get reference pointer

    /*[-C++;-NATIVE]
        btBroadphasePairArray* nativeObject = (btBroadphasePairArray*)addr;
        return (jlong)&nativeObject->at(n);
    */
    private static native long atNATIVE(long addr, int n);
}