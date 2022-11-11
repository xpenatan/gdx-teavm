package com.badlogic.gdx.physics.bullet;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMathConstants;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import java.util.Arrays;

/**
 * @author xpenatan
 */
public class Bullet {

    public final static int VERSION = LinearMathConstants.BT_BULLET_VERSION;

    protected static boolean useRefCounting = false;
    protected static boolean enableLogging = true;

    private static boolean bulletInit = false;

    public static void init() {
        init(false);
    }

    public static void init(boolean useRefCounting) {
        init(useRefCounting, true);
    }

    public static void init(boolean useRefCounting, boolean logging) {
        if(Bullet.bulletInit)
            return;
        Bullet.bulletInit = true;
        Bullet.useRefCounting = useRefCounting;
        Bullet.enableLogging = logging;
        final int version = LinearMath.btGetVersion();
        if(version != VERSION)
            throw new GdxRuntimeException("Bullet binaries version (" + version + ") does not match source version (" + VERSION
                    + ")");
    }

    /**
     * Dispose temp objects
     */
    public static void dispose() {
        btVector3.TEMP_0.dispose();
        btVector3.TEMP_1.dispose();
        btVector3.TEMP_2.dispose();
        btVector3.TEMP_3.dispose();
        btVector3.TEMP_4.dispose();
        btTransform.TEMP_0.dispose();
        btTransform.TEMP_1.dispose();
        btTransform.TEMP_2.dispose();
        btTransform.TEMP_3.dispose();
        btTransform.TEMP_4.dispose();
    }

    protected static class ShapePart {
        public Array<MeshPart> parts = new Array<MeshPart>();
        public Matrix4 transform = new Matrix4();
    }

    private final static Pool<ShapePart> shapePartPool = new Pool<ShapePart>() {
        @Override
        protected ShapePart newObject() {
            return new ShapePart();
        }
    };
    private final static Array<ShapePart> shapePartArray = new Array<ShapePart>();

    private final static Matrix4 idt = new Matrix4();
    private final static Matrix4 tmpM = new Matrix4();

    public static void getShapeParts(final Node node, final boolean applyTransform, final Array<ShapePart> out, final int offset,
                                     final Pool<ShapePart> pool) {
        final Matrix4 transform = applyTransform ? node.localTransform : idt;
        if(node.parts.size > 0) {
            ShapePart part = null;
            for(int i = offset, n = out.size; i < n; i++) {
                final ShapePart p = out.get(i);
                if(Arrays.equals(p.transform.val, transform.val)) {
                    part = p;
                    break;
                }
            }
            if(part == null) {
                part = pool.obtain();
                part.parts.clear();
                part.transform.set(transform);
                out.add(part);
            }
            for(int i = 0, n = node.parts.size; i < n; i++)
                part.parts.add(node.parts.get(i).meshPart);
        }
        if(node.hasChildren()) {
            final boolean transformed = applyTransform && !Arrays.equals(transform.val, idt.val);
            final int o = transformed ? out.size : offset;
            getShapeParts(node.getChildren(), out, o, pool);
            if(transformed) {
                for(int i = o, n = out.size; i < n; i++) {
                    final ShapePart part = out.get(i);
                    tmpM.set(part.transform);
                    part.transform.set(transform).mul(tmpM);
                }
            }
        }
    }

    public static <T extends Node> void getShapeParts(final Iterable<T> nodes, final Array<ShapePart> out, final int offset,
                                                      final Pool<ShapePart> pool) {
        for(T node : nodes)
            getShapeParts(node, true, out, offset, pool);
    }

    public static btCollisionShape obtainStaticNodeShape(final Node node, final boolean applyTransform) {
        getShapeParts(node, applyTransform, shapePartArray, 0, shapePartPool);
        btCollisionShape result = obtainStaticShape(shapePartArray);
        shapePartPool.freeAll(shapePartArray);
        shapePartArray.clear();
        return result;
    }

    /**
     * Obtain a {@link btCollisionShape} based on the specified nodes, which can be used for a static body but not for a dynamic
     * body. Depending on the specified nodes the result will be either a {@link com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape} or a
     * {@link com.badlogic.gdx.physics.bullet.collision.btCompoundShape} of multiple btBvhTriangleMeshShape's. Where possible, the same btBvhTriangleMeshShape will be reused
     * if multiple nodes use the same (mesh) part. The node transformation (translation and rotation) will be included, but scaling
     * will be ignored.
     *
     * @param nodes The nodes for which to obtain a node, typically this would be: `model.nodes`.
     * @return The obtained shape, if you're using reference counting then you can release the shape when no longer needed.
     */
    public static btCollisionShape obtainStaticNodeShape(final Array<Node> nodes) {
        getShapeParts(nodes, shapePartArray, 0, shapePartPool);
        btCollisionShape result = obtainStaticShape(shapePartArray);
        shapePartPool.freeAll(shapePartArray);
        shapePartArray.clear();
        return result;
    }

    public static btCollisionShape obtainStaticShape(final Array<ShapePart> parts) {
        if(parts.size == 0) return null;
        if(parts.size == 1 && Arrays.equals(parts.get(0).transform.val, idt.val))
            return btBvhTriangleMeshShape.obtain(parts.get(0).parts);
        btCompoundShape result = new btCompoundShape();
        result.obtain();
        for(int i = 0, n = parts.size; i < n; i++) {
            final btBvhTriangleMeshShape shape = btBvhTriangleMeshShape.obtain(parts.get(i).parts);
            //TODO
            result.addChildShape(parts.get(i).transform, shape);
            shape.release();
        }
        return result;
    }
}
