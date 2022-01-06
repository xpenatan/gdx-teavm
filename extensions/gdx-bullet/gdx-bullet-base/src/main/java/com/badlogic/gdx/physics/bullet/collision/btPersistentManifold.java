package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.linearmath.btTypedObject;

/**
 * @author xpenatan
 */
public class btPersistentManifold extends btTypedObject {

    public btPersistentManifold() {
        super("btPersistentManifold");
    }

    @Override
    protected void deleteNative() {

    }
}