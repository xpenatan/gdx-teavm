package com.github.xpenatan.gdx.backends.teavm.dom.impl;

import com.github.xpenatan.gdx.backends.web.soundmanager.SMSoundCallbackWrapper;
import com.github.xpenatan.gdx.backends.web.soundmanager.SMSoundOptions;
import com.github.xpenatan.gdx.backends.web.soundmanager.SMSoundWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaSMSoundWrapper implements SMSoundWrapper {

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.position;")
    private static native int getPositionJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.destruct();")
    private static native void destructJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS", "position"}, script = "return smSoundJS.setPosition(position);")
    private static native void setPositionJS(JSObject smSoundJS, int position);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.pause();")
    private static native void pauseJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.play();")
    private static native void playJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS", "volume", "pan", "loop", "from", "callback"}, script = "" +
            "return smSoundJS.play({" +
            "volume: volume," +
            "pan: pan," +
            "loop: loop," +
            "from: from," +
            "onfinish: function() { if(callback != null) callback.onfinish(); }" +
            "});")
    private static native void playJS(JSObject smSoundJS, int volume, int pan, int loop, int from, SMSoundCallbackWrapper callback);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.resume();")
    private static native void resumeJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.stop();")
    private static native void stopJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS", "volume"}, script = "return smSoundJS.setVolume(volume);")
    private static native void setVolumeJS(JSObject smSoundJS, int volume);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.volume;")
    private static native int getVolumeJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS", "pan"}, script = "return smSoundJS.setPan(pan);")
    private static native void setPanJS(JSObject smSoundJS, int pan);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.pan;")
    private static native int getPanJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.playState;")
    private static native int getPlayStateJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.paused;")
    private static native boolean getPausedJS(JSObject smSoundJS);

    @JSBody(params = {"smSoundJS"}, script = "return smSoundJS.loops;")
    private static native int getLoopsJS(JSObject smSoundJS);

    private JSObject smSoundJS;

    public TeaSMSoundWrapper(JSObject smSoundJS) {
        this.smSoundJS = smSoundJS;
    }

    @Override
    public int getPosition() {
        return getPositionJS(smSoundJS);
    }

    @Override
    public void destruct() {
        destructJS(smSoundJS);
    }

    @Override
    public void setPosition(int position) {
        setPositionJS(smSoundJS, position);
    }

    @Override
    public void pause() {
        pauseJS(smSoundJS);
    }

    @Override
    public void play(SMSoundOptions options) {
        playJS(smSoundJS, options.volume, options.pan, options.loops, options.from, options.callback);
    }

    @Override
    public void play() {
        playJS(smSoundJS);
    }

    @Override
    public void resume() {
        resumeJS(smSoundJS);
    }

    @Override
    public void stop() {
        stopJS(smSoundJS);
    }

    @Override
    public void setVolume(int volume) {
        setVolumeJS(smSoundJS, volume);
    }

    @Override
    public int getVolume() {
        return getVolumeJS(smSoundJS);
    }

    @Override
    public void setPan(int pan) {
        setPanJS(smSoundJS, pan);
    }

    @Override
    public int getPan() {
        return getPanJS(smSoundJS);
    }

    @Override
    public int getPlayState() {
        return getPlayStateJS(smSoundJS);
    }

    @Override
    public boolean getPaused() {
        return getPausedJS(smSoundJS);
    }

    @Override
    public int getLoops() {
        return getLoopsJS(smSoundJS);
    }
}
