package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btTransform extends BulletBase {

    public static btTransform emptyTransform = new btTransform(false);

    protected btTransform() {
        this(true);
    }

    /**
     * Useful on creating temp objects
     */
    public btTransform(boolean cMemoryOwn) {
        super("btVector3");
        initObject(cMemoryOwn ? createNative() : 0, cMemoryOwn);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btTransform();
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTransform);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public void setFromOpenGLMatrix(float[] m) {
        setFromOpenGLMatrix(cPointer, m);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTransform);
        jsObj.setFromOpenGLMatrix(m);
     */
    private static native void setFromOpenGLMatrix(long addr, float[] m);

    public void getOpenGLMatrix(float[] m) {
        getOpenGLMatrix(cPointer, m);
    }

    /*[-teaVM;-NATIVE]
        var worldTrans = Bullet.wrapPointer(addr, Bullet.btTransform);
        var origin = worldTrans.getOrigin();
        var matrix3x3 = worldTrans.getBasis();
        var row0 = matrix3x3.getRow(0);
        var row1 = matrix3x3.getRow(1);
        var row2 = matrix3x3.getRow(2);

        m[0] = row0.x();
        m[1] = row1.x();
        m[2] = row2.x();
        m[3] = 0;
        m[4] = row0.y();
        m[5] = row1.y();
        m[6] = row2.y();
        m[7] = 0;
        m[8] = row0.z();
        m[9] = row1.z();
        m[10] = row2.z();
        m[11] = 0;
        m[12] = origin.x();
        m[13] = origin.y();
        m[14] = origin.z();
        m[15] = 1.0;
     */
    private static native void getOpenGLMatrix(long addr, float[] m);

    public static void convert(Matrix4 in, btTransform out) {
        out.setFromOpenGLMatrix(in.val);
    }

    public static void convert(btTransform in, Matrix4 out) {
        in.getOpenGLMatrix(out.val);
    }

    public static void convert(Matrix4 in, long outAddr) {
        emptyTransform.setPointer(outAddr);
        convert(in, emptyTransform);
    }

    public static void convert(long inAddr, Matrix4 out) {
        emptyTransform.setPointer(inAddr);
        convert(emptyTransform, out);
    }


}