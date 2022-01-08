package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btBoxShape extends btPolyhedralConvexShape {

    public btBoxShape(Vector3 boxHalfExtents) {
        btVector3 out = btVector3.TEMP_01;
        btVector3.convert(boxHalfExtents, out);
        initObject(createNative(out.getCPointer()), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var boxHalfExtentsJSObj = Bullet.wrapPointer(boxHalfExtentsAddr, Bullet.btVector3);
        var jsObj = new Bullet.btBoxShape(boxHalfExtentsJSObj);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long boxHalfExtentsAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btBoxShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public void calculateLocalInertia(float mass, Vector3 inertia) {
        btVector3 out = btVector3.TEMP_01;
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