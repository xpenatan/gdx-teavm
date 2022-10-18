package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.VoidPtr;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

/**
 * @author xpenatan
 */
public class btCollisionObject extends BulletBase {

    public static btCollisionObject temp01 = new btCollisionObject(0);

    protected btCollisionObject(long cPointer) {
        initObject(cPointer, false);
    }

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

    public void setUserPointer(VoidPtr userPointer) {
    }

    public void setUserPointer (long userPointer) {
        setUserPointerNATIVE(cPointer, userPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        jsObj.setUserPointer(userPointer);
     */
    private static native void setUserPointerNATIVE(long addr, long userPointer);

    /*[-teaVM;-REPLACE]
    public long getUserPointer () {
        int userPointer = getUserPointer(cPointer);
        long longPointer = (long)userPointer;
        return longPointer;
    }
     */
    public long getUserPointer () {
        long userPointer = getUserPointer(cPointer);
        return userPointer;
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        var intPointer = jsObj.getUserPointer();
        return intPointer;
     */
    private static native long getUserPointer(long addr);

    public void getWorldTransform (Matrix4 out) {
        // Gdx method
        int worldTransformAddr = (int)getWorldTransformAddr(cPointer);
        btTransform.convert(worldTransformAddr, out);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject);
        var jsTransform = jsObj.getWorldTransform();
        return Bullet.getPointer(jsTransform);
     */
    private static native long getWorldTransformAddr(long addr);
}