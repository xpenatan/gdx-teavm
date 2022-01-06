package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btActionInterface extends BulletBase {
    protected btActionInterface(String className) {
        super(className);
    }
    @Override
    protected void deleteNative() {

    }
}