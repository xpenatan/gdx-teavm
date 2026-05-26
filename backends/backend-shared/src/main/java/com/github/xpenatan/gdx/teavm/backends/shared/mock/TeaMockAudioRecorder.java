package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.audio.AudioRecorder;

public class TeaMockAudioRecorder implements AudioRecorder {
    @Override
    public void read(short[] samples, int offset, int numSamples) {
    }

    @Override
    public void dispose() {
    }
}
