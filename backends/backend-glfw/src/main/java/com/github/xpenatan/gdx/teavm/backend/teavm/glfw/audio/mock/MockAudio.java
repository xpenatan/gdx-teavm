package com.github.xpenatan.gdx.teavm.backend.teavm.glfw.audio.mock;

import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backend.teavm.glfw.audio.GLFWAudio;

/**
 * The headless backend does its best to mock elements. This is intended to make code-sharing between server and client as simple
 * as possible.
 */
public class MockAudio implements GLFWAudio {

    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        return new MockAudioDevice();
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        return new MockAudioRecorder();
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return new MockSound();
    }

    @Override
    public Music newMusic(FileHandle file) {
        return new MockMusic();
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        return true;
    }

    @Override
    public String[] getAvailableOutputDevices() {
        return new String[0];
    }

    @Override
    public void update() {
    }

    @Override
    public void dispose() {
    }
}
