package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btConvexHullShape extends btPolyhedralConvexAabbCachingShape {

    public btConvexHullShape (java.nio.FloatBuffer points, int numPoints, int stride) {
        // Custom constructor from GDX
        initObject(createNative(), true);
        int remaining = points.limit();
        stride = stride/4;
        int i = 0;
        while (i < remaining) {
            float x = points.get(i);
            float y = points.get(i + 1);
            float z = points.get(i + 2);
            btVector3.TEMP_0.setValue(x, y, z);
            addPoint(btVector3.TEMP_0);
            i += stride;
        }
    }

    public btConvexHullShape (btShapeHull hull) {
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

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConvexHullShape();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

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
}