package com.badlogic.gdx.physics.box2d;

public class JointEdge {
    public final Body other;
    public final Joint joint;

    protected JointEdge(Body other, Joint joint) {
        this.other = other;
        this.joint = joint;
    }
}