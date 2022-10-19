package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2WorldManifold;

public class WorldManifold {

    protected b2WorldManifold b2WorldManifold;
    protected int numContactPoints;
    protected final Vector2[] points = {new Vector2(), new Vector2()};

    public WorldManifold() {
        b2WorldManifold = new b2WorldManifold();
    }

    public WorldManifold(boolean cMemoryOwn) {
        b2WorldManifold = new b2WorldManifold(cMemoryOwn);
    }

    public Vector2[] getPoints () {

        return points;
    }

    public int getNumberOfContactPoints () {
        return numContactPoints;
    }
}
