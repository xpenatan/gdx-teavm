package com.github.xpenatan.gdx.backends.teavm.dom.impl;

import com.github.xpenatan.gdx.backends.teavm.soundmanager.SMSoundCallback;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.SMSoundOptions;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class TeaSMSound {

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
    private static native void playJS(JSObject smSoundJS, int volume, int pan, int loop, int from, SMSoundCallback callback);

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

    public TeaSMSound(JSObject smSoundJS) {
        this.smSoundJS = smSoundJS;
    }

    public int getPosition() {
        return getPositionJS(smSoundJS);
    }

    public void destruct() {
        destructJS(smSoundJS);
    }

    public void setPosition(int position) {
        setPositionJS(smSoundJS, position);
    }

    public void pause() {
        pauseJS(smSoundJS);
    }

    public void play(SMSoundOptions options) {
        playJS(smSoundJS, options.volume, options.pan, options.loops, options.from, options.callback);
    }

    public void play() {
        playJS(smSoundJS);
    }

    public void resume() {
        resumeJS(smSoundJS);
    }

    public void stop() {
        stopJS(smSoundJS);
    }

    public void setVolume(int volume) {
        setVolumeJS(smSoundJS, volume);
    }

    public int getVolume() {
        return getVolumeJS(smSoundJS);
    }

    public void setPan(int pan) {
        setPanJS(smSoundJS, pan);
    }

    public int getPan() {
        return getPanJS(smSoundJS);
    }

    public int getPlayState() {
        return getPlayStateJS(smSoundJS);
    }

    public boolean getPaused() {
        return getPausedJS(smSoundJS);
    }

    public int getLoops() {
        return getLoopsJS(smSoundJS);
    }
}
