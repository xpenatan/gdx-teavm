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
 * @author xpenatan
 */
public class AudioBufferSourceNodeWrapper {

    private AudioContext audioContext;
    public AudioBufferSourceNode _bufferSource;
    ChannelSplitterNode _splitter;
    ChannelMergerNode _out;
    Float32Array _sampleHolder;
    AnalyserNode _analyser;

    public AudioBufferSourceNodeWrapper(AudioContext audioContext) {
        this.audioContext = audioContext;
        _bufferSource = audioContext.createBufferSource();
        _splitter = audioContext.createChannelSplitter();
        _out = audioContext.createChannelMerger();
        _sampleHolder = new Float32Array(1);
    }

    public AudioBuffer getBuffer() {
        return _bufferSource.getBuffer();
    }

    public void setBuffer(AudioBuffer audioBuffer) {

        AudioBuffer buffer2 = audioContext.createBuffer(audioBuffer.getNumberOfChannels() + 1, audioBuffer.getLength(), audioBuffer.getSampleRate());

        _bufferSource.setBuffer(buffer2);

        // copy data from the audioBuffer arg to our new AudioBuffer
        for (int index = 0; index < audioBuffer.getNumberOfChannels(); index++) {
            _bufferSource.getBuffer().copyToChannel(audioBuffer.getChannelData(index), index);
        }

        // fill up the position channel with numbers from 0 to 1

        int length = audioBuffer.getLength();
        Float32Array timeDataArray = new Float32Array(length);
        for (int i = 0; i < length; i++) {
            timeDataArray.set(i, (float)i / length);
        }
        _bufferSource.getBuffer().copyToChannel(timeDataArray, audioBuffer.getNumberOfChannels());

//        for (int index = 0; index < audioBuffer.getLength(); index++) {
//            Float32Array channelData = _bufferSource.getBuffer().getChannelData(audioBuffer.getNumberOfChannels());
//            channelData.set(index, (float)(index / audioBuffer.getLength()));
//        }

        // split the channels
        _bufferSource.connect(_splitter);

        // connect all the audio channels to the line out
        for (int index = 0; index < audioBuffer.getNumberOfChannels(); index++) {
            _splitter.connect(_out, index, index);
        }

        // connect the position channel to an analyzer so we can extract position data
        _analyser = audioContext.createAnalyser();
        _splitter.connect(_analyser, audioBuffer.getNumberOfChannels());
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
    }

    public double getLoopStart() {
        return _bufferSource.getLoopStart();
    }

    public void setLoopStart(double start) {
        _bufferSource.setLoopStart(start);
    }

    public double getLoopEnd() {
        return _bufferSource.getLoopEnd();
    }

    public void setLoopEnd(double end) {
        _bufferSource.setLoopEnd(end);
    }

    public void setOnEnded(EventListener<MediaEvent> ent) {
        _bufferSource.setOnEnded(ent);
    }

    public void start(double when, double offset, double duration) {
        _bufferSource.start(when, offset, duration);
    }

    public void start(double when, double offset) {
        _bufferSource.start(when, offset);
    }

    public void start(double when) {
        _bufferSource.start(when);
    }

    public void start() {
        _bufferSource.start();
    }

    public void stop(double when) {
        _bufferSource.stop(when);
    }

    public void stop() {
        _bufferSource.stop();
    }

    public float getPlaybackPosition() {
        _analyser.getFloatTimeDomainData(_sampleHolder);
        return _sampleHolder.get(0);
    }
}