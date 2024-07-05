package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Keys;
import com.github.xpenatan.gdx.backends.teavm.dom.AudioBufferSourceNodeWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferWrapper;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.webaudio.AudioBuffer;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.AudioParam;
import org.teavm.jso.webaudio.DecodeErrorCallback;
import org.teavm.jso.webaudio.DecodeSuccessCallback;
import org.teavm.jso.webaudio.GainNode;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class WebAudioAPI {
    // JavaScript object that is the base object of the Web Audio API
    private final AudioContext audioContext;

    // JavaScript AudioNode representing the final destination of the sound. Typically the speakers of whatever device we are
    // running on.
    private final GainNode destinationNode;

    // Maps from integer keys to active sounds. Both the AudioBufferSourceNode and the associated AudioControlGraph are stored for
    // quick access
    private final IntMap<AudioSourceData> activeSounds;

    // The raw sound data of this sound, which will be fed into the audio nodes
    private AudioBuffer audioBuffer;

    // Key generator for sound objects.
    private int nextKey = 0;

    // We use a pool of AudioControlGraphs in order to minimize object creation
    private final AudioControlGraphPool audioGraphPool;

    /**
     * @param audioContext    The JavaScript AudioContext object that servers as the base object of the Web Audio API
     * @param destinationNode The JavaScript AudioNode to route all the sound output to
     * @param audioGraphPool  A Pool that allows us to create AudioControlGraphs efficiently
     */
    public WebAudioAPI(AudioContext audioContext, GainNode destinationNode, AudioControlGraphPool audioGraphPool) {
        this.audioContext = audioContext;
        this.destinationNode = destinationNode;
        this.audioGraphPool = audioGraphPool;
        this.activeSounds = new IntMap<>();
    }

    /**
     * Set the buffer containing the actual sound data
     *
     * @param audioBuffer
     */
    public void setAudioBuffer(AudioBuffer audioBuffer) {
        this.audioBuffer = audioBuffer;
        // If play-back of sounds have been requested before we were ready, do a pause/resume to get sound flowing
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            int key = keys.next();
            pause(key);
            resume(key, 0f);
        }
    }

    public long play(float offset, float volume, float pitch, float pan, boolean loop) {
        // if the sound system is not yet unlocked, skip playing the sound.
        // otherwise, it is played when the user makes his first input
        if(!WebAudioAPIManager.isSoundUnlocked() && WebAudioAPIManager.isAudioContextLocked(audioContext)) return -1;

        int myKey = nextKey++;
        AudioSourceData audioSourceData = new AudioSourceData(myKey);

        audioSourceData.offset = offset;
        audioSourceData.volume = volume;
        audioSourceData.pitch = pitch;
        audioSourceData.pan = pan;
        audioSourceData.loop = loop;
        activeSounds.put(myKey, audioSourceData);

        // Get ourselves a fresh audio graph
        AudioControlGraph audioControlGraph = audioGraphPool.obtain();
        audioSourceData.audioControlGraph = audioControlGraph;
        // Create the source node that will be feeding the audio graph
        createBufferSourceNodeInternal(audioSourceData, loop, pitch);

        // Configure the audio graph
        audioControlGraph.setSource(audioSourceData.node._bufferSource);
        audioControlGraph.setPan(pan);
        audioControlGraph.setVolume(volume);

        // Start the playback
        playInternal(audioSourceData, offset);
        return myKey;
    }

    public void stop() {
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            int next = keys.next();
            stop(next);
        }
    }

    public void pause() {
        System.out.println("PAUSEEEE");
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            pause(keys.next());
        }
    }

    public void resume() {
        System.out.println("RESUMEEEE");
        Keys keys = activeSounds.keys();
        while(keys.hasNext) {
            resume(keys.next());
        }
    }

    public void dispose() {
        stop();
        activeSounds.clear();
    }

    public void stop(long soundId) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.remove(soundKey);
            if(audioSourceData.node != null) {
                audioSourceData.node.stop();
                audioSourceData.node = null;
            }
            if(audioSourceData.audioControlGraph != null) {
                audioGraphPool.free(audioSourceData.audioControlGraph);
                audioSourceData.audioControlGraph = null;
            }
        }
    }

    public void pause(long soundId) {
        // Record our current position, and then stop
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.get(soundKey);

            // The API has no concept of pause/resume, so we do it by recording a pause time stamp, and then stopping the sound. On
            // resume we play the
            // sound again, starting from a calculated offset.

            audioSourceData.pauseTime = audioContext.getCurrentTime();
            // Remove ended listener when pausing
            if(audioSourceData.node != null) {
                audioSourceData.node.setOnEnded(evt -> {});
                audioSourceData.node.stop();
            }
        }
    }

    public void resume(long soundId) {
        resume(soundId, null);
    }

    private void resume(long soundId, Float from) {
        // Start from previous paused position
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.get(soundKey);

            boolean loop = audioSourceData.node.getLoop();
            float pitch = audioSourceData.node.getPlaybackRate().getValue();

            float resumeOffset = (float)(audioSourceData.pauseTime - audioSourceData.startTime);

            if(from != null) resumeOffset = from;

            createBufferSourceNodeInternal(audioSourceData, loop, pitch);

            audioSourceData.audioControlGraph.setSource(audioSourceData.node._bufferSource);

            playInternal(audioSourceData, resumeOffset);
        }
    }

    public void setLooping(long soundId, boolean looping) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.get(soundKey);
            audioSourceData.node.setLoop(looping);
        }
    }

    public void setPitch(long soundId, float pitch) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.get(soundKey);
            audioSourceData.node.getPlaybackRate().setValue(pitch);
        }
    }

    public void setVolume(long soundId, float volume) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceNodeData = activeSounds.get(soundKey);
            audioSourceNodeData.audioControlGraph.setVolume(volume);
        }
    }

    public float getVolume(long soundId) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceNodeData = activeSounds.get(soundKey);
            return audioSourceNodeData.audioControlGraph.getVolume();
        }
        return -1;
    }

    public void setPan(long soundId, float pan, float volume) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceNodeData = activeSounds.get(soundKey);
            AudioControlGraph audioControlGraph = audioSourceNodeData.audioControlGraph;
            audioControlGraph.setPan(pan);
            audioControlGraph.setVolume(volume);
        }
    }

    public float getCurrentTime(long soundId) {
        int soundKey = (int)soundId;
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.get(soundKey);
            float playbackPosition = audioSourceData.node.getPlaybackDuration();
            return playbackPosition;


//            if(audioSourceData.pauseTime == audioSourceData.startTime) {
//                return (float)(audioContext.getCurrentTime() - audioSourceData.startTime);
//            }
//            else {
//                float resumeOffset = (float)(audioSourceData.pauseTime - audioSourceData.startTime);
//                return resumeOffset;
//            }
        }
        return 0;
    }

    protected void onSoundDone(int soundId) {
    }

    private void soundDone(int soundKey) {
        // The sound might have been removed by an explicit stop, before the sound reached its end
        if(activeSounds.containsKey(soundKey)) {
            AudioSourceData audioSourceData = activeSounds.remove(soundKey);
            audioGraphPool.free(audioSourceData.audioControlGraph);
            audioSourceData.audioControlGraph = null;
        }
        onSoundDone(soundKey);
    }

    private void createBufferSourceNodeInternal(AudioSourceData data, boolean loop, float pitch) {
        if(audioBuffer == null) {
            audioBuffer = audioContext.createBuffer(2, 22050, 44100);
        }

        AudioBufferSourceNodeWrapper newAudioBufferSourceNode = new AudioBufferSourceNodeWrapper(audioContext);
        newAudioBufferSourceNode.setBuffer(audioBuffer);
        newAudioBufferSourceNode.setLoop(loop);
        if(pitch != 1.0f) {
            AudioParam playbackRate = newAudioBufferSourceNode.getPlaybackRate();
            playbackRate.setValue(pitch);
        }
        data.node = newAudioBufferSourceNode;
    }

    private void playInternal(AudioSourceData audioSourceData, float startOffset) {
        double currentTime = audioContext.getCurrentTime();
        audioSourceData.startTime = currentTime;
        audioSourceData.pauseTime = currentTime;
        audioSourceData.node.setOnEnded(evt -> soundDone(audioSourceData.soundId));
        audioSourceData.node.start(currentTime, startOffset);
    }

    public static void decodeAudioData(FileHandle fileHandle, AudioContext audioContext, ArrayBufferWrapper audioData, DecodeSuccessCallback decodeFunction) {
        audioContext.decodeAudioData((ArrayBuffer)audioData, decodeFunction, new DecodeErrorCallback() {
            @Override
            public void onError(JSObject error) {
                System.err.println("Error: decodeAudioData");
            }
        });
    }

    private static class AudioSourceData {
        public final int soundId;

        private AudioBufferSourceNodeWrapper node;
        public AudioControlGraph audioControlGraph;
        public double startTime;
        public double pauseTime;

        public float offset;
        public float volume;
        public float pitch;
        public float pan;
        public boolean loop;
        public boolean delayedPlay = false;

        public AudioBufferSourceNodeWrapper getNode() {
            return node;
        }

        public AudioSourceData(int soundId) {
            this.soundId = soundId;
        }
    }
}