package com.badlogic.gdx.physics.bullet;

import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
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
        public Array<MeshPart> parts = new Array<>();
        public Matrix4 transform = new Matrix4();
    }

    private final static Pool<ShapePart> shapePartPool = new Pool<ShapePart>() {
        @Override
        protected ShapePart newObject() {
            return new ShapePart();
        }
    };
    private final static Array<ShapePart> shapePartArray = new Array<>();

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
}