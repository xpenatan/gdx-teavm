package com.github.xpenatan.gdx.backends.teavm.webaudio.howler;

import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class HowlerAudioManager implements LifecycleListener {

    public Sound createSound(FileHandle fileHandle) {
        return new HowlSound(fileHandle);
    }

    public Music createMusic(FileHandle fileHandle) {
        return new HowlMusic(fileHandle);
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
}
