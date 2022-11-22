package com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

@Emulate(VertexArray.class)
public class VertexArrayEmu extends VertexBufferObjectEmu {
    public VertexArrayEmu(int numVertices, VertexAttribute... attributes) {
        this(numVertices, new VertexAttributes(attributes));
    }

    public VertexArrayEmu(int numVertices, VertexAttributes attributes) {
        super(false, numVertices, attributes);
    }
}