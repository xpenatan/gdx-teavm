package com.github.xpenatan.gdx.backends.teavm.dom.impl;

import com.github.xpenatan.gdx.backends.teavm.soundmanager.SoundManagerCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaSoundManager {
    @JSBody(script = "var soundManager = new SoundManager();" +
            "soundManager.debugMode = false;" +
            "return soundManager;")
    private static native JSObject createSM();

    @JSBody(params = {"soundManager", "url", "callback"}, script = "" +
            "soundManager.setup({" +
            "url: url," +
            "onready: function() { callback.onready(); }," +
            "ontimeout: function(status) { callback.ontimeout(); }" +
            "});")
    private static native void setupJS(JSObject soundManager, String url, SoundManagerCallback callbackk);

    @JSBody(params = {"soundManager", "url"}, script = "return soundManager.createSound({url: url});")
    private static native JSObject createSoundJS(JSObject soundManager, String url);

    private JSObject soundManagerJS;

    public TeaSoundManager() {
        soundManagerJS = createSM();
    }

    public TeaSMSound createSound(String url) {
        JSObject soundJS = createSoundJS(soundManagerJS, url);
        return new TeaSMSound(soundJS);
    }

    public void setup(String url, SoundManagerCallback callback) {
        setupJS(soundManagerJS, url, callback);
    }
}
