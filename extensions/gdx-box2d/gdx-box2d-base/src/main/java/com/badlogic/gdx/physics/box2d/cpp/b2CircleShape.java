package com.badlogic.gdx.physics.box2d.cpp;

public class b2CircleShape extends b2Shape {

    public b2CircleShape() {
        initObject(createNative(), true);
    }

    public b2CircleShape(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2CircleShape();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public native b2Vec2 m_p();

    public native void m_p(b2Vec2 m_p);
}