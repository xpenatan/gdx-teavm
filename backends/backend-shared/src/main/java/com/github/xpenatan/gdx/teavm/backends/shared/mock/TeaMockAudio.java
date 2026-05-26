package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class TeaMockAudio implements Audio {
    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        return new TeaMockAudioDevice(isMono);
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        return new TeaMockAudioRecorder();
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return new TeaMockSound();
    }

    @Override
    public Music newMusic(FileHandle file) {
        return new TeaMockMusic();
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
    public void dispose() {
    }
}
