package com.badlogic.gdx.graphics.glutils;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(IndexArray.class)
public class IndexArrayEmu extends IndexBufferObject {
    public IndexArrayEmu(int maxIndices) {
        super(maxIndices);
    }
}
