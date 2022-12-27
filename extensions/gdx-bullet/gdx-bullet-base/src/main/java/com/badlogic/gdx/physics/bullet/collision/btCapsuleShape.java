package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btCapsuleShape extends btConvexInternalShape {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btCapsuleShape(float radius, float height) {
        initObject(createNative(radius, height), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btCapsuleShape(radius, height);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(float radius, float height);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCapsuleShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}