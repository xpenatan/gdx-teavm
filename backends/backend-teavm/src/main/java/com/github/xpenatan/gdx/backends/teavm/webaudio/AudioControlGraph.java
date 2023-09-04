package com.github.xpenatan.gdx.backends.teavm.webaudio;


import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * Port from GWT gdx 1.12.0
 *
 * @author xpenatan
 */
public class AudioControlGraph {
    private final JSObject audioContext;
    private JSObject destinationNode;

    private JSObject gainNode;
    private JSObject panNode;

    public AudioControlGraph(JSObject audioContext, JSObject destinationNode) {
        this.audioContext = audioContext;
        this.destinationNode = destinationNode;

        panNode = setupPanNode(audioContext);
        gainNode = setupAudioGraph(audioContext, panNode, destinationNode);
    }

    @JSBody(params = { "audioContext" }, script = "" +
            "var panNode = null;" +
            "if (audioContext.createStereoPanner) {\n" +
            "   panNode = audioContext.createStereoPanner();\n" +
            "   panNode.pan.value = 0;\n" +
            "}" +
            "return panNode;"
    )
    public static native JSObject setupPanNode(JSObject audioContext);

    @JSBody(params = { "audioContext", "panNode", "destinationNode" }, script = "" +
            "var gainNode = null;" +
            "if(audioContext.createGain)" +
            "   gainNode = audioContext.createGain();" +
            "else" +
            "   gainNode = audioContext.createGainNode();" +
            "gainNode.gain.value = 1;" +
            "if(panNode) {" +
            "   panNode.connect(gainNode)" +
            "}" +
            "gainNode.connect(destinationNode);" +
            "return gainNode;"
    )
    public static native JSObject setupAudioGraph(JSObject audioContext, JSObject panNode, JSObject destinationNode);

    @JSBody(params = { "sourceNode", "panNode", "gainNode" }, script = "" +
            "if (panNode) {" +
            "   sourceNode.connect(panNode);" +
            "} else {" +
            "   sourceNode.connect(gainNode);" +
            "}"
    )
    public static native void setSourceJSNI(JSObject sourceNode, JSObject panNode, JSObject gainNode);

    public void setVolume(float volume) {
        setVolumeJSNI(volume, gainNode);
    }

    public float getVolume() {
        return getVolumeJSNI(gainNode);
    }

    @JSBody(params = { "volume", "gainNode" }, script = "" +
            "gainNode.gain.value = volume;"
    )
    public static native void setVolumeJSNI(float volume, JSObject gainNode);

    @JSBody(params = { "gainNode" }, script = "" +
            "return gainNode.gain.value;"
    )
    public static native float getVolumeJSNI(JSObject gainNode);

    public void setPan(float pan) {
        setPanJSNI(pan, panNode);
    }

    public float getPan() {
        return getPanJSNI(panNode);
    }

    @JSBody(params = { "pan", "panNode" }, script = "" +
            "if(panNode)" +
            "   panNode.pan.value = pan"
    )
    public static native void setPanJSNI(float pan, JSObject panNode);

    @JSBody(params = { "panNode" }, script = "" +
            "if(panNode)" +
            "   return panNode.pan.value;" +
            "return 0;"
    )
    public static native float getPanJSNI(JSObject panNode);

    public void setSource(JSObject sourceNode) {
        setSourceJSNI(sourceNode, panNode, gainNode);
    }
}