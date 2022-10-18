package com.badlogic.gdx.physics.box2d.cpp;

public class b2ChainShape extends b2Shape {

    public b2ChainShape() {
        initObject(createNative(), true);
    }

    public b2ChainShape(boolean cMemoryOwn) {
        initObject(0, false);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = new Box2D.b2ChainShape();
        return Box2D.getPointer(jsObj);
    */
    private static native long createNative();

    public native b2Vec2 GetVertex(int index);

    public native int GetVertexCount();
}