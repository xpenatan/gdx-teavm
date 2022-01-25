package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @author xpenatan
 */
public class btIndexedMesh extends BulletBase {
    //TODO need to finish impl

    protected final static Array<btIndexedMesh> instances = new Array<btIndexedMesh>();
    protected static btIndexedMesh getInstance(final Object tag) {
        final int n = instances.size;
        for (int i = 0; i < n; i++) {
            final btIndexedMesh mesh = instances.get(i);
            if (tag.equals(mesh.tag))
                return mesh;
        }
        return null;
    }

    /** Create or reuse a btIndexedMesh instance based on the specified {@link com.badlogic.gdx.graphics.g3d.model.MeshPart}.
     * Use {@link #release()} to release the mesh when it's no longer needed. */
    public static btIndexedMesh obtain(final MeshPart meshPart) {
        if (meshPart == null)
            throw new GdxRuntimeException("meshPart cannot be null");

        btIndexedMesh result = getInstance(meshPart);
        if (result == null) {
            result = new btIndexedMesh(meshPart);
            instances.add(result);
        }
        result.obtain();
        return result;
    }


    public Object tag;

    /** Construct a new btIndexedMesh based on the supplied {@link MeshPart}
     * The specified mesh must be indexed and triangulated and must outlive this btIndexedMesh.
     * The buffers for the vertices and indices are shared amonst both. */
    public btIndexedMesh(final MeshPart meshPart) {
        initObject(createNative(), true);
        set(meshPart);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btIndexedMesh();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btIndexedMesh);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);


    /** Convenience method to set this btIndexedMesh to the specified {@link MeshPart}
     * The specified mesh must be indexed and triangulated and must outlive this btIndexedMesh.
     * The buffers for the vertices and indices are shared amonst both. */
    public void set(final MeshPart meshPart) {
        if (meshPart.primitiveType != com.badlogic.gdx.graphics.GL20.GL_TRIANGLES)
            throw new com.badlogic.gdx.utils.GdxRuntimeException("Mesh must be indexed and triangulated");
        set(meshPart, meshPart.mesh, meshPart.offset, meshPart.size);
    }

    /** Convenience method to set this btIndexedMesh to the specified {@link com.badlogic.gdx.graphics.Mesh}
     * The specified mesh must be indexed and triangulated and must outlive this btIndexedMesh.
     * The buffers for the vertices and indices are shared amonst both. */
    public void set(final Object tag, final Mesh mesh, int offset, int count) {
        if ((count <= 0) || ((count % 3) != 0))
            throw new com.badlogic.gdx.utils.GdxRuntimeException("Mesh must be indexed and triangulated");

        VertexAttribute posAttr = mesh.getVertexAttribute(Usage.Position);

        if (posAttr == null)
            throw new com.badlogic.gdx.utils.GdxRuntimeException("Mesh doesn't have a position attribute");

        set(tag, mesh.getVerticesBuffer(), mesh.getVertexSize(), mesh.getNumVertices(), posAttr.offset, mesh.getIndicesBuffer(), offset, count);
    }

    /** Convenience method to set this btIndexedMesh to the specified vertex and index data.
     * The specified data must be indexed and triangulated and must outlive this btIndexedMesh. */
    public void set(final Object tag,
                    final FloatBuffer vertices, int sizeInBytesOfEachVertex, int vertexCount, int positionOffsetInBytes,
                    final ShortBuffer indices, int indexOffset, int indexCount) {
        setVertices(vertices, sizeInBytesOfEachVertex, vertexCount, positionOffsetInBytes);
        setIndices(indices, indexOffset, indexCount);
        this.tag = tag;
    }

    public void setVertices(java.nio.FloatBuffer vertices, int sizeInBytesOfEachVertex, int vertexCount, int positionOffsetInBytes) {
        int remaining = vertices.remaining();
        float[] array = new float[remaining];
        for (int i = 0; i < remaining; i++) {
            array[i] = vertices.get(i);
        }

        setVertices(cPointer, array, sizeInBytesOfEachVertex, vertexCount, positionOffsetInBytes);
    }
    /*[-teaVM;-NATIVE]
         Bullet.MyClassHelper.prototype.setVertices(addr, vertices, sizeInBytesOfEachVertex, vertexCount, positionOffsetInBytes);
     */
    private static native void setVertices(long addr, float [] vertices, int sizeInBytesOfEachVertex, int vertexCount, int positionOffsetInBytes);

    public void setIndices(java.nio.ShortBuffer indices, int indexOffset, int indexCount) {
        int remaining = indices.remaining();
        short[] array = new short[remaining];
        for (int i = 0; i < remaining; i++) {
            array[i] = indices.get(i);
        }
        setIndices(cPointer, array, indexOffset, indexCount);
    }

    /*[-teaVM;-NATIVE]
         Bullet.MyClassHelper.prototype.setIndices(addr, indices, indexOffset, indexCount);
     */
    private static native void setIndices(long addr, short [] indices, int indexOffset, int indexCount);
}