package com.github.xpenatan.gdx.backends.teavm.webaudio.howler;

import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;

@JSClass
public class Howl implements JSObject {

    @JSBody(params = { "arrayBufferView"}, script = "" +
            "var blob = new Blob( [ arrayBufferView ]);" +
            "var howlSource = URL.createObjectURL(blob);" +
            "return new Howl({ src: [howlSource], format: ['ogg', 'webm', 'mp3', 'wav']});")
    public static native Howl create(ArrayBufferViewWrapper arrayBufferView);

    public native int play();

    public native int play(int soundId);

    public native void stop(int soundId);

    public native void pause(int soundId);

    @JSMethod("rate")
    public native void setRate(float rate, int soundId);

    @JSMethod("rate")
    public native float getRate(int soundId);

    @JSMethod("volume")
    public native void setVolume(float volume, int soundId);

    @JSMethod("volume")
    public native float getVolume(int soundId);

    @JSMethod("mute")
    public native void setMute(boolean mute, int soundId);

    @JSMethod("mute")
    public native boolean getMute(int soundId);

    @JSMethod("seek")
    public native void setSeek(float seek, int soundId);

    @JSMethod("seek")
    public native float getSeek(int soundId);

    @JSMethod("duration")
    public native int getDuration(int spriteId);

    @JSMethod("duration")
    public native float getDuration();

    @JSMethod("loop")
    public native void setLoop (boolean loop , int soundId);

    @JSMethod("loop")
    public native boolean getLoop(int soundId);

    @JSMethod("playing")
    public native boolean isPlaying(int soundId);

    @JSMethod("pannerAttr")
    public native void setPannerAttr(HowlPannerAttr pannerAttr, int soundId);

    @JSMethod("pannerAttr")
    public native HowlPannerAttr getPannerAttr(int soundId);

    @JSMethod("stereo")
    public native void setStereo(float pan, int soundId);

    @JSMethod("stereo")
    public native float getStereo(int soundId);

    // ##### Globals

    public native void stop();

    public native void unload();

    public native void pause();

    @JSMethod("volume")
    public native void setVolume(float volume);

    @JSMethod("volume")
    public native float getVolume();

    public native float on(String event, HowlEventFunction function, int soundId);

    public native float on(String event, HowlEventFunction function);

    @JSMethod("stereo")
    public native void setStereo(float pan);

    @JSMethod("loop")
    public native void setLoop (boolean loop);

    @JSMethod("loop")
    public native boolean getLoop();

    @JSMethod("playing")
    public native boolean isPlaying();

    @JSMethod("seek")
    public native void setSeek(float seek);

    @JSMethod("seek")
    public native float getSeek();
}