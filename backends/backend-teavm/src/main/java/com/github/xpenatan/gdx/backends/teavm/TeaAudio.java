package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.TeaSoundManager;

public class TeaAudio implements Audio {

    private TeaSoundManager soundManager;

    public TeaAudio(TeaSoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        throw new GdxRuntimeException("AudioDevice not supported by Web backend");
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        throw new GdxRuntimeException("AudioRecorder not supported by Web backend");
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return new TeaSound(soundManager, fileHandle);
    }

    @Override
    public Music newMusic(FileHandle file) {
        return new TeaMusic(soundManager, file);
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String[] getAvailableOutputDevices() {
        // TODO Auto-generated method stub
        return new String[0];
    }
}
