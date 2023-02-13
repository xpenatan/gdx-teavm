package com.badlogic.gdx.graphics.glutils;

import com.github.xpenatan.gdx.backends.web.gen.Emulate;

@Emulate(IndexArray.class)
public class IndexArrayEmu extends IndexBufferObject {
    public IndexArrayEmu(int maxIndices) {
        super(maxIndices);
    }
}
