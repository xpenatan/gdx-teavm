package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btCollisionObject extends BulletBase {
    btCollisionShape collisionShape;

    public void setCollisionShape (btCollisionShape collisionShape) {
        this.collisionShape = collisionShape;
        setCollisionShape(cPointer, collisionShape != null ? collisionShape.getCPointer() : 0);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        jsObj.setCollisionShape(collisionShapeAddr);
     */
    private static native void setCollisionShape(long addr, long collisionShapeAddr);

    public btCollisionShape getCollisionShape () {
        return collisionShape;
    }
}