package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.audio.Audio;
import com.github.xpenatan.gdx.backends.teavm.dom.audio.HTMLAudioElementWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class WebAudioAPIManager implements LifecycleListener {
    private final JSObject audioContext;
    private final JSObject globalVolumeNode;
    private final AudioControlGraphPool audioControlGraphPool;
    private static boolean soundUnlocked;

    public WebAudioAPIManager() {
        this.audioContext = createAudioContextJSNI();
        this.globalVolumeNode = createGlobalVolumeNodeJSNI(audioContext);
        this.audioControlGraphPool = new AudioControlGraphPool(audioContext, globalVolumeNode);

        // for automatically muting/unmuting on pause/resume
        Gdx.app.addLifecycleListener(this);

        /*
         * The Web Audio API is blocked on many platforms until the developer triggers the first sound playback using the API. But
         * it MUST happen as a direct result of a few specific input events. This is a major point of confusion for developers new
         * to the platform. Here we attach event listeners to the graphics canvas in order to unlock the sound system on the first
         * input event. On the event, we play a silent sample, which should unlock the sound - on platforms where it is not
         * necessary the effect should not be noticeable (i.e. we play silence). As soon as the attempt to unlock has been
         * performed, we remove all the event listeners.
         */
        if(isAudioContextLocked(audioContext)) {
            UnlockFunction unlockFunction = new UnlockFunction() {
                @Override
                public void unlockFunction() {
                    setUnlocked();
                }
            };
            hookUpSoundUnlockers(audioContext, unlockFunction);
        }
        else
            setUnlocked();
    }

    @JSFunctor
    public interface UnlockFunction extends JSObject {
        void unlockFunction();
    }

    @JSBody(params = { "audioContext", "unlockFunction" }, script = "" +
            "var userInputEventNames = [" +
            "   'click', 'contextmenu', 'auxclick', 'dblclick', 'mousedown'," +
            "   'mouseup', 'pointerup', 'touchend', 'keydown', 'keyup', 'touchstart'" +
            "];" +
            "var unlock = function(e) {" +
            "   audioContext.resume();" +
            "   unlockFunction();" +
            "   userInputEventNames.forEach(function (eventName) {" +
            "       $doc.removeEventListener(eventName, unlock);" +
            "   });" +
            "};" +
            "userInputEventNames.forEach(function (eventName) {" +
            "   $doc.addEventListener(eventName, unlock);" +
            "});"
    )
    public static native void hookUpSoundUnlockers(JSObject audioContext, UnlockFunction unlockFunction);

    public void setUnlocked() {
        Gdx.app.log("Webaudio", "Audiocontext unlocked");
        soundUnlocked = true;
    }

    public static boolean isSoundUnlocked() {
        return soundUnlocked;
    }

    @JSBody(params = { "audioContext" }, script = "" +
            "return audioContext.state !== 'running';"
    )
    static native boolean isAudioContextLocked(JSObject audioContext);

    /**
     * Older browsers do not support the Web Audio API. This is where we find out.
     *
     * @return is the WebAudioAPI available in this browser?
     */

    @JSBody(script = "" +
            "return typeof (window.AudioContext || window.webkitAudioContext) != \"undefined\";"
    )
    public static native boolean isSupported();

    @JSBody(script = "" +
            "var AudioContext = window.AudioContext || window.webkitAudioContext;" +
            "if(AudioContext) {" +
            "   var audioContext = new AudioContext();" +
            "   return audioContext;" +
            "}" +
            "return null;"
    )
    private static native JSObject createAudioContextJSNI();

    @JSBody(params = { "audioContext" }, script = "" +
            "var gainNode = null;" +
            "if (audioContext.createGain)" +
            "   gainNode = audioContext.createGain();" +
            "else" +
            "   gainNode = audioContext.createGainNode();" +
            "gainNode.gain.value = 1.0;" +
            "gainNode.connect(audioContext.destination);" +
            "return gainNode;"
    )
    private static native JSObject createGlobalVolumeNodeJSNI(JSObject audioContext);

    @JSBody(params = { "audioContext", "gainNode" }, script = "" +
            "gainNode.disconnect(audioContext.destination);"
    )
    private static native void disconnectJSNI(JSObject audioContext, JSObject gainNode);

    @JSBody(params = { "audioContext", "gainNode" }, script = "" +
            "gainNode.connect(audioContext.destination);"
    )
    private static native void connectJSNI(JSObject audioContext, JSObject gainNode);

    public JSObject getAudioContext() {
        return audioContext;
    }

    public Sound createSound(FileHandle fileHandle) {
        final WebAudioAPISound newSound = new WebAudioAPISound(audioContext, globalVolumeNode, audioControlGraphPool);

        String url = ((TeaFileHandle)fileHandle).getAssetUrl();

        XMLHttpRequest request = XMLHttpRequest.create();
        request.setOnReadyStateChange(new ReadyStateChangeHandler() {
            @Override
            public void stateChanged() {
                if(request.getReadyState() == XMLHttpRequest.DONE) {
                    if(request.getStatus() != 200) {
                    }
                    else {
                        Int8ArrayWrapper data = (Int8ArrayWrapper)Int8Array.create((ArrayBuffer)request.getResponse());

                        /*
                         * Start decoding the sound data. This is an asynchronous process, which is a bad fit for the libGDX API, which
                         * expects sound creation to be synchronous. The result is that sound won't actually start playing until the
                         * decoding is done.
                         */

                        DecodeAudioFunction audioFunction = new DecodeAudioFunction() {
                            @Override
                            public void decodeAudioFunction(JSObject jsObject) {
                                newSound.setAudioBuffer(jsObject);
                            }
                        };
                        decodeAudioData(getAudioContext(), data.getBuffer(), audioFunction);
                    }
                }
            }
        });
        request.open("GET", url);
        request.setResponseType("arraybuffer");
        request.send();
        return newSound;
    }

    public Music createMusic(FileHandle fileHandle) {
        String url = ((TeaFileHandle)fileHandle).getAssetUrl();

        HTMLAudioElementWrapper audio = Audio.createIfSupported();
        audio.setSrc(url);

        WebAudioAPIMusic music = new WebAudioAPIMusic(audioContext, audio, audioControlGraphPool);

        return music;
    }

    @JSFunctor
    public interface DecodeAudioFunction extends JSObject {
        void decodeAudioFunction(JSObject jsObject);
    }

    @JSBody(params = { "audioContextIn", "audioData", "decodeFunction" }, script = "" +
            "audioContextIn.decodeAudioData(" +
            "   audioData, " +
            "   decodeFunction," +
            "   function() {" +
            "      console.log(\"Error: decodeAudioData\");" +
            "   }" +
            ");"
    )
    public static native void decodeAudioData(JSObject audioContextIn, ArrayBufferWrapper audioData, DecodeAudioFunction decodeFunction);

    @Override
    public void pause() {
        // As the web application looses focus, we mute the sound
        disconnectJSNI(audioContext, globalVolumeNode);
    }

    @Override
    public void resume() {
        // As the web application regains focus, we unmute the sound
        connectJSNI(audioContext, globalVolumeNode);
    }

    public void setGlobalVolume(float volume) {
        setGlobalVolumeJSNI(volume, globalVolumeNode);
    }

    @JSBody(params = { "volume", "gainNode" }, script = "" +
            "gainNode.gain.value = volume;"
    )
    public static native void setGlobalVolumeJSNI(float volume, JSObject gainNode);

    @Override
    public void dispose() {
    }
}
