package com.badlogic.gdx.physics.box2d.cpp;

public class b2EdgeShape extends b2Shape {

    public b2EdgeShape() {
        initObject(createNative(), true);
    }

    public b2EdgeShape(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2EdgeShape();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public void set(float x1, float y1, float x2, float y2) {
        b2Vec2.TMP_01.Set(x1, y1);
        b2Vec2.TMP_02.Set(x2, y2);
        Set(b2Vec2.TMP_01, b2Vec2.TMP_02);
    }

    public native void Set(b2Vec2 v1, b2Vec2 v2);

    public native b2Vec2 m_vertex0();

    public native void m_vertex0(b2Vec2 m_vertex0);

    public native b2Vec2 m_vertex1();

    public native void m_vertex1(b2Vec2 m_vertex1);

    public native b2Vec2 m_vertex2();

    public native void m_vertex2(b2Vec2 m_vertex2);

    public native b2Vec2 m_vertex3();

    public native void m_vertex3(b2Vec2 m_vertex3);
}