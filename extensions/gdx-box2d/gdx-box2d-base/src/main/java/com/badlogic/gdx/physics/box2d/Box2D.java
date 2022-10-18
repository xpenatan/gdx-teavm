package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

/**
 * @author xpenatan
 */
public class Box2D {

    public final static int VERSION = 231;

    protected static boolean useRefCounting = false;
    protected static boolean enableLogging = true;

    private static boolean box2DInit = false;

    public static void init() {
        init(false);
    }

    public static void init(boolean useRefCounting) {
        init(useRefCounting, true);
    }

    public static void init(boolean useRefCounting, boolean logging) {
        if(Box2D.box2DInit)
            return;
        Box2D.box2DInit = true;
        Box2D.useRefCounting = useRefCounting;
        Box2D.enableLogging = logging;
    }

    /**
     * Dispose temp objects
     */
    public static void dispose() {
        b2Vec2.TMP_01.dispose();
        b2Vec2.TMP_02.dispose();
    }
}