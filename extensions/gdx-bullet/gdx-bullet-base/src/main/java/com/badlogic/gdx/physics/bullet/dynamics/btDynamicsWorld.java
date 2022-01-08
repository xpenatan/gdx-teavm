package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btDynamicsWorld  extends btCollisionWorld {

    public void setGravity(Vector3 gravity) {
        btVector3 out = btVector3.TEMP_01;
        btVector3.convert(gravity, out);
    }

    /*[-teaVM;NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDynamicsWorld);
        var vec3JSObj = Bullet.wrapPointer(vec3Addr, Bullet.btVector3);
        jsObj.setGravity(vec3JSObj);
     */
    private static native void setGravity(long addr, long vec3Addr);


    public int stepSimulation(float timeStep, int maxSubSteps, float fixedTimeStep) {
        return stepSimulation(cPointer, timeStep, maxSubSteps, fixedTimeStep);
    }

    public int stepSimulation(float timeStep, int maxSubSteps) {
        return stepSimulation(cPointer, timeStep, maxSubSteps, 1.0f/60.0f);
    }

    public int stepSimulation(float timeStep) {
        return stepSimulation(cPointer, timeStep, 1, 1.0f/60.0f);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDynamicsWorld);
        jsObj.stepSimulation(timeStep, maxSubSteps, fixedTimeStep);
     */
    private static native int stepSimulation(long addr, float timeStep, int maxSubSteps, float fixedTimeStep);

    public void addRigidBody(btRigidBody body) {
        addRigidBody(cPointer, body.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDynamicsWorld);
        var rigidBodyJSObj = Bullet.wrapPointer(rigidBodyAddr, Bullet.btRigidBody);
        jsObj.addRigidBody(rigidBodyJSObj);
     */
    private static native int addRigidBody(long addr, long rigidBodyAddr);

    public void removeRigidBody(btRigidBody body) {
        removeRigidBody(cPointer, body.getCPointer());
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.btDynamicsWorld);
        var rigidBodyJSObj = Bullet.wrapPointer(rigidBodyAddr, Bullet.btRigidBody);
        jsObj.removeRigidBody(rigidBodyJSObj);
     */
    private static native int removeRigidBody(long addr, long rigidBodyAddr);

}