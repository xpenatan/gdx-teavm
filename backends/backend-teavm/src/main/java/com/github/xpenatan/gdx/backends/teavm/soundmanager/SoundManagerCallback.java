package com.github.xpenatan.gdx.backends.teavm.soundmanager;

import org.teavm.jso.JSObject;

public interface SoundManagerCallback extends JSObject {
    public void onready();

    public void ontimeout();
}
