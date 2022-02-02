package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btRigidBody extends btCollisionObject {


    public btRigidBody (btRigidBodyConstructionInfo constructionInfo) {
        super(0);
        btVector3 out = new btVector3();
        btVector3.convert(constructionInfo.localInertia, out);
        initObject(createNative(constructionInfo.mass, (int)constructionInfo.motionStateAddr, (int)constructionInfo.collisionShapeAddr, out.getCPointer()), true);
    }

    public btRigidBody(float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
        super(0);
        btVector3 out = new btVector3();
        btVector3.convert(localInertia != null ? localInertia : btVector3.TEMP_GDX_01.setZero(), out);
        long motionStatePointer = motionState != null ? motionState.getCPointer() : 0;
        long shapePointer = collisionShape != null ? collisionShape.getCPointer() : 0;
        initObject(createNative(mass, motionStatePointer,shapePointer, out.getCPointer()), true);
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

    static public class btRigidBodyConstructionInfo extends BulletBase {

        public float mass;
        public long motionStateAddr;
        public long collisionShapeAddr;
        public Vector3 localInertia = new Vector3();

        public btRigidBodyConstructionInfo (float mass, btMotionState motionState, btCollisionShape collisionShape, Vector3 localInertia) {
            this.mass = mass;
            this.motionStateAddr = motionState != null ? motionState.getCPointer() : 0;
            this.collisionShapeAddr = collisionShape != null ? collisionShape.getCPointer() : 0;
            this.localInertia.set(localInertia);
        }

        public btRigidBodyConstructionInfo (float mass, btMotionState motionState, btCollisionShape collisionShape) {
            this.mass = mass;
            this.motionStateAddr = motionState != null ? motionState.getCPointer() : 0;
            this.collisionShapeAddr = collisionShape != null ? collisionShape.getCPointer() : 0;
        }
    }
}