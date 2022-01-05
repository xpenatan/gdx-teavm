package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btRigidBody extends btCollisionObject {

    public btRigidBody(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
        super("btRigidBody");
        btVector3 out = btVector3.tempWrapper01;
        btVector3.convert(localInertia, out);
        initObject(createNative(mass, motionState.getCPointer(), collisionShape.getCPointer(), out.getCPointer()), true);
    }

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var motionStateJSObj = Bullet.wrapPointer(motionStateAddr, Bullet.btMotionState);
        var collisionShapeJSObj = Bullet.wrapPointer(collisionShapeAddr, Bullet.btCollisionShape);
        var localInertiaJSObj = Bullet.wrapPointer(localInertiaAddr, Bullet.btVector3);
        var jsObj = new Bullet.btRigidBody(mass, motionStateJSObj, collisionShapeJSObj, localInertiaJSObj);
        return Bullet.getPointer(jsObj);
     */
    private static native long createNative(float mass, long motionStateAddr, long collisionShapeAddr, long localInertiaAddr);

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btRigidBody);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

}