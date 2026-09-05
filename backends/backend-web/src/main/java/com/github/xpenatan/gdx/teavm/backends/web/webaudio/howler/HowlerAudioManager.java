package com.github.xpenatan.gdx.teavm.backends.web.webaudio.howler;

import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import org.teavm.jso.JSBody;

public class HowlerAudioManager implements LifecycleListener {

    public Sound createSound(FileHandle fileHandle) {
        return new HowlSound(fileHandle);
    }

    public Music createMusic(FileHandle fileHandle) {
        return new HowlMusic(fileHandle);
    }
    @Override
    public void pause() {
        if(hasHowlerContext()) {
            suspendAudioContext();
        }
    }

    @Override
    public void resume() {
        if(hasHowlerContext()) {
            resumeAudioContext();
        }
    }

    @Override
    public void dispose() {

    }

    /** Suspends the shared AudioContext. */
    @JSBody(script = "Howler.ctx.suspend();")
    private static native void suspendAudioContext();

    /** Resumes the shared AudioContext. */
    @JSBody(script = "Howler.ctx.resume();")
    private static native void resumeAudioContext();

    /**
     * The {@code typeof Howler} guard exists because howler.js is script-loaded asynchronously
     * while lifecycle events can already fire during startup.
     */
    @JSBody(script = "return typeof Howler !== 'undefined' && Howler.usingWebAudio && !!Howler.ctx;")
    private static native boolean hasHowlerContext();
}
