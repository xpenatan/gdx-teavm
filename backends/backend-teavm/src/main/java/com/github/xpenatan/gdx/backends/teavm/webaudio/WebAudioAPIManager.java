package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.AudioDestinationNode;
import org.teavm.jso.webaudio.GainNode;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class WebAudioAPIManager implements LifecycleListener {
    private final AudioContext audioContext;
    private final GainNode globalVolumeNode;
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
            "       document.removeEventListener(eventName, unlock);" +
            "   });" +
            "};" +
            "userInputEventNames.forEach(function (eventName) {" +
            "   document.addEventListener(eventName, unlock);" +
            "});"
    )
    public static native void hookUpSoundUnlockers(JSObject audioContext, UnlockFunction unlockFunction);

    public void setUnlocked() {
        if(!soundUnlocked) {
            Gdx.app.log("Webaudio", "Audiocontext unlocked");
        }
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
    private static native AudioContext createAudioContextJSNI();

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
    private static native GainNode createGlobalVolumeNodeJSNI(JSObject audioContext);

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
        return new WebAudioSound(fileHandle, audioContext, globalVolumeNode, audioControlGraphPool);
    }

    public Music createMusic(FileHandle fileHandle) {
        return new WebAudioMusic(fileHandle, audioContext, globalVolumeNode, audioControlGraphPool);
    }

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
