package com.github.xpenatan.gdx.backends.teavm.dom.impl;

import com.github.xpenatan.gdx.backend.web.soundmanager.SMSoundWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SoundManagerCallbackWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SoundManagerWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaSoundManager implements SoundManagerWrapper {
    @JSBody(script = "var soundManager = new SoundManager();" +
            "soundManager.debugMode = false;" +
            "return soundManager;")
    private static native JSObject createSM();

    @JSBody(params = { "soundManager", "url", "callback" }, script = "" +
            "soundManager.setup({" +
            "url: url," +
            "onready: function() { callback.onready(); }," +
            "ontimeout: function(status) { callback.ontimeout(); }" +
            "});")
    private static native void setupJS(JSObject soundManager, String url, SoundManagerCallbackWrapper callbackk);

    @JSBody(params = { "soundManager", "url" }, script = "return soundManager.createSound({url: url});")
    private static native JSObject createSoundJS(JSObject soundManager, String url);

    private JSObject soundManagerJS;

    public TeaSoundManager() {
        soundManagerJS = createSM();
    }

    @Override
    public SMSoundWrapper createSound(String url) {
        JSObject soundJS = createSoundJS(soundManagerJS, url);
        return new TeaSMSoundWrapper(soundJS);
    }

    @Override
    public void setup(String url, SoundManagerCallbackWrapper callback) {
        setupJS(soundManagerJS, url, callback);
    }
}
