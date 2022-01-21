package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btCollisionShape extends BulletBase {

    public void calculateLocalInertia(float mass, Vector3 inertia) {
        btVector3 out = btVector3.TEMP_0;
        btVector3.convert(inertia, out);
        calculateLocalInertia(cPointer, mass, out.getCPointer());
        btVector3.convert(out, inertia);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btBoxShape);
        var inertiaJSObj = Bullet.wrapPointer(inertiaAddr, Bullet.btVector3);
        jsObj.calculateLocalInertia(mass, inertiaJSObj);
     */
    private static native void calculateLocalInertia(long addr, float mass, long inertiaAddr);
}