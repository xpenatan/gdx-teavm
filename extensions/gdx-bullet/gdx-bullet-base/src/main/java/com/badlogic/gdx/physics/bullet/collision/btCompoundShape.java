package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btCompoundShape extends btCollisionShape {

    public btCompoundShape() {
        initObject(createNative(), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btCompoundShape(radius);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btCompoundShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}