package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btDefaultCollisionConfiguration extends btCollisionConfiguration {

    public btDefaultCollisionConfiguration() {
        super("btDefaultCollisionConfiguration");
        initObject(createNative(), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDefaultCollisionConfiguration);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    /*[-C++;-NATIVE]
        return (jlong)new btDefaultCollisionConfiguration();
     */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btDefaultCollisionConfiguration();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();
}