package com.github.xpenatan.gdx.backends.teavm.soundmanager;

/**
 * @author xpenatan
 */
public class SMSoundOptions {
    public SMSoundOptions() {
    }

    public int volume = 100;
    public int pan = 0;
    public int loops = 1;
    public int from = 0;
    public SMSoundCallbackWrapper callback = null;
}
