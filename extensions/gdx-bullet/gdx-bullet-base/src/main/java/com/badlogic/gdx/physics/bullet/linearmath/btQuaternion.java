package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * @author xpenatan
 */
public class btQuaternion extends btQuadWord {

    public static btQuaternion TEMP_0 = new btQuaternion(true);
    public static btQuaternion TEMP_1 = new btQuaternion(true);
    public static btQuaternion TEMP_2 = new btQuaternion(true);
    public static btQuaternion TEMP_3 = new btQuaternion(true);
    public static btQuaternion TEMP_4 = new btQuaternion(true);

    public static Quaternion TEMP_GDX_01 = new Quaternion();

    public static btQuaternion emptyTransform = new btQuaternion(false);

    public btQuaternion() {
        this(true);
    }

    /**
     * Useful on creating temp objects
     */
    public btQuaternion(boolean cMemoryOwn) {
        initObject(cMemoryOwn ? createNative() : 0, cMemoryOwn);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btQuaternion();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btQuaternion);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public native void setValue(float x, float y, float z, float w);

    public static void convert(Quaternion in, btQuaternion out) {
        out.setValue(in.x, in.y, in.z, in.w);
    }

    public static void convert(btQuaternion in, Quaternion out) {
        out.x = in.x();
        out.y = in.y();
        out.z = in.z();
        out.w = in.w();
    }

    public static void convert(Quaternion in, long outAddr) {
        emptyTransform.setPointer(outAddr);
        convert(in, emptyTransform);
    }

    public static void convert(long inAddr, Quaternion out) {
        emptyTransform.setPointer(inAddr);
        convert(emptyTransform, out);
    }
}