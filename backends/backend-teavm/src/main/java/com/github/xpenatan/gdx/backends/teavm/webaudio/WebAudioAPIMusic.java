package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.audio.Music;
import com.github.xpenatan.gdx.backends.teavm.dom.audio.HTMLAudioElementWrapper;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 * Port from GWT gdx 1.12.0
 */
public class WebAudioAPIMusic implements Music {
    // The Audio element to be streamed
    private final HTMLAudioElementWrapper audio;

    // Pool from which we will draw, and eventually return to, our AudioControlGraph from
    private final AudioControlGraphPool audioControlGraphPool;

    // The audio graph used to control pan and volume for this piece of music
    private final AudioControlGraph audioControlGraph;

    private OnCompletionListener onCompletionListener;

    public WebAudioAPIMusic(JSObject audioContext, HTMLAudioElementWrapper audio, AudioControlGraphPool audioControlGraphPool) {
        this.audio = audio;
        this.audioControlGraphPool = audioControlGraphPool;

        EndedFunction endedFunction = new EndedFunction() {
            @Override
            public void endedFunc() {
                ended();
            }
        };

        // Create AudioSourceNode from Audio element
        JSObject audioSourceNode = createMediaElementAudioSourceNode(audioContext, audio, endedFunction);

        // Setup the sound graph to control pan and volume
        audioControlGraph = audioControlGraphPool.obtain();
        audioControlGraph.setSource(audioSourceNode);
    }

    public void ended() {
        if(this.onCompletionListener != null) this.onCompletionListener.onCompletion(this);
    }

    @JSFunctor
    public interface EndedFunction extends JSObject {
        void endedFunc();
    }

    @JSBody(params = { "audioContext", "audioElement", "endedFunction" }, script = "" +
            "var source = audioContext.createMediaElementSource(audioElement);" +
            "audioElement.addEventListener('ended', endedFunction);" +
            "return source;"
    )
    public static native JSObject createMediaElementAudioSourceNode(JSObject audioContext, JSObject audioElement, EndedFunction endedFunction);

    @Override
    public void play() {
        audio.play();
    }

    @Override
    public void pause() {
        audio.pause();
    }

    @Override
    public void stop() {
        pause();
        audio.setCurrentTime(0);
    }

    @Override
    public boolean isPlaying() {
        return !audio.isPaused();
    }

    @Override
    public void setLooping(boolean isLooping) {
        audio.setLoop(isLooping);
    }

    @Override
    public boolean isLooping() {
        return audio.isLoop();
    }

    @Override
    public void setVolume(float volume) {
        // Volume can be controlled on the Audio element, or as part of the audio graph. We do it as part of the graph to ensure we
        // use as much common
        // code as possible with the sound effect code.
        audioControlGraph.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return audioControlGraph.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        audioControlGraph.setPan(pan);
        audioControlGraph.setVolume(volume);
    }

    @Override
    public void setPosition(float position) {
        audio.setCurrentTime(position);
    }

    @Override
    public float getPosition() {
        return (float)audio.getCurrentTime();
    }

    @Override
    public void dispose() {
        // Stop the music
        pause();

        // Tear down the audio graph
        audioControlGraphPool.free(audioControlGraph);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }
}