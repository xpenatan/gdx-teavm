package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.webaudio.WebAudioAPIManager;

/**
 * @author xpenatan
 * Port from GWT gdx 1.12.0
 */
public class DefaultTeaAudio implements Audio {
    private WebAudioAPIManager webAudioAPIManager = null;

    public DefaultTeaAudio() {
        webAudioAPIManager = new WebAudioAPIManager();
    }

    @Override
    public AudioDevice newAudioDevice(int samplingRate, boolean isMono) {
        throw new GdxRuntimeException("AudioDevice not supported by GWT backend");
    }

    @Override
    public AudioRecorder newAudioRecorder(int samplingRate, boolean isMono) {
        throw new GdxRuntimeException("AudioRecorder not supported by GWT backend");
    }

    @Override
    public Sound newSound(FileHandle fileHandle) {
        return webAudioAPIManager.createSound(fileHandle);
    }

    @Override
    public Music newMusic(FileHandle file) {
        return webAudioAPIManager.createMusic(file);
    }

    @Override
    public boolean switchOutputDevice(String deviceIdentifier) {
        return true;
    }

    @Override
    public String[] getAvailableOutputDevices() {
        return new String[0];
    }
}