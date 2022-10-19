package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.cpp.b2PulleyJoint;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class PulleyJoint extends Joint<b2PulleyJoint> {

    private final Vector2 groundAnchorA = new Vector2();
    private final Vector2 groundAnchorB = new Vector2();

    public PulleyJoint(World world, long addr) {
        super(world);
        b2Joint = new b2PulleyJoint();
        b2Joint.setPointer(addr);
    }

    public Vector2 getGroundAnchorA () {
        b2Vec2 b2Vec2 = b2Joint.GetGroundAnchorA();
        groundAnchorA.set(b2Vec2.x(), b2Vec2.y());
        return groundAnchorA;
    }
    public Vector2 getGroundAnchorB () {
        b2Vec2 b2Vec2 = b2Joint.GetGroundAnchorB();
        groundAnchorB.set(b2Vec2.x(), b2Vec2.y());
        return groundAnchorB;
    }
}
