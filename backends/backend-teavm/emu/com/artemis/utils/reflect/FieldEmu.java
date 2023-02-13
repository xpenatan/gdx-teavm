package com.artemis.utils.reflect;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "com.artemis.utils.reflect.Field")
public final class FieldEmu extends FieldGen {
    public FieldEmu(java.lang.reflect.Field field) {
        super(field);
    }
}

