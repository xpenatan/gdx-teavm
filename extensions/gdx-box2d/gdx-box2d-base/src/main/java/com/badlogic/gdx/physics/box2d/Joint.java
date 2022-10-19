package com.badlogic.gdx.physics.box2d;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.cpp.b2Joint;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class Joint<T extends b2Joint> {

    protected T b2Joint;

    private final World world;
    private Object userData;
    protected JointEdge jointEdgeA;
    protected JointEdge jointEdgeB;
    private final Vector2 anchorA = new Vector2();
    private final Vector2 anchorB = new Vector2();

    protected Joint(World world) {
        this.world = world;
    }

    public Body getBodyA() {
        return world.bodies.get(b2Joint.GetBodyA().getCPointer());
    }

    /**
     * Get the second body attached to this joint.
     */
    public Body getBodyB() {
        return world.bodies.get(b2Joint.GetBodyB().getCPointer());
    }

    public Vector2 getAnchorA() {
        b2Vec2 b2Vec2 = b2Joint.GetAnchorA();
        anchorA.x = b2Vec2.x();
        anchorA.y = b2Vec2.y();
        return anchorA;
    }

    public Vector2 getAnchorB() {
        b2Vec2 b2Vec2 = b2Joint.GetAnchorB();
        anchorB.x = b2Vec2.x();
        anchorB.y = b2Vec2.y();
        return anchorB;
    }

    public JointDef.JointType getType() {
        int type = b2Joint.getType();
        if(type > 0 && type < JointDef.JointType.valueTypes.length)
            return JointDef.JointType.valueTypes[type];
        else
            return JointDef.JointType.Unknown;
    }

    public void setUserData (Object userData) {
        this.userData = userData;
    }

    public Object getUserData () {
        return userData;
    }
}