package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btBoxShape extends btPolyhedralConvexShape {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btBoxShape(Vector3 boxHalfExtents) {
        btVector3 out = btVector3.TEMP_0;
        btVector3.convert(boxHalfExtents, out);
        initObject(createNative(out.getCPointer()), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var boxHalfExtentsJSObj = Bullet.wrapPointer(boxHalfExtentsAddr, Bullet.btVector3);
        var jsObj = new Bullet.btBoxShape(boxHalfExtentsJSObj);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long boxHalfExtentsAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btBoxShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}