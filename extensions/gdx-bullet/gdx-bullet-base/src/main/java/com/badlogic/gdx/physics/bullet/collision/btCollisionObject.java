package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btCollisionObject extends BulletBase {

    public btCollisionObject() {
    }

    public void setRestitution(float rest) {
        setRestitution(cPointer, rest);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        jsObj.setRestitution(rest);
     */
    private static native int setRestitution(long addr, float rest);
}