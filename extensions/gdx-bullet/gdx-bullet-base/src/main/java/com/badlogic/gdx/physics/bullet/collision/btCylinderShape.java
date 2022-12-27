package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btCylinderShape extends btConvexInternalShape {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btCylinderShape(Vector3 halfExtents) {
        btVector3 out = btVector3.TEMP_0;
        btVector3.convert(halfExtents, out);
        initObject(createNative(out.getCPointer()), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var boxHalfExtentsJSObj = Bullet.wrapPointer(halfExtentsAddr, Bullet.btVector3);
        var jsObj = new Bullet.btCylinderShape(boxHalfExtentsJSObj);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long halfExtentsAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCylinderShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}