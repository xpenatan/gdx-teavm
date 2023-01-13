package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.collision.PHY_ScalarType;
import com.badlogic.gdx.physics.bullet.collision.btIndexedMesh;

/**
 * @author xpenatan
 */
public class btMatrix3x3 extends BulletBase {

    /*[-C++;-NATIVE]
        #include "LinearMath/btMatrix3x3.h"
    */

    public btMatrix3x3() {
        this(true);
    }

    /**
     * Useful on creating temp objects
     */
    public btMatrix3x3(boolean cMemoryOwn) {
        initObject(cMemoryOwn ? createNative() : 0, cMemoryOwn);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btMatrix3x3();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btMatrix3x3();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btMatrix3x3*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btMatrix3x3);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    /*[-IDL_SKIP]
     */
    public static native btVector3 getRow(int i);

    /*[-IDL_SKIP]
     */
    public static native btVector3 getColumn(int i);
}