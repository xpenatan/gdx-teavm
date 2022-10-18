package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2Body;
import com.badlogic.gdx.physics.box2d.cpp.b2Transform;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;
import com.badlogic.gdx.utils.Array;

public class Body {

    private final Transform transform = new Transform(false);

    private World world;

    public b2Body b2Body;

    private Object userData;

    private Vector2 position = new Vector2();

    private final Vector2 linearVelocity = new Vector2();

    /**
     * Fixtures of this body
     **/
    private Array<Fixture> fixtures = new Array<Fixture>(2);

    protected Body(World world, long addr) {
        this.world = world;
        b2Body = new b2Body();
        b2Body.setPointer(addr);
    }

    protected void reset(long addr) {
        b2Body.setPointer(addr);
        this.userData = null;
    }

    /**
     * Get the user data
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * Set the user data
     */
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * Get the type of this body.
     */
    public BodyDef.BodyType getType() {
        int type = b2Body.getType();
        if(type == 0) return BodyDef.BodyType.StaticBody;
        if(type == 1) return BodyDef.BodyType.KinematicBody;
        if(type == 2) return BodyDef.BodyType.DynamicBody;
        return BodyDef.BodyType.StaticBody;
    }

    public Array<Fixture> getFixtureList() {
        return fixtures;
    }

    public Fixture createFixture(FixtureDef def) {
        long fixtureAddr = b2Body.createFixture(def.shape.b2Shape, def.friction, def.restitution, def.density, def.isSensor,
                def.filter.categoryBits, def.filter.maskBits, def.filter.groupIndex);
        Fixture fixture = this.world.freeFixtures.obtain();
        fixture.reset(this, fixtureAddr);
        this.world.fixtures.put(fixture.b2Fixture.getCPointer(), fixture);
        this.fixtures.add(fixture);
        return fixture;
    }

    public Fixture createFixture(Shape shape, float density) {
        long fixtureAddr = b2Body.createFixture(shape.b2Shape, density);
        Fixture fixture = this.world.freeFixtures.obtain();
        fixture.reset(this, fixtureAddr);
        this.world.fixtures.put(fixture.b2Fixture.getCPointer(), fixture);
        this.fixtures.add(fixture);
        return fixture;
    }

    public void destroyFixture(Fixture fixture) {
        this.world.destroyFixture(this, fixture);
        fixture.setUserData(null);
        this.world.fixtures.remove(fixture.b2Fixture.getCPointer());
        this.fixtures.removeValue(fixture, true);
        this.world.freeFixtures.free(fixture);
    }

    public boolean isActive() {
        return b2Body.IsActive();
    }

    public boolean isAwake() {
        return b2Body.IsAwake();
    }

    public Transform getTransform() {
        b2Transform b2Transform = b2Body.GetTransform();
        transform.setAddr(b2Transform.cPointer);
        return transform;
    }

    public Vector2 getPosition() {
        b2Vec2 b2Vec2 = b2Body.GetPosition();
        position.set(b2Vec2.x(), b2Vec2.y());
        return position;
    }

    public Vector2 getLinearVelocity() {
        b2Vec2 b2Vec2 = b2Body.GetLinearVelocity();
        linearVelocity.set(b2Vec2.x(), b2Vec2.y());
        return linearVelocity;
    }
}