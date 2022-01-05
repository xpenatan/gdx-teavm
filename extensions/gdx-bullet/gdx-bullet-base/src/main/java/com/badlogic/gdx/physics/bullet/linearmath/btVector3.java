package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btVector3 extends BulletBase {

    public static btVector3 tempWrapper01 = new btVector3(true);
    public static btVector3 tempWrapper02 = new btVector3(true);
    public static btVector3 tempWrapper03 = new btVector3(true);

    public btVector3() {
        this(true);
    }

    public btVector3(float x, float y, float z) {
        super("btVector3");
        initObject(createNative(x, y, z), true);
    }

    /**
     * Useful on creating temp objects
     */
    public btVector3(boolean cMemoryOwn) {
        super("btVector3");
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

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btVector3);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public float getX() {
        return getX(cPointer);
    }

    /*[-C++;-NATIVE]
        btVector3 * vec = (btVector3 *)addr;
        return vec->x();
     */
    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        return vec3.x();
     */
    private static native float getX(long addr);

    public float getY() {
        return getY(cPointer);
    }

    /*[-C++;-NATIVE]
        btVector3 * vec = (btVector3 *)addr;
        return vec->y();
     */
    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        return vec3.y();
     */
    private static native float getY(long addr);

    public float getZ() {
        return getZ(cPointer);
    }

    /*[-C++;-NATIVE]
        btVector3 * vec = (btVector3 *)addr;
        return vec->z();
    */
    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        return vec3.z();
     */
    private static native float getZ(long addr);

    public void setX(float x) {
        setX(cPointer, x);
    }

    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        vec3.setX(x);
     */
    private static native void setX(long addr, float x);

    public void setY(float y) {
        setY(cPointer, y);
    }

    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        vec3.setY(y);
     */
    private static native void setY(long addr, float y);

    public void setZ(float z) {
        setZ(cPointer, z);
    }

    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        vec3.setZ(z);
     */
    private static native void setZ(long addr, float z);

    public void setValue(float x, float y, float z) {
        setValue(cPointer, x, y, z);
    }

    /*[-teaVM;-NATIVE]
        var vec3 = Bullet.wrapPointer(addr, Bullet.btVector3);
        vec3.setValue(x, y, z);
     */
    private static native void setValue(long addr, float x, float y, float z);

    public float x() {
        return getX(cPointer);
    }

    public float y() {
        return getY(cPointer);
    }

    public float z() {
        return getZ(cPointer);
    }

    public static void convert(Vector3 in, btVector3 out) {
        out.setValue(in.x, in.y, in.z);
    }

    public static void convert(btVector3 in, Vector3 out) {
        float x = in.getX();
        float y = in.getY();
        float z = in.getZ();
        out.set(x, y, z);
    }

}