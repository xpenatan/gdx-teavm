package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btConvexHullShape extends btPolyhedralConvexAabbCachingShape {

    public btConvexHullShape (java.nio.FloatBuffer points, int numPoints, int stride) {
        // Custom constructor from GDX
        int remaining = points.remaining();
        float[] array = new float[remaining];
        for(int i = 0; i < remaining; i++) {
            array[i] = points.get(i);
        }
        initObject(createNative(array, numPoints, stride, remaining), true);
    }

    public btConvexHullShape (btShapeHull hull) {
        // Custom constructor from GDX
        initObject(createNative(hull.getCPointer()), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConvexHullShape();
        var shapeHull = Bullet.wrapPointer(btShapeHullAddr, Bullet.btShapeHull);
        var numVertices = shapeHull.numVertices();
        var vertexPointer = shapeHull.getVertexPointer();
        var i = 0;
        while(i < numVertices) {
            var btVector3 = vertexPointer[i];
            jsObj.addPoint(btVector3);
            i++;
        }
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long btShapeHullAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConvexHullShape();
        stride = stride/4;
        var tmpbtVector = new Bullet.btVector3(x,y,z);
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
    private static native long createNative(float[] points, int numPoints, int stride, int remaining);

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