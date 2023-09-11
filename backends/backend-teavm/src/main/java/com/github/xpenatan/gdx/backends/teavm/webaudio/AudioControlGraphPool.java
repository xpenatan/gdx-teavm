package com.github.xpenatan.gdx.backends.teavm.webaudio;

import com.badlogic.gdx.utils.Pool;
import org.teavm.jso.JSObject;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class AudioControlGraphPool extends Pool<AudioControlGraph> {
	public JSObject audioContext;
	public JSObject destinationNode;

	public AudioControlGraphPool(JSObject audioContext, JSObject destinationNode) {
		this.audioContext = audioContext;
		this.destinationNode = destinationNode;
	}

	@Override
	protected AudioControlGraph newObject () {
		return new AudioControlGraph(audioContext, destinationNode);
	}
}