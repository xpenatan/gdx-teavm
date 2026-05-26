package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.audio.Music;

public class TeaMockMusic implements Music {
    private boolean looping;
    private boolean playing;
    private float volume = 1;
    private float position;
    private OnCompletionListener completionListener;

    @Override
    public void play() {
        playing = true;
    }

    @Override
    public void pause() {
        playing = false;
    }

    @Override
    public void stop() {
        playing = false;
        position = 0;
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void setLooping(boolean isLooping) {
        looping = isLooping;
    }

    @Override
    public boolean isLooping() {
        return looping;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setPan(float pan, float volume) {
        this.volume = volume;
    }

    @Override
    public void setPosition(float position) {
        this.position = position;
    }

    @Override
    public float getPosition() {
        return position;
    }

    @Override
    public void dispose() {
        playing = false;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        completionListener = listener;
    }

    public OnCompletionListener getCompletionListener() {
        return completionListener;
    }
}
