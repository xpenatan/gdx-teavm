package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btBroadphaseProxy extends BulletBase {

    protected btBroadphaseProxy(String className) {
        super(className);
    }

    @Override
    protected void deleteNative() {
    }
}