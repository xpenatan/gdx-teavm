package com.badlogic.gdx.physics.bullet.dynamics;

/**
 * @author xpenatan
 */
public class btSequentialImpulseConstraintSolver extends btConstraintSolver {

    /*[-C++;-NATIVE]
        #include "btBulletDynamicsCommon.h"
    */

    public btSequentialImpulseConstraintSolver() {
        initObject(createNative(), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btDefaultCollisionConfiguration();
     */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btSequentialImpulseConstraintSolver();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btSequentialImpulseConstraintSolver);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}