package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class ContactResultCallback extends BulletBase {
    protected ContactResultCallback(String className) {
        super(className);
    }

    @Override
    protected void deleteNative() {

    }
}