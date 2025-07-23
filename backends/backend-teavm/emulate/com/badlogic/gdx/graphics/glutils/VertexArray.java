package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

public class VertexArray extends VertexBufferObject {
    public VertexArray(int numVertices, VertexAttribute... attributes) {
        this(numVertices, new VertexAttributes(attributes));
    }

    public VertexArray(int numVertices, VertexAttributes attributes) {
        super(false, numVertices, attributes);
    }
}
