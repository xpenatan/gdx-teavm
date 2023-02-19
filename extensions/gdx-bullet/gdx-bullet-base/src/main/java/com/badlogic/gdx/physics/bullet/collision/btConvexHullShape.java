package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btConvexHullShape extends btPolyhedralConvexAabbCachingShape {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    public btConvexHullShape(java.nio.FloatBuffer points, int numPoints, int stride) {
        // Custom constructor from GDX
        initObject(createNative(), true);
        int remaining = points.limit();
        stride = stride / 4;
        int i = 0;
        while(i < remaining) {
            float x = points.get(i);
            float y = points.get(i + 1);
            float z = points.get(i + 2);
            btVector3.TEMP_0.setValue(x, y, z);
            addPoint(btVector3.TEMP_0);
            i += stride;
        }
    }

    public btConvexHullShape(btShapeHull hull) {
        // Custom constructor from GDX
        initObject(createNative(hull.getCPointer()), true);
    }

    /*[-teaVM;-NATIVE]
        var shapeHull = Bullet.wrapPointer(btShapeHullAddr, Bullet.btShapeHull);
        var jsObj = new Bullet.btConvexHullShape();
        var numVertices = shapeHull.numVertices();
        var i = 0;
        while(i < numVertices) {
            var vec3 = Bullet.MyClassHelper.prototype.getVertexPointer(shapeHull, i);
            jsObj.addPoint(vec3);
            i++;
        }
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative(long btShapeHullAddr);

    /*[-C++;-NATIVE]
        return (jlong)new btConvexHullShape();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConvexHullShape();
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btConvexHullShape*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btConvexHullShape);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public void addPoint(btVector3 point) {
        addPointNATIVE(cPointer, point.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btConvexHullShape);
        jsObj.addPoint(btVector3Addr);
    */
    private static native void addPointNATIVE(long addr, long btVector3Addr);

    public Vector3 getScaledPoint(int i) {
        getScaledPointNATIVE(cPointer, i, BulletBase.FLOAT_4);
        btVector3.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2]);
        return btVector3.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btConvexHullShape* nativeObject = (btConvexHullShape*)addr;
        btVector3 vec3 = nativeObject->getScaledPoint(i);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btConvexHullShape);
        var vec3 = nativeObject.getScaledPoint(i);
        array[0] = vec3.getX();
        array[1] = vec3.getY();
        array[2] = vec3.getZ();
    */
    private static native void getScaledPointNATIVE(long addr, int i, float [] array);
}