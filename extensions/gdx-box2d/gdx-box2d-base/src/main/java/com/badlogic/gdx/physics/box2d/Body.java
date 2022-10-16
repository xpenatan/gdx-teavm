package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.physics.box2d.cpp.b2Body;

public class Body {

    private World world;

    public b2Body b2Body;

    private Object userData;

    protected Body(World world, long addr) {
        this.world = world;
        b2Body = new b2Body();
        b2Body.setPointer(addr);
    }

    protected void reset(long addr) {
        b2Body.setPointer(addr);
        this.userData = null;
    }

    /** Get the user data */
    public Object getUserData () {
        return userData;
    }

    /** Set the user data */
    public void setUserData (Object userData) {
        this.userData = userData;
    }

    /** Get the type of this body. */
    public BodyDef.BodyType getType () {
        int type = b2Body.getType();
        if (type == 0) return BodyDef.BodyType.StaticBody;
        if (type == 1) return BodyDef.BodyType.KinematicBody;
        if (type == 2) return BodyDef.BodyType.DynamicBody;
        return BodyDef.BodyType.StaticBody;
    }
}