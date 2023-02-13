package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.github.xpenatan.gdx.backends.web.gen.Emulate;

@Emulate(VertexArray.class)
public class VertexArrayEmu extends VertexBufferObject {
    public VertexArrayEmu(int numVertices, VertexAttribute... attributes) {
        this(numVertices, new VertexAttributes(attributes));
    }

    public VertexArrayEmu(int numVertices, VertexAttributes attributes) {
        super(false, numVertices, attributes);
    }
}
