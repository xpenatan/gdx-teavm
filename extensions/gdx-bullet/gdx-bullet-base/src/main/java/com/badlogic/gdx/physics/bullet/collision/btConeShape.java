package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btConeShape extends btConvexInternalShape {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btConeShape(float radius, float height) {
        initObject(createNative(radius, height), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConeShape(radius, height);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(float radius, float height);

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btConeShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}