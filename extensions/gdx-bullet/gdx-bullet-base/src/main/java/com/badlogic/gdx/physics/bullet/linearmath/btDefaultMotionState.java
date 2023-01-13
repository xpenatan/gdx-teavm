package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;

/**
 * @author xpenatan
 */
public class btDefaultMotionState extends btMotionState {

    /*[-C++;-NATIVE]
        #include "LinearMath/btDefaultMotionState.h"
    */

    private Matrix4 worldTrans;

    public btDefaultMotionState() {
        this(new Matrix4());
    }

    public btDefaultMotionState(Matrix4 renderTrans) {
        worldTrans = renderTrans;
    }

    @Override
    public void getWorldTransform(Matrix4 centerOfMassWorldTrans) {
        centerOfMassWorldTrans.set(this.worldTrans);
    }

    ///synchronizes world transform from physics to user
    ///Bullet only calls the update of worldtransform for active objects
    public void setWorldTransform(Matrix4 centerOfMassWorldTrans) {
        this.worldTrans.set(centerOfMassWorldTrans);
    }

    @Override
    protected void deleteNative() {

    }
}