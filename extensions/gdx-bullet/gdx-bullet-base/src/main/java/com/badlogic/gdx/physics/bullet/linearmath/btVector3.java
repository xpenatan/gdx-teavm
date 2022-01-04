package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btVector3 extends BulletBase {

    public btVector3() {
        this(true);
    }

    public btVector3(float x, float y, float z) {
        super("btVector3");
        initObject(createObj(x, y, z), true);
    }

    /** Useful on creating temp objects */
    public btVector3(boolean cMemoryOwn) {
        super("btVector3");
        initObject(cMemoryOwn ? createObj(0, 0, 0) : 0, cMemoryOwn);
    }

    /*[-teaVM;-REPLACE]
    private int createObj(float x, float y, float z) {
        int pointer = createNative(x, y, z);
        jsObj = getNativeObject(pointer);
        return pointer;
    }
     */
    private long createObj(float x, float y, float z) {
        return createNative(x, y, z);
    }

    /*[-teaVM;-ADD]
    @org.teavm.jso.JSBody(params = {"addr"}, script = "return Bullet.wrapPointer(addr, Bullet.btVector3);")
    private static native org.teavm.jso.JSObject getNativeObject(int addr);
     */

    /*[-C++;-NATIVE]
        return (jlong)new btVector3(x,y,z);
     */
    /*[-teaVM;-NATIVE]
        var vec3 = new Bullet.btVector3(x,y,z);
        return Bullet.getPointer(vec3);
     */
    private static native long createNative(float x, float y, float z);

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

    public float x() {
        return getX(cPointer);
    }

    public float y() {
        return getY(cPointer);
    }

    public float z() {
        return getZ(cPointer);
    }
}