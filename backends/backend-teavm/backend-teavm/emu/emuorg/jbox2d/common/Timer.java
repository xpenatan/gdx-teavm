package emuorg.jbox2d.common;

import org.teavm.jso.JSBody;

public class Timer {

    private double resetMillis;

    public Timer() {
        reset();
    }

    public void reset() {
        resetMillis = now();
    }

    public float getMilliseconds() {
        return (float)(now() - resetMillis);
    }

    @JSBody(script = "return Date.now()")
    private static native double now();
}
