package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;

/**
 * @author xpenatan
 */
public class btCollisionWorld extends BulletBase {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    protected final LongMap<btCollisionObject> bodies = new LongMap<>(100);

    private btCollisionObjectArray objectArray = new btCollisionObjectArray(false);

    protected btDispatcher dispatcher;

    public btCollisionWorld(btDispatcher dispatcher, btBroadphaseInterface pairCache, btCollisionConfiguration collisionConfiguration) {
        this.dispatcher = dispatcher;
        dispatcher.init(this);
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

    public void addCollisionObject(btCollisionObject collisionObject, int collisionFilterGroup, int collisionFilterMask) {
        bodies.put(collisionObject.getCPointer(), collisionObject);
        addCollisionObjectNATIVE(cPointer, collisionObject.getCPointer(), collisionFilterGroup, collisionFilterMask);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld* nativeObject = (btCollisionWorld*)addr;
        nativeObject->addCollisionObject((btCollisionObject* )collisionObjectAddr, collisionFilterGroup, collisionFilterMask);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        jsObj.addCollisionObject(collisionObjectAddr, collisionFilterGroup, collisionFilterMask);
    */
    private static native void addCollisionObjectNATIVE(long addr, long collisionObjectAddr, int collisionFilterGroup, int collisionFilterMask);

    public void addCollisionObject(btCollisionObject collisionObject, int collisionFilterGroup) {
        bodies.put(collisionObject.getCPointer(), collisionObject);
        addCollisionObjectNATIVE(cPointer, collisionObject.getCPointer(), collisionFilterGroup);
    }

    /*[-C++;-NATIVE]
        btCollisionWorld* nativeObject = (btCollisionWorld*)addr;
        nativeObject->addCollisionObject((btCollisionObject* )collisionObjectAddr, collisionFilterGroup);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        jsObj.addCollisionObject(collisionObjectAddr, collisionFilterGroup);
    */
    private static native void addCollisionObjectNATIVE(long addr, long collisionObjectAddr, int collisionFilterGroup);

    public void addCollisionObject(btCollisionObject collisionObject) {
        bodies.put(collisionObject.getCPointer(), collisionObject);
        addCollisionObjectNATIVE(cPointer, collisionObject.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionWorld* nativeObject = (btCollisionWorld*)addr;
        nativeObject->addCollisionObject((btCollisionObject* )collisionObjectAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        jsObj.addCollisionObject(collisionObjectAddr);
    */
    private static native void addCollisionObjectNATIVE(long addr, long collisionObjectAddr);

    public void removeCollisionObject(btCollisionObject collisionObject) {
        bodies.remove(collisionObject.getCPointer());
        removeCollisionObjectNATIVE(cPointer, collisionObject.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionWorld* nativeObject = (btCollisionWorld*)addr;
        nativeObject->removeCollisionObject((btCollisionObject* )collisionObjectAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        jsObj.removeCollisionObject(collisionObjectAddr);
    */
    private static native void removeCollisionObjectNATIVE(long addr, long collisionObjectAddr);

    public btCollisionObjectArray getCollisionObjectArray() {
        long pointer = getCollisionObjectArrayNATIVE(cPointer);
        objectArray.setPointer(pointer);
        objectArray.init(this);
        return objectArray;
    }

    /*[-C++;-NATIVE]
        btCollisionWorld* nativeObject = (btCollisionWorld*)addr;
        return (jlong)&nativeObject->getCollisionObjectArray();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        var returnedJSObj = jsObj.getCollisionObjectArray();
        return Bullet.getPointer(returnedJSObj);
    */
    private static native long getCollisionObjectArrayNATIVE(long addr);

    public btDispatcher getDispatcher() {
        return dispatcher;
    }

    public void rayTest(btVector3 rayFromWorld, btVector3 rayToWorld, RayResultCallback resultCallback) {
        resultCallback.init(this);
        rayTestNATIVE(cPointer, rayFromWorld.getCPointer(), rayToWorld.getCPointer(), resultCallback.getCPointer());
    }

    /*[-C++;-NATIVE]
        btCollisionWorld* nativeObject = (btCollisionWorld*)addr;
        nativeObject->rayTest(*((btVector3* )rayFromWorldAddr), *((btVector3* )rayToWorldAddr), *(( btCollisionWorld::RayResultCallback* )resultCallbackAddr));
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        jsObj.rayTest(rayFromWorldAddr, rayToWorldAddr, resultCallbackAddr);
    */
    private static native void rayTestNATIVE(long addr, long rayFromWorldAddr, long rayToWorldAddr, long resultCallbackAddr);

    public void rayTest(Vector3 rayFromWorldGDX, Vector3 rayToWorldGDX, RayResultCallback resultCallback) {
        btVector3.convert(rayFromWorldGDX, btVector3.TEMP_0);
        btVector3 rayFromWorld = btVector3.TEMP_0;
        btVector3.convert(rayToWorldGDX, btVector3.TEMP_1);
        btVector3 rayToWorld = btVector3.TEMP_1;
        rayTestNATIVE(cPointer, rayFromWorld.getCPointer(), rayToWorld.getCPointer(), resultCallback.getCPointer());
    }
}