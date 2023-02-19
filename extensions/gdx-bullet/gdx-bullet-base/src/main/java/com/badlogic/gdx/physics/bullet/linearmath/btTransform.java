package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btTransform extends BulletBase {

    /*[-C++;-NATIVE]
        #include "LinearMath/btTransform.h"
    */

    public static btTransform WRAPPER_GEN_01 = new btTransform(false);

    public static btTransform TEMP_0 = new btTransform(true);
    public static btTransform TEMP_1 = new btTransform(true);
    public static btTransform TEMP_2 = new btTransform(true);
    public static btTransform TEMP_3 = new btTransform(true);
    public static btTransform TEMP_4 = new btTransform(true);

    public static btTransform emptyTransform = new btTransform(false);

    public static Matrix4 TEMP_GDX_01 = new Matrix4();

    public btTransform() {
        this(true);
    }

    /**
     * Useful on creating temp objects
     */
    public btTransform(boolean cMemoryOwn) {
        initObject(cMemoryOwn ? createNative() : 0, cMemoryOwn);
    }

    /*[-C++;-NATIVE]
        return (jlong)new btTransform();
    */
    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btTransform();
        return Bullet.getPointer(jsObj);
    */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (btTransform*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTransform);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public void setFromOpenGLMatrix(float[] m) {
        setFromOpenGLMatrix(cPointer, m);
    }

    /*[-C++;-NATIVE]
        ((btTransform*)addr)->setFromOpenGLMatrix(m);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btTransform);
        jsObj.setFromOpenGLMatrix(m);
    */
    private static native void setFromOpenGLMatrix(long addr, float[] m);

    public void getOpenGLMatrix(float[] m) {
        getOpenGLMatrix(cPointer, m);
    }

    /*[-C++;-NATIVE]
        btTransform * transform = (btTransform*)addr;
        btVector3 & origin = transform->getOrigin();
        btMatrix3x3 & matrix3x3 = transform->getBasis();
        const btVector3 & row0 = matrix3x3.getRow(0);
        const btVector3 & row1 = matrix3x3.getRow(1);
        const btVector3 & row2 = matrix3x3.getRow(2);

        m[0] = row0.getX();
        m[1] = row1.getX();
        m[2] = row2.getX();
        m[3] = 0;
        m[4] = row0.getY();
        m[5] = row1.getY();
        m[6] = row2.getY();
        m[7] = 0;
        m[8] = row0.getZ();
        m[9] = row1.getZ();
        m[10] = row2.getZ();
        m[11] = 0;
        m[12] = origin.getX();
        m[13] = origin.getY();
        m[14] = origin.getZ();
        m[15] = 1.0;
    */
    /*[-teaVM;-NATIVE]
        var worldTrans = Bullet.wrapPointer(addr, Bullet.btTransform);
        var origin = worldTrans.getOrigin();
        var matrix3x3 = worldTrans.getBasis();
        var row0 = matrix3x3.getRow(0);
        var row1 = matrix3x3.getRow(1);
        var row2 = matrix3x3.getRow(2);

        m[0] = row0.getX();
        m[1] = row1.getX();
        m[2] = row2.getX();
        m[3] = 0;
        m[4] = row0.getY();
        m[5] = row1.getY();
        m[6] = row2.getY();
        m[7] = 0;
        m[8] = row0.getZ();
        m[9] = row1.getZ();
        m[10] = row2.getZ();
        m[11] = 0;
        m[12] = origin.getX();
        m[13] = origin.getY();
        m[14] = origin.getZ();
        m[15] = 1.0;
    */
    private static native void getOpenGLMatrix(long addr, float[] m);

    public Quaternion getRotation() {
        getRotationNATIVE(cPointer, BulletBase.FLOAT_4);
        btQuaternion.TEMP_GDX_01.set(BulletBase.FLOAT_4[0], BulletBase.FLOAT_4[1], BulletBase.FLOAT_4[2], BulletBase.FLOAT_4[3]);
        return btQuaternion.TEMP_GDX_01;
    }

    /*[-C++;-NATIVE]
        btTransform* nativeObject = (btTransform*)addr;
        btQuaternion quat = nativeObject->getRotation();
        array[0] = quat.getX();
        array[1] = quat.getY();
        array[2] = quat.getZ();
        array[3] = quat.getW();
    */
    /*[-teaVM;-NATIVE]
        var nativeObject = Bullet.wrapPointer(addr, Bullet.btTransform);
        var quat = nativeObject.getRotation();
        array[0] = quat.getX();
        array[1] = quat.getY();
        array[2] = quat.getZ();
        array[3] = quat.getW();
    */
    private static native void getRotationNATIVE(long addr, float [] array);

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