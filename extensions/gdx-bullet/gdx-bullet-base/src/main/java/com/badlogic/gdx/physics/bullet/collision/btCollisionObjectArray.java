package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btCollisionObjectArray extends BulletBase {

    /*[-C++;-NATIVE]
        #include "BulletCollision/CollisionDispatch/btCollisionObject.h"
    */

    public static btCollisionObjectArray WRAPPER_GEN_01 = new btCollisionObjectArray(false);

    protected btCollisionObjectArray(boolean cMemoryOwn) {
    }

    public btCollisionObject atConst(int n) {
        //TODO impl
        return null;
    }
}