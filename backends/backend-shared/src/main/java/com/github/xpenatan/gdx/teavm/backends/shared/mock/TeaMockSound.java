package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.audio.Sound;

public class TeaMockSound implements Sound {
    @Override
    public long play() {
        return -1;
    }

    @Override
    public long play(float volume) {
        return -1;
    }

    @Override
    public long play(float volume, float pitch, float pan) {
        return -1;
    }

    @Override
    public long loop() {
        return -1;
    }

    @Override
    public long loop(float volume) {
        return -1;
    }

    @Override
    public long loop(float volume, float pitch, float pan) {
        return -1;
    }

    @Override
    public void stop() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void stop(long soundId) {
    }

    @Override
    public void pause(long soundId) {
    }

    @Override
    public void resume(long soundId) {
    }

    @Override
    public void setLooping(long soundId, boolean looping) {
    }

    @Override
    public void setPitch(long soundId, float pitch) {
    }

    @Override
    public void setVolume(long soundId, float volume) {
    }

    @Override
    public void setPan(long soundId, float pan, float volume) {
    }
}
