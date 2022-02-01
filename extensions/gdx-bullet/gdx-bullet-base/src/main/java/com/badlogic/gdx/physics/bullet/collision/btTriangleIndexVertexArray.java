package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.utils.Array;

/**
 * @author xpenatan
 */
public class btTriangleIndexVertexArray extends btStridingMeshInterface {

    protected final static Array<btTriangleIndexVertexArray> instances = new Array<btTriangleIndexVertexArray>();
    /** @return Whether the supplied array contains all specified tags. */
    public static <T extends Object> boolean compare (final btTriangleIndexVertexArray array, final Array<T> tags) {
        if (array.meshes.size != tags.size) return false;
        for (final btIndexedMesh mesh : array.meshes) {
            boolean found = false;
            final Object tag = mesh.tag;
            if (tag == null) return false;
            for (final T t : tags) {
                if (t.equals(tag)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    protected static <T extends Object> btTriangleIndexVertexArray getInstance (final Array<T> tags) {
        for (final btTriangleIndexVertexArray instance : instances) {
            if (compare(instance, tags)) return instance;
        }
        return null;
    }

    /** Create or reuse a btTriangleIndexVertexArray instance based on the specified {@link MeshPart} array. Use {@link #release()}
     * to release the mesh when it's no longer needed. */
    public static <T extends MeshPart> btTriangleIndexVertexArray obtain (final Array<T> meshParts) {
        btTriangleIndexVertexArray result = getInstance(meshParts);
        if (result == null) {
            result = new btTriangleIndexVertexArray(meshParts);
            instances.add(result);
        }
        result.obtain();
        return result;
    }

    protected final Array<btIndexedMesh> meshes = new Array<btIndexedMesh>(1);

    public btTriangleIndexVertexArray(final MeshPart meshPart) {
        // Gdx constructor
        initObject(createNative(), true);
        addMeshPart(meshPart);
    }

    public <T extends MeshPart> btTriangleIndexVertexArray (final Iterable<T> meshParts) {
        // Gdx constructor
        initObject(createNative(), true);
        addMeshParts(meshParts);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btTriangleIndexVertexArray();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTriangleIndexVertexArray);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public void addIndexedMesh(btIndexedMesh mesh, int indexType) {
        addIndexedMeshNATIVE(cPointer, mesh.getCPointer(), indexType);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTriangleIndexVertexArray);
        var meshJSObj = Bullet.wrapPointer(meshAddr, Bullet.btIndexedMesh);
        jsObj.addIndexedMesh(meshJSObj, indexType);
     */
    private static native void addIndexedMeshNATIVE(long addr, long meshAddr, int indexType);

    public void addIndexedMesh(btIndexedMesh mesh) {
        addIndexedMeshNATIVE(cPointer, mesh.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTriangleIndexVertexArray);
        var meshJSObj = Bullet.wrapPointer(meshAddr, Bullet.btIndexedMesh);
        jsObj.addIndexedMesh(meshJSObj);
     */
    private static native void addIndexedMeshNATIVE(long addr, long meshAddr);

    /** Add a {@link MeshPart} instance to this btTriangleIndexVertexArray.
     * The specified mesh must be indexed and triangulated and must outlive this btTriangleIndexVertexArray.
     * The buffers for the vertices and indices are shared amongst both. */
    public btTriangleIndexVertexArray addMeshPart(final MeshPart meshPart) {
        btIndexedMesh mesh = btIndexedMesh.obtain(meshPart);
        addIndexedMesh(mesh, PHY_ScalarType.PHY_SHORT);
        mesh.release();
        return this;
    }

    /** Add one or more {@link MeshPart} instances to this btTriangleIndexVertexArray.
     * The specified meshes must be indexed and triangulated and must outlive this btTriangleIndexVertexArray.
     * The buffers for the vertices and indices are shared amongst both. */
    public <T extends MeshPart> btTriangleIndexVertexArray addMeshParts(final Iterable<T> meshParts) {
        for (final MeshPart meshPart : meshParts)
            addMeshPart(meshPart);
        return this;
    }

}