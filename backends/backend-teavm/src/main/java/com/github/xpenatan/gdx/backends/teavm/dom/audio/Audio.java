package com.github.xpenatan.gdx.backends.teavm.dom.audio;

import org.teavm.jso.JSBody;
import org.teavm.jso.dom.html.HTMLAudioElement;

public class Audio {

    public static HTMLAudioElement createIfSupported() {
        //TODO
//        if (detector == null) {
//            detector = GWT.create(AudioElementSupportDetector.class);
//        }
//        if (!detector.isSupportedCompileTime()) {
//            return null;
//        }
//        AudioElement element = Document.get().createAudioElement();
//        if (!detector.isSupportedRunTime(element)) {
//            return null;
//        }
        return createAudio();
    }

    @JSBody(script = "return new Audio();")
    private static native HTMLAudioElement createAudio();
}