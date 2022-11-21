package com.github.xpenatan.gdx.backends.web.emu.com.badlogic.gdx.utils.async;

import com.badlogic.gdx.utils.async.ThreadUtils;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;

@Emulate(ThreadUtils.class)
public class ThreadUtilsEmu {
    public static void yield() {
    }
}