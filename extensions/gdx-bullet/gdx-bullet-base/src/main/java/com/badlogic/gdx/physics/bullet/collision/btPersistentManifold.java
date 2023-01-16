package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.linearmath.btTypedObject;

/**
 * @author xpenatan
 */
public class btPersistentManifold extends btTypedObject {

    /*[-C++;-NATIVE]
        #include "BulletCollision/NarrowPhaseCollision/btPersistentManifold.h"
    */

    private btCollisionWorld world;

    protected btPersistentManifold(boolean cMemoryOwn) {
    }

    public btCollisionObject getBody0() {
        int pointer = getBody0NATIVE((int) cPointer);
        return world.bodies.get(pointer);
    }

    void init(btCollisionWorld world) {
        this.world = world;
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btPersistentManifold);
        var returnedJSObj = jsObj.getBody0();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native int getBody0NATIVE(int addr);

    public btCollisionObject getBody1() {
        int pointer = getBody1NATIVE((int) cPointer);
        return world.bodies.get(pointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btPersistentManifold);
        var returnedJSObj = jsObj.getBody1();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native int getBody1NATIVE(int addr);
}