package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btSphereShape extends btConvexInternalShape {

    public btSphereShape (float radius) {
        initObject(createNative(radius), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btSphereShape(radius);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(float radius);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btSphereShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}