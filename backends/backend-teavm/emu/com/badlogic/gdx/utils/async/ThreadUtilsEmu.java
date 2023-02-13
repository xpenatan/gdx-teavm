package com.badlogic.gdx.utils.async;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(ThreadUtils.class)
public class ThreadUtilsEmu {
    public static void yield() {
    }
}