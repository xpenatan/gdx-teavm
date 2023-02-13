package com.github.xpenatan.gdx.backends.teavm.dom.impl;

import com.github.xpenatan.gdx.backends.teavm.dom.TimerWrapper;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

public class TeaTimer extends TimerWrapper {

    private TimerHandler createCallback(int cancelCounter) {
        TimerHandler handler = new TimerHandler() {
            @Override
            public void onTimer() {
                fire(cancelCounter);
            }
        };
        return handler;
    }

    @Override
    protected int setTimeout(int cancelCounter, int delayMillis) {
        return Window.setTimeout(createCallback(cancelCounter), delayMillis);
    }

    @Override
    protected int setInterval(int cancelCounter, int periodMillis) {
        return Window.setInterval(createCallback(cancelCounter), periodMillis);
    }

    @Override
    protected void clearInterval(int timerId) {
        Window.clearInterval(timerId);
    }

    @Override
    protected void clearTimeout(int timerId) {
        Window.clearTimeout(timerId);
    }
}
