package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public abstract class btBroadphaseInterface  extends BulletBase {
    protected btBroadphaseInterface(String className) {
        super(className);
    }
}