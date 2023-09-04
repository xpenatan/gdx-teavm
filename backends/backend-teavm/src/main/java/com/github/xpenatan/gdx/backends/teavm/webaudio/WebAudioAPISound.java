package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Keys;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 * Port from GWT gdx 1.12.0
 */
public class WebAudioAPISound implements Sound {
    // JavaScript object that is the base object of the Web Audio API
    private final JSObject audioContext;

    // JavaScript AudioNode representing the final destination of the sound. Typically the speakers of whatever device we are
    // running on.
    private final JSObject destinationNode;

    // Maps from integer keys to active sounds. Both the AudioBufferSourceNode and the associated AudioControlGraph are stored for
    // quick access
    private final IntMap<JSObject> activeSounds;
    private final IntMap<AudioControlGraph> activeAudioControlGraphs;

    // The raw sound data of this sound, which will be fed into the audio nodes
    private JSObject audioBuffer;

    // Key generator for sound objects.
    private int nextKey = 0;

    // We use a pool of AudioControlGraphs in order to minimize object creation
    private AudioControlGraphPool audioGraphPool;

    /**
     * @param audioContext    The JavaScript AudioContext object that servers as the base object of the Web Audio API
     * @param destinationNode The JavaScript AudioNode to route all the sound output to
     * @param audioGraphPool  A Pool that allows us to create AudioControlGraphs efficiently
     */
    public WebAudioAPISound(JSObject audioContext, JSObject destinationNode, AudioControlGraphPool audioGraphPool) {
        this.audioContext = audioContext;
        this.destinationNode = destinationNode;
        this.audioGraphPool = audioGraphPool;
        this.activeSounds = new IntMap<JSObject>();
        this.activeAudioControlGraphs = new IntMap<AudioControlGraph>();
    }

