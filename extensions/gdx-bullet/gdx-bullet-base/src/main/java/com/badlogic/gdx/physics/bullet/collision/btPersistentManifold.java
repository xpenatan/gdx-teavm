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

    void init(btCollisionWorld world) {
        this.world = world;
    }

    public btCollisionObject getBody0() {
        return world.bodies.get(getBody0NATIVE(cPointer));
    }

    /*[-C++;-NATIVE]
        btPersistentManifold* nativeObject = (btPersistentManifold*)addr;
        return (jlong)nativeObject->getBody0();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btPersistentManifold);
        var returnedJSObj = jsObj.getBody0();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native long getBody0NATIVE(long addr);

    public btCollisionObject getBody1() {
        return world.bodies.get(getBody1NATIVE(cPointer));
    }

    /*[-C++;-NATIVE]
        btPersistentManifold* nativeObject = (btPersistentManifold*)addr;
        return (jlong)nativeObject->getBody1();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btPersistentManifold);
        var returnedJSObj = jsObj.getBody1();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native long getBody1NATIVE(long addr);
}