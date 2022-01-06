package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btQuaternion extends BulletBase {

    public btQuaternion() {
        this(true);
    }

    /**
     * Useful on creating temp objects
     */
    public btQuaternion(boolean cMemoryOwn) {
        super("btVector3");
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
}