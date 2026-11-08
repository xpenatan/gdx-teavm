package com.github.xpenatan.gdx.teavm.backends.web.webaudio.howler;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays;
import org.teavm.jso.typedarrays.ArrayBufferView;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class HowlMusic implements Music {

    private Howl howl;
    private int soundId = -1;
    /** True while play is requested but Howler has not yet confirmed playback. */
    private boolean playPending;
    private boolean waitingForUnlock;
    private HowlEventFunctionWithArgs onPlay;
    private HowlEventFunctionWithArgs onEnd;
    private HowlEventFunctionWithArgs onLoadError;
    private HowlEventFunctionWithArgs onPlayError;
    private HowlEventFunctionWithArgs onUnlock;
    private HowlEventFunction onCompletion;

    public HowlMusic(FileHandle fileHandle) {
        byte[] bytes = fileHandle.readBytes();
        ArrayBufferView data = TypedArrays.getInt8Array(bytes);
        howl = Howl.create(data);
        registerPlaybackEvents();
    }

    private void registerPlaybackEvents() {
        onPlay = (id, data) -> {
            if (id == soundId && howl.isPlaying(id)) {
                playPending = false;
                waitingForUnlock = false;
            }
        };
        onEnd = (id, data) -> {
            if (howl != null && id == soundId && !howl.getLoop(id)) {
                playPending = false;
                waitingForUnlock = false;
                soundId = -1;
            }
        };
        onLoadError = (id, data) -> {
            playPending = false;
            waitingForUnlock = false;
            soundId = -1;
        };
        onPlayError = (id, data) -> {
            if (id != soundId) return;
            if (isAutoplayBlocked(data)) {
                waitingForUnlock = true;
            } else {
                playPending = false;
                waitingForUnlock = false;
                soundId = -1;
            }
        };
        onUnlock = (id, data) -> {
            if (playPending && waitingForUnlock && soundId >= 0) {
                waitingForUnlock = false;
                howl.play(soundId);
            }
        };

        howl.on("play", onPlay);
        howl.on("end", onEnd);
        howl.on("loaderror", onLoadError);
        howl.on("playerror", onPlayError);
        howl.on("unlock", onUnlock);
    }

    private static boolean isAutoplayBlocked(JSObject error) {
        return error != null && stringify(error).contains("not within a user interaction");
    }

    @JSBody(params = {"error"}, script = "return String(error);")
    private static native String stringify(JSObject error);

    @Override
    public void play() {
        if (howl != null && !isPlaying()) {
            playPending = true;
            waitingForUnlock = false;
            if (soundId < 0) soundId = howl.play();
            else howl.play(soundId);
        }
    }

    @Override
    public void pause() {
        playPending = false;
        waitingForUnlock = false;
        if (howl != null) howl.pause();
    }

    @Override
    public void stop() {
        playPending = false;
        waitingForUnlock = false;
        soundId = -1;
        if (howl != null) howl.stop();
    }

    @Override
    public boolean isPlaying() {
        return howl != null && (playPending || (soundId >= 0 && howl.isPlaying(soundId)));
    }

    @Override
    public void setLooping(boolean isLooping) {
        howl.setLoop(isLooping);
    }

    @Override
    public boolean isLooping() {
        return howl.getLoop();
    }

    @Override
    public void setVolume(float volume) {
        howl.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return howl.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        howl.setStereo(pan);
        howl.setVolume(volume);
    }

    @Override
    public void setPosition(float position) {
        howl.setSeek(position);
    }

    @Override
    public float getPosition() {
        return howl.getSeek();
    }

    @Override
    public void dispose() {
        playPending = false;
        waitingForUnlock = false;
        soundId = -1;
        if (howl == null) return;
        howl.off("play", onPlay);
        howl.off("end", onEnd);
        howl.off("loaderror", onLoadError);
        howl.off("playerror", onPlayError);
        howl.off("unlock", onUnlock);
        if (onCompletion != null) howl.off("end", onCompletion);
        howl.stop();
        howl.unload();
        howl = null;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        if (howl == null) return;
        if (onCompletion != null) howl.off("end", onCompletion);
        onCompletion = listener == null ? null : () -> listener.onCompletion(HowlMusic.this);
        if (onCompletion != null) howl.on("end", onCompletion);
    }
}