    /**
     * Set the buffer containing the actual sound data
     *
     * @param audioBuffer
     */
    public void setAudioBuffer(JSObject audioBuffer) {
        this.audioBuffer = audioBuffer;

        // If play-back of sounds have been requested before we were ready, do a pause/resume to get sound flowing
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            int key = keys.next();
            pause(key);
            resume(key, 0f);
        }
    }

    protected long play(float volume, float pitch, float pan, boolean loop) {
        // if the sound system is not yet unlocked, skip playing the sound.
        // otherwise, it is played when the user makes his first input
        if(!WebAudioAPIManager.isSoundUnlocked() && WebAudioAPIManager.isAudioContextLocked(audioContext)) return -1;

        // Get ourselves a fresh audio graph
        AudioControlGraph audioControlGraph = audioGraphPool.obtain();

        // Create the source node that will be feeding the audio graph
        JSObject audioBufferSourceNode = createBufferSourceNode(loop, pitch, audioContext, audioBuffer);

        // Configure the audio graph
        audioControlGraph.setSource(audioBufferSourceNode);
        audioControlGraph.setPan(pan);
        audioControlGraph.setVolume(volume);

        int myKey = nextKey++;

        // Start the playback

        EndedFunction endedFunction = new EndedFunction() {
            @Override
            public void endedFunc() {
                soundDone(myKey);
            }
        };
        playJSNI(audioBufferSourceNode, myKey, 0f, audioContext, endedFunction);

        // Remember that we are playing
        activeSounds.put(myKey, audioBufferSourceNode);
        activeAudioControlGraphs.put(myKey, audioControlGraph);

        return myKey;
    }

    private void soundDone(int key) {
        // The sound might have been removed by an explicit stop, before the sound reached its end
        if(activeSounds.containsKey(key)) {
            activeSounds.remove(key);
            audioGraphPool.free(activeAudioControlGraphs.remove(key));
        }
    }

    @JSBody(params = { "loop", "pitch", "audioContext", "audioBuffer" }, script = "" +
            "if (audioBuffer == null) {" +
            "   audioBuffer = audioContext.createBuffer(2, 22050, 44100);" +
            "}" +
            "var source = audioContext.createBufferSource();" +
            "source.buffer = audioBuffer;" +
            "source.loop = loop;" +
            "if (pitch !== 1.0) {" +
            "   source.playbackRate.value = pitch;" +
            "}" +
            "return source;"
    )
    public static native JSObject createBufferSourceNode(boolean loop, float pitch, JSObject audioContext, JSObject audioBuffer);


    @JSFunctor
    public interface EndedFunction extends JSObject {
        void endedFunc();
    }

    @JSBody(params = { "source", "key", "startOffset", "audioContext", "endedFunction" }, script = "" +
            "source.startTime = audioContext.currentTime;" +
            "source.onended = endedFunction;" +
            "if(typeof (source.start) !== \"undefined\")" +
            "   source.start(audioContext.currentTime, startOffset);" +
            "else" +
            "   source.noteOn(audioContext.currentTime, startOffset);" +
            "return source;"
    )
    public native JSObject playJSNI(JSObject source, int key, float startOffset, JSObject audioContext, EndedFunction endedFunction);

    @JSBody(params = { "audioBufferSourceNode" }, script = "" +
            "if(typeof (audioBufferSourceNode.stop) !== 'undefined')" +
            "   audioBufferSourceNode.stop();" +
            "else" +
            "   audioBufferSourceNode.noteOff();"
    )
    public static native void stopJSNI(JSObject audioBufferSourceNode);

    @Override
    public long play() {
        return play(1f);
    }

    @Override
    public long play(float volume) {
        return play(volume, 1f, 0f);
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        return play(volume, pitch, pan, false);
    }

    @Override
    public long loop() {
        return loop(1f);
    }

    @Override
    public long loop(float volume) {
        return loop(volume, 1f, 0f);
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return play(volume, pitch, pan, true);
    }

    @Override
    public void stop() {
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            int next = keys.next();
            stop(next);
        }
    }

    @Override
    public void pause() {
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            pause(keys.next());
        }
    }

    @Override
    public void resume() {
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            resume(keys.next());
        }
    }

    @Override
    public void dispose() {
        stop();
        activeSounds.clear();
    }

    @Override
    public void stop(long soundId) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            JSObject audioBufferSourceNode = activeSounds.remove(soundKey);
            stopJSNI(audioBufferSourceNode);

            audioGraphPool.free(activeAudioControlGraphs.remove(soundKey));
        }
    }

    @Override
    public void pause(long soundId) {
        // Record our current position, and then stop
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            JSObject audioBufferSourceNode = activeSounds.get(soundKey);

            // The API has no concept of pause/resume, so we do it by recording a pause time stamp, and then stopping the sound. On
            // resume we play the
            // sound again, starting from a calculated offset.
            pauseJSNI(audioBufferSourceNode, audioContext);
            stopJSNI(audioBufferSourceNode);
        }
    }

    @Override
    public void resume(long soundId) {
        resume(soundId, null);
    }

    private void resume(long soundId, Float from) {
        // Start from previous paused position
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            JSObject audioBufferSourceNode = activeSounds.remove(soundKey);
            AudioControlGraph audioControlGraph = activeAudioControlGraphs.get(soundKey);

            boolean loop = getLoopingJSNI(audioBufferSourceNode);
            float pitch = getPitchJSNI(audioBufferSourceNode);
            float resumeOffset = getResumeOffsetJSNI(audioBufferSourceNode);

            if(from != null) resumeOffset = from;

            // These things can not be re-used. One play only, as dictated by the Web Audio API
            JSObject newAudioBufferSourceNode = createBufferSourceNode(loop, pitch, audioContext, audioBuffer);
            audioControlGraph.setSource(newAudioBufferSourceNode);
            activeSounds.put(soundKey, newAudioBufferSourceNode);


            EndedFunction endedFunction = new EndedFunction() {
                @Override
                public void endedFunc() {
                    soundDone(soundKey);
                }
            };
            playJSNI(newAudioBufferSourceNode, soundKey, resumeOffset, audioContext, endedFunction);
        }
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            JSObject sound = activeSounds.get(soundKey);
            setLoopingJSNI(sound, looping);
        }
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            JSObject sound = activeSounds.get(soundKey);
            setPitchJSNI(sound, pitch);
        }
    }

    @Override
    public void setVolume(long soundId, float volume) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioControlGraph audioControlGraph = activeAudioControlGraphs.get(soundKey);
            audioControlGraph.setVolume(volume);
        }
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioControlGraph audioControlGraph = activeAudioControlGraphs.get(soundKey);
            audioControlGraph.setPan(pan);
            audioControlGraph.setVolume(volume);
        }
    }

    @JSBody(params = { "audioBufferSourceNode", "pitch" }, script = "" +
            "audioBufferSourceNode.playbackRate.value = pitch;"
    )
    public static native void setPitchJSNI(JSObject audioBufferSourceNode, float pitch);

    @JSBody(params = { "audioBufferSourceNode" }, script = "" +
            "return audioBufferSourceNode.playbackRate.value;"
    )
    public static native float getPitchJSNI(JSObject audioBufferSourceNode);

    @JSBody(params = { "audioBufferSourceNode", "looping" }, script = "" +
            "audioBufferSourceNode.loop = looping;"
    )
    public static native void setLoopingJSNI(JSObject audioBufferSourceNode, boolean looping);

    @JSBody(params = { "audioBufferSourceNode" }, script = "" +
            "return audioBufferSourceNode.loop;"
    )
    public static native boolean getLoopingJSNI(JSObject audioBufferSourceNode);

    @JSBody(params = { "audioBufferSourceNode", "audioContext" }, script = "" +
            "audioBufferSourceNode.pauseTime = audioContext.currentTime;"
    )
    public native void pauseJSNI(JSObject audioBufferSourceNode, JSObject audioContext);

    @JSBody(params = { "audioBufferSourceNode" }, script = "" +
            "return audioBufferSourceNode.pauseTime - audioBufferSourceNode.startTime;"
    )
    public static native float getResumeOffsetJSNI(JSObject audioBufferSourceNode);
}