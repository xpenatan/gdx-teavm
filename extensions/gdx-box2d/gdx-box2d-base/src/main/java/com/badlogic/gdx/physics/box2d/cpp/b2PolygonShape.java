package com.badlogic.gdx.physics.box2d.cpp;

public class b2PolygonShape extends b2Shape {

    public b2PolygonShape() {
        initObject(createNative(), true);
    }

    public b2PolygonShape(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2PolygonShape();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public native void SetAsBox(float hx, float hy);

    public native b2Vec2 GetVertex(int index);

    public native int GetVertexCount();
}