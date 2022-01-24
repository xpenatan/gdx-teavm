package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btConvexHullShape extends btPolyhedralConvexAabbCachingShape {

    public btConvexHullShape (java.nio.FloatBuffer points, int numPoints, int stride) {
        // Custom constructor from GDX
        float[] array = points.array();
        initObject(createNative(array, numPoints, stride), true);
    }

    public btConvexHullShape (btShapeHull hull) {
        // Custom constructor from GDX
        initObject(createNative(hull.getCPointer()), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConvexHullShape();
        var shapeHull = Bullet.wrapPointer(btShapeHullAddr, Bullet.btShapeHull);
        var numVertices = shapeHull.numVertices();
        var i = 0;
        while(i < numVertices) {
            var vectice = shapeHull.getVector(i);
            jsObj.addPoint(vectice);
            i++;
        }
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long btShapeHullAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConvexHullShape();
        stride = stride/4;
        var tmpbtVector = new Bullet.btVector3(x,y,z);
        var remaining = points.@java.nio.FloatBuffer::remaining()();
        var i = 0;
        while (i < remaining) {
            var x = points[i];
            var y = points[i + 1];
            var z = points[i + 2];
            tmpbtVector.setValue(x, y, z);
            jsObj.addPoint(tmpbtVector);
            i += stride;
        }
        Bullet.destroy(tmpbtVector);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(float[] points, int numPoints, int stride);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btConvexHullShape);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}