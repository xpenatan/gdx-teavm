package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btDynamicsWorld extends btCollisionWorld {

    /*[-C++;-NATIVE]
        #include "BulletDynamics/Dynamics/btDynamicsWorld.h"
    */

    public Vector3 getGravity() {
        getGravityNATIVE(cPointer, BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btDynamicsWorld* nativeObject = (btDynamicsWorld*)addr;
        btVector3 gravity = nativeObject->getGravity();
        array[0] = gravity.getX();
        array[1] = gravity.getY();
        array[2] = gravity.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btDynamicsWorld);
        var gravity = nativeObject.getGravity();
        array[0] = gravity.getX();
        array[1] = gravity.getY();
        array[2] = gravity.getZ();
    */
    private static native void getGravityNATIVE(long addr, float [] array);

}