package com.github.xpenatan.gdx.backends.teavm.webaudio.howler;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import org.teavm.jso.typedarrays.ArrayBufferView;

public class HowlSound implements Sound {

    private Howl howl;

    public HowlSound(FileHandle fileHandle) {
        byte[] bytes = fileHandle.readBytes();
        ArrayBufferView data = TypedArrays.getTypedArray(bytes);
        howl = Howl.create(data);
    }

    @Override
    public long play() {
        return howl.play();
    }

    @Override
    public long play(float volume) {
        int soundId = howl.play();
        howl.setVolume(volume, soundId);
        return soundId;
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        int soundId = howl.play();
        howl.setVolume(volume, soundId);
        howl.setRate(pitch, soundId);
        howl.setStereo(pan, soundId);
        return soundId;
    }

    @Override
    public long loop() {
        int soundId = howl.play();
        howl.setLoop(true, soundId);
        return soundId;
    }

    @Override
    public long loop(float volume) {
        int soundId = howl.play();
        howl.setLoop(true, soundId);
        howl.setVolume(volume, soundId);
        return soundId;
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        int soundId = howl.play();
        howl.setLoop(true, soundId);
        howl.setVolume(volume, soundId);
        howl.setStereo(volume, soundId);
        return soundId;
    }

    @Override
    public void stop() {
        howl.stop();
    }

    @Override
    public void pause() {
        howl.pause();
    }

    @Override
    public void resume() {
        howl.play();
    }

    @Override
    public void dispose() {
        howl.stop();
        howl = null;
    }

    @Override
    public void stop(long soundId) {
        howl.stop((int)soundId);
    }

    @Override
    public void pause(long soundId) {
        howl.pause((int)soundId);
    }

    @Override
    public void resume(long soundId) {
        howl.play((int)soundId);
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
        howl.setLoop(looping, (int)soundId);
    }

    @Override
    public void setPitch(long soundId, float pitch) {
        howl.setRate(pitch, (int)soundId);
    }

    @Override
    public void setVolume(long soundId, float volume) {
        howl.setVolume(volume, (int)soundId);
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
        int soundIdd = (int)soundId;
        howl.setStereo(pan, soundIdd);
        howl.setVolume(volume, soundIdd);
    }
}