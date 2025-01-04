package com.github.xpenatan.gdx.backends.teavm.webaudio.howler;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;

public class HowlMusic implements Music {

    private Howl howl;

    public HowlMusic(FileHandle fileHandle) {
        byte[] bytes = fileHandle.readBytes();
        ArrayBufferViewWrapper data = TypedArrays.getTypedArray(bytes);
        howl = Howl.create(data);
    }

    @Override
    public void play() {
        howl.play();
    }

    @Override
    public void pause() {
        howl.pause();
    }

    @Override
    public void stop() {
        howl.stop();
    }

    @Override
    public boolean isPlaying() {
        return howl.isPlaying();
    }

    @Override
    public void setLooping(boolean isLooping) {
        howl.setLoop(isLooping);
    }

    @Override
    public boolean isLooping() {
        return howl.getLoop();
    }

    @Override
    public void setVolume(float volume) {
        howl.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return howl.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        howl.setStereo(pan);
        howl.setVolume(volume);
    }

    @Override
    public void setPosition(float position) {
        howl.setSeek(position);
    }

    @Override
    public float getPosition() {
        return howl.getSeek();
    }

    @Override
    public void dispose() {
        howl.stop();
        howl.unload();
        howl = null;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        howl.on("end", () -> listener.onCompletion(HowlMusic.this));
    }
}