package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btSphereShape extends btConvexInternalShape {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btSphereShape(float radius) {
        initObject(createNative(radius), true);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btSphereShape(radius);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btSphereShape(radius);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(float radius);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btSphereShape*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btSphereShape);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);
}