package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btVector3 extends BulletBase {

    /*[-C++;-NATIVE]
        #include "LinearMath/btVector3.h"
    */

    public static btVector3 WRAPPER_GEN_01 = new btVector3(false);

    public static btVector3 TEMP_0 = new btVector3(true);
    public static btVector3 TEMP_1 = new btVector3(true);
    public static btVector3 TEMP_2 = new btVector3(true);
    public static btVector3 TEMP_3 = new btVector3(true);
    public static btVector3 TEMP_4 = new btVector3(true);

    public static btVector3 emptyTransform = new btVector3(false);

    public static Vector3 TEMP_GDX_01 = new Vector3();
    public static Vector3 TEMP_GDX_02 = new Vector3();
    public static Vector3 TEMP_GDX_03 = new Vector3();

    public btVector3() {
        this(true);
    }

    public btVector3(float x, float y, float z) {
        initObject(createNative(x, y, z), true);
    }

    /**
     * Useful on creating temp objects
     */
    public btVector3(boolean cMemoryOwn) {
        initObject(cMemoryOwn ? createNative(0, 0, 0) : 0, cMemoryOwn);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btVector3(x,y,z);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btVector3(x,y,z);
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(float x, float y, float z);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btVector3*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btVector3);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public native float getX();

    public native float getY();

    public native float getZ();

    public native void setValue(float x, float y, float z);

    public static void convert(Vector3 in, btVector3 out) {
        out.setValue(in.x, in.y, in.z);
    }

    public static void convert(btVector3 in, Vector3 out) {
        float x = in.getX();
        float y = in.getY();
        float z = in.getZ();
        out.set(x, y, z);
    }

    public static void convert(Vector3 in, long outAddr) {
        emptyTransform.setPointer(outAddr);
        convert(in, emptyTransform);
    }

    public static void convert(long inAddr, Vector3 out) {
        emptyTransform.setPointer(inAddr);
        convert(emptyTransform, out);
    }
}