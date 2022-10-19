package com.badlogic.gdx.physics.box2d.joints;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.cpp.b2MouseJoint;
import com.badlogic.gdx.physics.box2d.cpp.b2Vec2;

public class MouseJoint extends Joint<b2MouseJoint> {

    public MouseJoint(World world, long addr) {
        super(world);
        b2Joint = new b2MouseJoint();
        b2Joint.setPointer(addr);
    }

    public void setTarget (Vector2 target) {
        b2Vec2.TMP_01.Set(target.x, target.y);
        b2Joint.SetTarget(b2Vec2.TMP_01);
    }
}
