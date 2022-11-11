package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btHingeConstraint extends btTypedConstraint {
    public btHingeConstraint(btRigidBody rbA, Vector3 pivotInA, Vector3 axisInA) {
        btVector3 btpivotInA = btVector3.TEMP_0;
        btVector3 btaxisInA = btVector3.TEMP_1;
        btVector3.convert(pivotInA, btpivotInA);
        btVector3.convert(axisInA, btaxisInA);
        initObject(createNative(rbA.getCPointer(), btpivotInA.getCPointer(), btaxisInA.getCPointer()), true);
    }

    public btHingeConstraint(btRigidBody rbA, btRigidBody rbB, Matrix4 rbAFrame, Matrix4 rbBFrame) {
        this(rbA, rbB, rbAFrame, rbBFrame, false);
    }

    public btHingeConstraint(btRigidBody rbA, btRigidBody rbB, Matrix4 rbAFrame, Matrix4 rbBFrame, boolean useReferenceFrameA) {
        btTransform btrbAFrame = btTransform.TEMP_0;
        btTransform btrbBFrame = btTransform.TEMP_1;
        btTransform.convert(rbAFrame, btrbAFrame);
        btTransform.convert(rbBFrame, btrbBFrame);
        initObject(createNative(rbA.getCPointer(), rbB.getCPointer(), btrbAFrame.getCPointer(), btrbBFrame.getCPointer(), useReferenceFrameA), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btHingeConstraint(rigidBodyAddr, pivotInAAddr, axisInAAddr);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long rigidBodyAddr, long pivotInAAddr, long axisInAAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btHingeConstraint(rbAAddr, rbBAddr, rbAFrameAddr, rbBFrameAddr, useReferenceFrameA);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long rbAAddr, long rbBAddr, long rbAFrameAddr, long rbBFrameAddr, boolean useReferenceFrameA);

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btHingeConstraint);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}