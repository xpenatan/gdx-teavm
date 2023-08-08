package com.github.xpenatan.gdx.examples.bullet;

import com.badlogic.gdx.math.Matrix4;
import bullet.linearmath.btMotionState;

public class TestMotionState extends btMotionState {
    private Matrix4 transform;

    public TestMotionState(Matrix4 transform) {
        this.transform = transform;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        transform.set(worldTrans);
    }
}
