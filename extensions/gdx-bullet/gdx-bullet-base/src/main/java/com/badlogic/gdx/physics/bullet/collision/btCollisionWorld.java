package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btCollisionWorld extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btCollisionWorld(btDispatcher dispatcher, btBroadphaseInterface pairCache, btCollisionConfiguration collisionConfiguration) {
        initObject(createNative(dispatcher.getCPointer(), pairCache.getCPointer(), collisionConfiguration.getCPointer()), true);
    }

    protected btCollisionWorld() {
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btCollisionWorld((btDispatcher*)dispatcherAddr, (btBroadphaseInterface*)pairCacheAddr, (btCollisionConfiguration*)collisionConfigurationAddr);
    */
    /*[-teaVM;-NATIVE]
        var dispatcherJSObj = Bullet.wrapPointer(dispatcherAddr, Bullet.btDispatcher);
        var broadphaceJSObj = Bullet.wrapPointer(pairCacheAddr, Bullet.btBroadphaseInterface);
        var configJSObj = Bullet.wrapPointer(collisionConfigurationAddr, Bullet.btCollisionConfiguration);
        var jsObj = new Bullet.btCollisionWorld(dispatcherJSObj, broadphaceJSObj, configJSObj);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long dispatcherAddr, long pairCacheAddr, long collisionConfigurationAddr);

    /*[-C++;-NATIVE]
        delete (btCollisionWorld*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}