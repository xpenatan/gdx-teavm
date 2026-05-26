package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.audio.AudioDevice;

public class TeaMockAudioDevice implements AudioDevice {
    private final boolean mono;

    public TeaMockAudioDevice(boolean mono) {
        this.mono = mono;
    }

    @Override
    public boolean isMono() {
        return mono;
    }

    @Override
    public void writeSamples(short[] samples, int offset, int numSamples) {
    }

    @Override
    public void writeSamples(float[] samples, int offset, int numSamples) {
    }

    @Override
    public int getLatency() {
        return 0;
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setVolume(float volume) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
