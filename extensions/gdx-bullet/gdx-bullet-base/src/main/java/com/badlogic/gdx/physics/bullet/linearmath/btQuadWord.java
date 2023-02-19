package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;

public class btQuadWord extends BulletBase {

    /*[-C++;-NATIVE]
        #include "LinearMath/btQuadWord.h"
    */

    public native float getX();
    public native float getY();
    public native float getZ();
}