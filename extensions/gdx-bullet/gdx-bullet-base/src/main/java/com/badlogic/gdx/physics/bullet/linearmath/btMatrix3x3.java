package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btMatrix3x3 extends BulletBase {

    public btMatrix3x3() {
        this(true);
    }

    /**
     * Useful on creating temp objects
     */
    public btMatrix3x3(boolean cMemoryOwn) {
        super("btMatrix3x3");
        initObject(cMemoryOwn ? createNative() : 0, cMemoryOwn);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btMatrix3x3();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btMatrix3x3);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}