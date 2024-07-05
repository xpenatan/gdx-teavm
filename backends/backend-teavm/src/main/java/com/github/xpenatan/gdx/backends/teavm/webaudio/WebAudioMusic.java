package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.DecodeSuccessCallback;
import org.teavm.jso.webaudio.GainNode;

public class WebAudioMusic implements Music {

    private AudioContext audioContext;
    final WebAudioAPI audioAPI;

    private long sourceId = -1;

    private OnCompletionListener listener;

    private boolean isLooping;
    private MusicState state = MusicState.STOP;
    private float startPosition = 0;
    private float volume = 1;
    private float pan = 0;

    public WebAudioMusic(FileHandle fileHandle, AudioContext audioContext, GainNode globalVolumeNode, AudioControlGraphPool audioControlGraphPool) {
        byte[] bytes = fileHandle.readBytes();
        this.audioContext = audioContext;
        audioAPI = new WebAudioAPI(audioContext, globalVolumeNode, audioControlGraphPool) {
            @Override
            protected void onSoundDone(int soundId) {
                if(listener != null) {
                    listener.onCompletion(WebAudioMusic.this);
                }
            }
        };
        ArrayBufferViewWrapper data = TypedArrays.getTypedArray(bytes);
        DecodeSuccessCallback successCallback = audioAPI::setAudioBuffer;
        WebAudioAPI.decodeAudioData(fileHandle, audioContext, data.getBuffer(), successCallback);
    }

    @Override
    public void play() {
        if(state == MusicState.STOP) {
            state = MusicState.PLAY;
            sourceId = audioAPI.play(startPosition, volume, 1f, pan, isLooping);
            startPosition = 0;
        }
        else if(state == MusicState.PAUSE) {
            state = MusicState.PLAY;
            audioAPI.resume(sourceId);
        }
    }

    @Override
    public void pause() {
        if(state == MusicState.PLAY) {
            state = MusicState.PAUSE;
            audioAPI.pause(sourceId);
        }
    }

    @Override
    public void stop() {
        if(state == MusicState.PLAY || state == MusicState.PAUSE) {
            state = MusicState.STOP;
            audioAPI.stop(sourceId);
            sourceId = -1;
        }
    }

    @Override
    public boolean isPlaying() {
        return state == MusicState.PLAY;
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
        audioAPI.setLooping(sourceId, isLooping);
    }

    @Override
    public boolean isLooping() {
        return isLooping;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = volume;
        audioAPI.setVolume(sourceId, volume);
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setPan(float pan, float volume) {
        this.volume = volume;
        this.pan = pan;
        audioAPI.setPan(sourceId, pan, volume);
    }

    @Override
    public void setPosition(float position) {
        this.startPosition = position;
        if(state == MusicState.PLAY) {
            stop();
            play();
        }
        else if(state == MusicState.PAUSE) {
            stop();
        }
    }

    @Override
    public float getPosition() {
        if(audioContext != null) {
            return audioAPI.getCurrentTime(sourceId);
        }
        else {
            return 0f;
        }
    }

    @Override
    public void dispose() {
        audioAPI.dispose();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    private enum MusicState {
        PLAY, PAUSE, STOP
    }
}