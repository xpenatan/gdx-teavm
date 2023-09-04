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
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class WebAudioAPIManager implements LifecycleListener {
    private final JSObject audioContext;
    private final JSObject globalVolumeNode;
//    private final AssetDownloader assetDownloader;
    private final AudioControlGraphPool audioControlGraphPool;
    private static boolean soundUnlocked;

    public WebAudioAPIManager() {
//        this.assetDownloader = new AssetDownloader();
        this.audioContext = createAudioContextJSNI();
        this.globalVolumeNode = createGlobalVolumeNodeJSNI();
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
        if(isAudioContextLocked(audioContext))
            hookUpSoundUnlockers();
        else
            setUnlocked();
    }

    public native void hookUpSoundUnlockers() /*-{
		var self = this;
		var audioContext = self.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::audioContext;
		
		// An array of various user interaction events we should listen for
		var userInputEventNames = [
			'click', 'contextmenu', 'auxclick', 'dblclick', 'mousedown',
			'mouseup', 'pointerup', 'touchend', 'keydown', 'keyup', 'touchstart'
		];

		var unlock = function(e) {
			
			// resume audio context if it was suspended. It's only required for musics since sounds automatically resume
			// audio context when started.
			audioContext.resume();
			
			self.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::setUnlocked()();

			userInputEventNames.forEach(function (eventName) {
				$doc.removeEventListener(eventName, unlock);
			});

		};

		userInputEventNames.forEach(function (eventName) {
			$doc.addEventListener(eventName, unlock);
		});
	}-*/;

    public void setUnlocked() {
        Gdx.app.log("Webaudio", "Audiocontext unlocked");
        soundUnlocked = true;
    }

    public static boolean isSoundUnlocked() {
        return soundUnlocked;
    }

    static native boolean isAudioContextLocked(JSObject audioContext) /*-{
		return audioContext.state !== 'running';
	}-*/;

    /**
     * Older browsers do not support the Web Audio API. This is where we find out.
     *
     * @return is the WebAudioAPI available in this browser?
     */
    public static native boolean isSupported() /*-{
		return typeof (window.AudioContext || window.webkitAudioContext) != "undefined";
	}-*/;

    private static native JSObject createAudioContextJSNI() /*-{
		var AudioContext = window.AudioContext || window.webkitAudioContext;
		if (AudioContext) {
			var audioContext = new AudioContext();
			return audioContext;
		}
		return null;
	}-*/;

    private native JSObject createGlobalVolumeNodeJSNI() /*-{
		var audioContext = this.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::audioContext;

		var gainNode = null;
		if (audioContext.createGain)
			// Standard compliant
			gainNode = audioContext.createGain();
		else
			// Old WebKit/iOS
			gainNode = audioContext.createGainNode();

		// Default to full, unmuted volume
		gainNode.gain.value = 1.0;

		// Connect the global volume to the speakers. This will be the last part of our audio graph.
		gainNode.connect(audioContext.destination);

		return gainNode;
	}-*/;

    private native void disconnectJSNI() /*-{
		var audioContext = this.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::audioContext;
		var gainNode = this.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::globalVolumeNode;

		gainNode.disconnect(audioContext.destination);
	}-*/;

    private native void connectJSNI() /*-{
		var audioContext = this.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::audioContext;
		var gainNode = this.@com.badlogic.gdx.backends.gwt.webaudio.WebAudioAPIManager::globalVolumeNode;

		gainNode.connect(audioContext.destination);
	}-*/;

    public JSObject getAudioContext() {
        return audioContext;
    }

    public Sound createSound(FileHandle fileHandle) {

        //TODO finish

        final WebAudioAPISound newSound = new WebAudioAPISound(audioContext, globalVolumeNode, audioControlGraphPool);

//        String url = ((GwtFileHandle)fileHandle).getAssetUrl();
//
//        XMLHttpRequest request = XMLHttpRequest.create();
//        request.setOnReadyStateChange(new ReadyStateChangeHandler() {
//            @Override
//            public void onReadyStateChange(XMLHttpRequest xhr) {
//                if(xhr.getReadyState() == XMLHttpRequest.DONE) {
//                    if(xhr.getStatus() != 200) {
//                    }
//                    else {
//                        Int8ArrayWrapper data = TypedArrays.createInt8Array(xhr.getResponseArrayBuffer());
//
//                        /*
//                         * Start decoding the sound data. This is an asynchronous process, which is a bad fit for the libGDX API, which
//                         * expects sound creation to be synchronous. The result is that sound won't actually start playing until the
//                         * decoding is done.
//                         */
//
//                        DecodeAudioFunction audioFunction = new DecodeAudioFunction() {
//                            @Override
//                            public void decodeAudioFunction(JSObject jsObject) {
//                                newSound.setAudioBuffer(jsObject);
//                            }
//                        };
//                        decodeAudioData(getAudioContext(), data.getBuffer(), audioFunction);
//                    }
//                }
//            }
//        });
//        request.open("GET", url);
//        request.setResponseType(ResponseType.ArrayBuffer);
//        request.send();

        return newSound;
    }

    public Music createMusic(FileHandle fileHandle) {
        //TODO finish
        String url = "";
//        String url = ((TeaFileHandle)fileHandle).getAssetUrl();

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
        disconnectJSNI();
    }

    @Override
    public void resume() {
        // As the web application regains focus, we unmute the sound
        connectJSNI();
    }

    public void setGlobalVolume(float volume) {
        setGlobalVolumeJSNI(volume, globalVolumeNode);
    }

    @JSBody(params = { "volume", "globalVolumeNode" }, script = "" +
            "gainNode.gain.value = volume;"
    )
    public static native void setGlobalVolumeJSNI(float volume, JSObject globalVolumeNode);

    @Override
    public void dispose() {
    }
}
