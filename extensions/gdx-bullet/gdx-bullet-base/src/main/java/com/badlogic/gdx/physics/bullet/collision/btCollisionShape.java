package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public abstract class btCollisionShape extends BulletBase {
    protected btCollisionShape(String className) {
        super(className);
    }
}