package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public abstract class btTypedObject extends BulletBase {

    protected btTypedObject(String className) {
        super(className);
    }
}