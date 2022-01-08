package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btCollisionWorld extends BulletBase {

    public btCollisionWorld() {
        initObject(createNative(), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btCollisionWorld();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}