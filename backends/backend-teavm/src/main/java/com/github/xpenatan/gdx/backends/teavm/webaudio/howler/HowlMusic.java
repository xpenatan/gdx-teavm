package com.github.xpenatan.gdx.backends.teavm.webaudio.howler;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;

public class HowlMusic implements Music {

    private int soundId;
    private Howl howl;

    public HowlMusic(FileHandle fileHandle) {
        byte[] bytes = fileHandle.readBytes();
        ArrayBufferViewWrapper data = TypedArrays.getTypedArray(bytes);
        howl = Howl.create(data);
        soundId = -1;
    }

    @Override
    public void play() {
        if(soundId != -1) {
            soundId = howl.play(soundId);
        }
        else {
            soundId = howl.play();
        }
    }

    @Override
    public void pause() {
        if(soundId != -1) {
            howl.pause(soundId);
        }
    }

    @Override
    public void stop() {
        howl.stop();
        soundId = -1;
    }

    @Override
    public boolean isPlaying() {
        if(soundId != -1) {
            return howl.isPlaying(soundId);
        }
        return false;
    }

    @Override
    public void setLooping(boolean isLooping) {
        if(soundId != -1) {
            howl.setLoop(isLooping, soundId);
        }
    }

    @Override
    public boolean isLooping() {
        if(soundId != -1) {
            return howl.getLoop(soundId);
        }
        return false;
    }

    @Override
    public void setVolume(float volume) {
        if(soundId != -1) {
            howl.setVolume(volume, soundId);
        }
    }

    @Override
    public float getVolume() {
        if(soundId != -1) {
            return howl.getVolume(soundId);
        }
        return 0f;
    }

    @Override
    public void setPan(float pan, float volume) {
        if(soundId != -1) {
            howl.setStereo(pan, soundId);
            howl.setVolume(volume, soundId);
        }
    }

    @Override
    public void setPosition(float position) {
        if(soundId != -1) {
            howl.setSeek(position, soundId);
        }
    }

    @Override
    public float getPosition() {
        return howl.getSeek(soundId);
    }

    @Override
    public void dispose() {
        howl.stop();
        soundId = -1;
        howl = null;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        howl.on("end", () -> listener.onCompletion(HowlMusic.this), soundId);
    }
}