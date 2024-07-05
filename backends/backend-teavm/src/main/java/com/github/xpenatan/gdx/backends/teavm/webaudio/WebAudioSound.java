package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.DecodeSuccessCallback;
import org.teavm.jso.webaudio.GainNode;

public class WebAudioSound implements Sound {

    final WebAudioAPI audioAPI;

    public WebAudioSound(FileHandle fileHandle, AudioContext audioContext, GainNode globalVolumeNode, AudioControlGraphPool audioControlGraphPool) {
        byte[] bytes = fileHandle.readBytes();
        audioAPI = new WebAudioAPI(audioContext, globalVolumeNode, audioControlGraphPool);
        ArrayBufferViewWrapper data = TypedArrays.getTypedArray(bytes);
        DecodeSuccessCallback successCallback = audioAPI::setAudioBuffer;
        WebAudioAPI.decodeAudioData(fileHandle, audioContext, data.getBuffer(), successCallback);
    }

    @Override
    public long play() {
        return audioAPI.play(0f, 1f, 0f, 0f, false);
    }

    @Override
    public long play(float volume) {
        return audioAPI.play(0f, volume, 0f, 0f, false);
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        return audioAPI.play(0f, volume, pitch, pan, false);
    }

    @Override
    public long loop() {
        return audioAPI.play(0f, 1f, 0f, 0f, true);
    }

    @Override
    public long loop(float volume) {
        return audioAPI.play(0f, volume, 0f, 0f, true);
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return audioAPI.play(0f, volume, pitch, pan, true);
    }

    @Override
    public void stop() {
        audioAPI.stop();
    }

    @Override
    public void pause() {
        audioAPI.pause();
    }

    @Override
    public void resume() {
        audioAPI.resume();
    }

    @Override
    public void dispose() {
        audioAPI.dispose();
    }

    @Override
    public void stop(long soundId) {
        audioAPI.stop(soundId);
    }

    @Override
    public void pause(long soundId) {
        audioAPI.pause(soundId);
    }

    @Override
    public void resume(long soundId) {
        audioAPI.resume(soundId);
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        audioAPI.setLooping(soundId, looping);
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        audioAPI.setPitch(soundId, pitch);
    }

    @Override
    public void setVolume(long soundId, float volume) {
        audioAPI.setVolume(soundId, volume);
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        audioAPI.setPan(soundId, pan, volume);
    }
}