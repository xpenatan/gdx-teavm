package com.github.xpenatan.gdx.backends.teavm.dom;

import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.webaudio.AnalyserNode;
import org.teavm.jso.webaudio.AudioBuffer;
import org.teavm.jso.webaudio.AudioBufferSourceNode;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.AudioParam;
import org.teavm.jso.webaudio.ChannelMergerNode;
import org.teavm.jso.webaudio.ChannelSplitterNode;
import org.teavm.jso.webaudio.MediaEvent;

/**
 * Ported https://github.com/WebAudio/web-audio-api/issues/2397#issuecomment-1262583050
 * SourceNode is duplicated because there is sound glitch when trying the original code,
 * @author xpenatan
 */
public class AudioBufferSourceNodeWrapper {

    public AudioBufferSourceNode _bufferSource;
    private AudioContext audioContext;
//    private AudioBufferSourceNode _bufferPositionSource;
    private ChannelSplitterNode _splitter;
    private ChannelMergerNode _out;
    private Float32Array _sampleHolder;
    private AnalyserNode _analyser;
    private float duration;

    public AudioBufferSourceNodeWrapper(AudioContext audioContext) {
        this.audioContext = audioContext;
        _bufferSource = audioContext.createBufferSource();
//        _bufferPositionSource = audioContext.createBufferSource();
        _splitter = audioContext.createChannelSplitter();
        _out = audioContext.createChannelMerger();
        _sampleHolder = new Float32Array(1);
    }

    public AudioBuffer getBuffer() {
        return _bufferSource.getBuffer();
    }

    public void setBuffer(AudioBuffer audioBuffer) {
//        _bufferSource.setBuffer(audioBuffer);
        duration = (float)audioBuffer.getDuration();

        int numberOfChannels = audioBuffer.getNumberOfChannels();
        int length = audioBuffer.getLength();
        float sampleRate = audioBuffer.getSampleRate();

        AudioBuffer newBuffer = audioContext.createBuffer(numberOfChannels + 1, length, sampleRate);

        // copy data from the audioBuffer arg to our new AudioBuffer
        for (int index = 0; index < numberOfChannels; index++) {
            newBuffer.copyToChannel(audioBuffer.getChannelData(index), index);
        }

        // fill up the position channel with numbers from 0 to 1
        Float32Array timeDataArray = new Float32Array(length);
        for (int i = 0; i < length; i++) {
            float val = i / (float)length;
            timeDataArray.set(i, val);
        }
        newBuffer.copyToChannel(timeDataArray, numberOfChannels);

        // Set buffer need to be after copy channel so timer works on firefox
        _bufferSource.setBuffer(newBuffer);

        // split the channels
        _bufferSource.connect(_splitter);

        // connect all the audio channels to the line out
        for (int index = 0; index < numberOfChannels; index++) {
            _splitter.connect(_out, index, index);
        }

        // connect the position channel to an analyzer so we can extract position data
        _analyser = audioContext.createAnalyser();
        _splitter.connect(_analyser, numberOfChannels+1);
    }

    public AudioParam getPlaybackRate() {
        return _bufferSource.getPlaybackRate();
    }

    public AudioParam getDetune() {
        return _bufferSource.getDetune();
    }

    public boolean getLoop() {
        return _bufferSource.getLoop();
    }

    public void setLoop(boolean loop) {
        _bufferSource.setLoop(loop);
//        _bufferPositionSource.setLoop(loop);
    }

    public double getLoopStart() {
        return _bufferSource.getLoopStart();
    }

    public void setLoopStart(double start) {
        _bufferSource.setLoopStart(start);
//        _bufferPositionSource.setLoopStart(start);
    }

    public double getLoopEnd() {
        return _bufferSource.getLoopEnd();
    }

    public void setLoopEnd(double end) {
        _bufferSource.setLoopEnd(end);
//        _bufferPositionSource.setLoopEnd(end);
    }

    public void setOnEnded(EventListener<MediaEvent> ent) {
        _bufferSource.setOnEnded(ent);
    }

    public void start(double when, double offset, double duration) {
        _bufferSource.start(when, offset, duration);
//        _bufferPositionSource.start(when, offset, duration);
    }

    public void start(double when, double offset) {
        _bufferSource.start(when, offset);
//        _bufferPositionSource.start(when, offset);
    }

    public void start(double when) {
        _bufferSource.start(when);
//        _bufferPositionSource.start(when);
    }

    public void start() {
        _bufferSource.start();
//        _bufferPositionSource.start();
    }

    public void stop(double when) {
        _bufferSource.stop(when);
//        _bufferPositionSource.stop(when);
    }

    public void stop() {
        _bufferSource.stop();
//        _bufferPositionSource.stop();
    }

    /** return position 0f - 1f */
    public float getPlaybackPosition() {
        _analyser.getFloatTimeDomainData(_sampleHolder);
        return _sampleHolder.get(0);
    }

    /** return position 0f - 1f */
    public float getPlaybackDuration() {
        return getPlaybackPosition() * duration;
    }
}