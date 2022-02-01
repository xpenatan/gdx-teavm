package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class PHY_ScalarType {
    public final static int PHY_FLOAT = 0;
    public final static int PHY_DOUBLE = PHY_FLOAT + 1;
    public final static int PHY_INTEGER = PHY_DOUBLE + 1;
    public final static int PHY_SHORT = PHY_INTEGER + 1;
    public final static int PHY_FIXEDPOINT88 = PHY_SHORT + 1;
    public final static int PHY_UCHAR = PHY_FIXEDPOINT88 + 1;

    public PHY_ScalarType() {
    }
}