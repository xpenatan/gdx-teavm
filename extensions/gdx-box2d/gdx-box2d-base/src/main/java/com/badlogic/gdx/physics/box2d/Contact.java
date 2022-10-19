package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.physics.box2d.cpp.b2Contact;
import com.badlogic.gdx.physics.box2d.cpp.b2Fixture;
import com.badlogic.gdx.physics.box2d.cpp.b2Manifold;

public class Contact {

    protected b2Contact b2Contact;

    protected World world;
    protected final WorldManifold worldManifold = new WorldManifold();

    protected Contact (World world, long addr) {
        this.world = world;
        b2Contact = new b2Contact();
        b2Contact.setPointer(addr);
    }

    public WorldManifold getWorldManifold () {
        b2Contact.GetWorldManifold(worldManifold.b2WorldManifold);
        b2Manifold b2Manifold = b2Contact.GetManifold();
        worldManifold.numContactPoints = b2Manifold.pointCount();
        return worldManifold;
    }

    /** Get the first fixture in this contact. */
    public Fixture getFixtureA () {
        b2Fixture b2Fixture = b2Contact.GetFixtureA();
        return world.fixtures.get(b2Fixture.getCPointer());
    }

    /** Get the second fixture in this contact. */
    public Fixture getFixtureB () {
        b2Fixture b2Fixture = b2Contact.GetFixtureB();
        return world.fixtures.get(b2Fixture.getCPointer());
    }
}