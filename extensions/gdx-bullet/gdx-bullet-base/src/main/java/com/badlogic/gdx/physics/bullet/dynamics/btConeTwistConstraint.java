package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;

/**
 * @author xpenatan
 */
public class btConeTwistConstraint extends btTypedConstraint {

    public btConeTwistConstraint (btRigidBody rbA, btRigidBody rbB, Matrix4 rbAFrame, Matrix4 rbBFrame) {
        btTransform btframeInA = btTransform.TEMP_0;
        btTransform btframeInB = btTransform.TEMP_1;
        btTransform.convert(rbAFrame, btframeInA);
        btTransform.convert(rbBFrame, btframeInB);
        initObject(createNative(rbA.getCPointer(), rbB.getCPointer(), btframeInA.getCPointer(), btframeInB.getCPointer()), true);
    }

    public btConeTwistConstraint (btRigidBody rbA, Matrix4 rbAFrame) {
        btTransform btrbAFrame = btTransform.TEMP_0;
        btTransform.convert(rbAFrame, btrbAFrame);
        initObject(createNative(rbA.getCPointer(), btrbAFrame.getCPointer()), true);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConeTwistConstraint(rbAAddr, rbBAddr, frameInAAddr, frameInBAddr);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long rbAAddr, long rbBAddr, long frameInAAddr, long frameInBAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = new Bullet.btConeTwistConstraint(rbAAddr, rbAFrameAddr);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(long rbAAddr, long rbAFrameAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btConeTwistConstraint);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);
}