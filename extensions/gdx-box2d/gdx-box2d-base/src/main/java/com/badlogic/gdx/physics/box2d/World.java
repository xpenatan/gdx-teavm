package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2Body;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;
import com.badlogic.gdx.physics.box2d.cpp.b2World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;
import java.util.Iterator;

public class World {

    /** pool for bodies **/
    protected final Pool<Body> freeBodies = new Pool<Body>(100, 200) {
        @Override
        protected Body newObject () {
            return new Body(World.this, 0);
        }
    };

    private Vector2 gravity;
    private b2World b2World;
    /** all known bodies **/
    protected final LongMap<Body> bodies = new LongMap<Body>(100);

    public World (Vector2 gravity, boolean doSleep) {
        this.gravity = new Vector2();
        b2Vec2 b2Gravity = new b2Vec2();
        b2Gravity.x(gravity.x);
        b2Gravity.y(gravity.y);
        b2World = new b2World(b2Gravity);
    }

    public void dispose() {
        b2World.destroy();
        b2World = null;
    }

    public Vector2 getGravity() {
        b2Vec2 b2Vec2 = b2World.GetGravity();
        gravity.set(b2Vec2.x(), b2Vec2.y());
        return gravity;
    }

    public void step (float timeStep, int velocityIterations, int positionIterations) {
        b2World.Step(timeStep, velocityIterations, positionIterations);
    }

    public Body createBody (BodyDef def) {
        long addr = b2World.CreateBody(
                def.type.getValue(), def.position.x, def.position.y, def.angle, def.linearVelocity.x, def.linearVelocity.y,
                def.angularVelocity, def.linearDamping, def.angularDamping, def.allowSleep, def.awake, def.fixedRotation,
                def.bullet, def.active, def.gravityScale);
        Body body = freeBodies.obtain();
        body.reset(addr);
        this.bodies.put(body.b2Body.getCPointer(), body);
        return body;
    }

    public void destroyBody (Body body) {
        b2Body b2Body = body.b2Body;
        this.bodies.remove(b2Body.getCPointer());
        // TODO destroy joints

        b2World.DestroyBody(b2Body);
        body.setUserData(null);
        freeBodies.free(body);
    }

    public void getBodies (Array<Body> bodies) {
        bodies.clear();
        bodies.ensureCapacity(this.bodies.size);
        for (Iterator<Body> iter = this.bodies.values(); iter.hasNext();) {
            bodies.add(iter.next());
        }
    }
}