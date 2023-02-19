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

    /*[-C++;-NATIVE]
        return (jlong)new btBoxShape(*((btVector3*)boxHalfExtentsAddr));
    */
    /*[-teaVM;-NATIVE]
        var boxHalfExtentsJSObj = Bullet.wrapPointer(boxHalfExtentsAddr, Bullet.btVector3);
        var jsObj = new Bullet.btBoxShape(boxHalfExtentsJSObj);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long boxHalfExtentsAddr);

    /*[-C++;-NATIVE]
        delete (btBoxShape*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btBoxShape);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public Vector3 getHalfExtentsWithoutMargin() {
        long pointer = getHalfExtentsWithoutMarginNATIVE(cPointer);
        btVector3.WRAPPER_GEN_01.setPointer(pointer);
        btVector3.convert(btVector3.WRAPPER_GEN_01, btVector3.TEMP_GDX_01);
        return btVector3.TEMP_GDX_01;
    }

    // TODO improve generator to get reference pointer

    /*[-C++;-NATIVE]
        btBoxShape* nativeObject = (btBoxShape*)addr;
        return (jlong)&nativeObject->getHalfExtentsWithoutMargin();
    */
    private static native long getHalfExtentsWithoutMarginNATIVE(long addr);
}