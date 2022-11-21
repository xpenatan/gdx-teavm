package com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

@Emulate(IndexArray.class)
public class IndexArrayEmu extends IndexBufferObjectEmu {
    public IndexArrayEmu(int maxIndices) {
        super(maxIndices);
    }
}
