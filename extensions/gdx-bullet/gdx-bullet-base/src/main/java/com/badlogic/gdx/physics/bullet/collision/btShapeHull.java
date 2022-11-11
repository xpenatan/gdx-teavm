package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btShapeHull extends BulletBase {

    public btShapeHull(btConvexShape shape) {
        initObject(createNative(shape.getCPointer()), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btShapeHull(shapeAddr);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long shapeAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btShapeHull);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}