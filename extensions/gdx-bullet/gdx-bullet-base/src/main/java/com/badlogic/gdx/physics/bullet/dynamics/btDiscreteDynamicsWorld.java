package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;

/**
 * @author xpenatan
 */
public class btDiscreteDynamicsWorld extends btDynamicsWorld {

    /*[-C++;-NATIVE]
        #include "btBulletDynamicsCommon.h"
    */

    public btDiscreteDynamicsWorld(btDispatcher dispatcher, btBroadphaseInterface pairCache, btConstraintSolver constraintSolver, btCollisionConfiguration collisionConfiguration) {
        initObject(createNative(dispatcher.getCPointer(), pairCache.getCPointer(), constraintSolver.getCPointer(), collisionConfiguration.getCPointer()), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btDiscreteDynamicsWorld((btDispatcher*)dispatcherAddr, (btBroadphaseInterface*)pairCacheAddr, (btConstraintSolver*)constraintSolverAddr, (btCollisionConfiguration*)collisionConfigurationAddr);
    */
    /*[-teaVM;-NATIVE]
        var dispatcherJSObj = Bullet.wrapPointer(dispatcherAddr, Bullet.btDispatcher);
        var broadphaceJSObj = Bullet.wrapPointer(pairCacheAddr, Bullet.btBroadphaseInterface);
        var solverJSObj = Bullet.wrapPointer(constraintSolverAddr, Bullet.btConstraintSolver);
        var configJSObj = Bullet.wrapPointer(collisionConfigurationAddr, Bullet.btCollisionConfiguration);
        var jsObj = new Bullet.btDiscreteDynamicsWorld(dispatcherJSObj, broadphaceJSObj, solverJSObj, configJSObj);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long dispatcherAddr, long pairCacheAddr, long constraintSolverAddr, long collisionConfigurationAddr);

    /*[-C++;-NATIVE]
        delete (btDiscreteDynamicsWorld*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDiscreteDynamicsWorld);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);
}