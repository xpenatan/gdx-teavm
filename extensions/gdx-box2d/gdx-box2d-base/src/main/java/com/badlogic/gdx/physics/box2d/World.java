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

    /**
     * pool for bodies
     **/
    protected final Pool<Body> freeBodies = new Pool<Body>(100, 200) {
        @Override
        protected Body newObject() {
            return new Body(World.this, 0);
        }
    };

    protected final Pool<Fixture> freeFixtures = new Pool<Fixture>(100, 200) {
        @Override
        protected Fixture newObject() {
            return new Fixture(null, 0);
        }
    };

    private Vector2 gravity;
    private b2World b2World;

    protected final LongMap<Body> bodies = new LongMap<Body>(100);
    protected final LongMap<Fixture> fixtures = new LongMap<Fixture>(100);
    protected final LongMap<Joint> joints = new LongMap<Joint>(100);

    //Since teavm dont support long we need to replace to int array
    /*[-teaVM;-REPLACE]
        private int[] contactAddrs = new int[200];
     */
    private long[] contactAddrs = new long[200];
    private final Array<Contact> contacts = new Array<Contact>();
    private final Array<Contact> freeContacts = new Array<Contact>();

    public World(Vector2 gravity, boolean doSleep) {
        this.gravity = new Vector2();
        b2Vec2 b2Gravity = new b2Vec2();
        b2Gravity.x(gravity.x);
        b2Gravity.y(gravity.y);
        b2World = new b2World(b2Gravity);

        contacts.ensureCapacity(contactAddrs.length);
        freeContacts.ensureCapacity(contactAddrs.length);

        for (int i = 0; i < contactAddrs.length; i++)
            freeContacts.add(new Contact(this, 0));
    }

    public void dispose() {
        b2World.dispose();
        b2World = null;
    }

    public Vector2 getGravity() {
        b2Vec2 b2Vec2 = b2World.GetGravity();
        gravity.set(b2Vec2.x(), b2Vec2.y());
        return gravity;
    }

    public void step(float timeStep, int velocityIterations, int positionIterations) {
        b2World.Step(timeStep, velocityIterations, positionIterations);
    }

    public Body createBody(BodyDef def) {
        long addr = b2World.CreateBody(
                def.type.getValue(), def.position.x, def.position.y, def.angle, def.linearVelocity.x, def.linearVelocity.y,
                def.angularVelocity, def.linearDamping, def.angularDamping, def.allowSleep, def.awake, def.fixedRotation,
                def.bullet, def.active, def.gravityScale);
        Body body = freeBodies.obtain();
        body.reset(addr);
        this.bodies.put(body.b2Body.getCPointer(), body);
        return body;
    }

    public void destroyBody(Body body) {
        b2Body b2Body = body.b2Body;
        this.bodies.remove(b2Body.getCPointer());
        // TODO destroy joints

        Array<Fixture> fixtureList = body.getFixtureList();
        while(fixtureList.size > 0) {
            Fixture fixtureToDelete = fixtureList.removeIndex(0);
            fixtureToDelete.reset(null, 0);
            this.fixtures.remove(fixtureToDelete.b2Fixture.getCPointer());
            freeFixtures.free(fixtureToDelete);
        }
        b2World.DestroyBody(b2Body);
        body.setUserData(null);
        freeBodies.free(body);
    }

    public void getBodies(Array<Body> bodies) {
        bodies.clear();
        bodies.ensureCapacity(this.bodies.size);
        for(Iterator<Body> iter = this.bodies.values(); iter.hasNext(); ) {
            bodies.add(iter.next());
        }
    }

    void destroyFixture(Body body, Fixture fixture) {
        b2World.destroyFixture(body.b2Body, fixture.b2Fixture);
    }

    public void getJoints (Array<Joint> joints) {
        joints.clear();
        joints.ensureCapacity(this.joints.size);
        for (Iterator<Joint> iter = this.joints.values(); iter.hasNext();) {
            joints.add(iter.next());
        }
    }

    public void destroyJoint (Joint joint) {
        joint.setUserData(null);
        joints.remove(joint.b2Joint.getCPointer());
        joint.jointEdgeA.other.joints.removeValue(joint.jointEdgeB, true);
        joint.jointEdgeB.other.joints.removeValue(joint.jointEdgeA, true);
        b2World.destroyJoint(joint.b2Joint);
    }

    /*[-teaVM;-REPLACE]
        private void createContactAddrs(int newSize) {
            contactAddrs = new int[newSize];
        }
     */
    private void createContactAddrs(int newSize) {
        contactAddrs = new long[newSize];
    }

    public Array<Contact> getContactList () {
        int numContacts = getContactCount();
        if (numContacts > contactAddrs.length) {
            int newSize = 2 * numContacts;
            createContactAddrs(newSize);
            contacts.ensureCapacity(newSize);
            freeContacts.ensureCapacity(newSize);
        }
        if (numContacts > freeContacts.size) {
            int freeConts = freeContacts.size;
            for (int i = 0; i < numContacts - freeConts; i++)
                freeContacts.add(new Contact(this, 0));
        }
        b2World.GetContactList(contactAddrs);

        contacts.clear();
        for (int i = 0; i < numContacts; i++) {
            Contact contact = freeContacts.get(i);
            contact.b2Contact.setPointer(contactAddrs[i]);
            contacts.add(contact);
        }

        return contacts;
    }

    public int getContactCount () {
        return b2World.GetContactCount();
    }
}