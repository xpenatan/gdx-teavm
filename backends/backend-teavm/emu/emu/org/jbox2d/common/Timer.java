package emu.org.jbox2d.common;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import org.teavm.jso.JSBody;

@Emulate(valueStr = "org.jbox2d.common.Timer")
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
