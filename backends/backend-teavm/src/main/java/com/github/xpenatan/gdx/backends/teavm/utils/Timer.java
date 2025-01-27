package com.github.xpenatan.gdx.backends.teavm.utils;

import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

/**
 * Ported from GWT
 * by xpenatan
 */
public abstract class Timer {

    private boolean isRepeating;

    private Integer timerId = null;

    /**
     * Workaround for broken clearTimeout in IE. Keeps track of whether cancel
     * has been called since schedule was called. See
     * https://code.google.com/p/google-web-toolkit/issues/detail?id=8101
     */
    private int cancelCounter = 0;

    /**
     * Returns {@code true} if the timer is running. Timer is running if and
     * only if it is scheduled but it is not expired or cancelled.
     */
    public final boolean isRunning() {
        return timerId != null;
    }

    /**
     * Cancels this timer. If the timer is not running, this is a no-op.
     */
    public void cancel() {
        if (!isRunning()) {
            return;
        }

        cancelCounter++;
        if (isRepeating) {
            clearInterval(timerId);
        } else {
            clearTimeout(timerId);
        }
        timerId = null;
    }

    /**
     * This method will be called when a timer fires. Override it to implement
     * the timer's logic.
     */
    public abstract void run();

    /**
     * Schedules a timer to elapse in the future. If the timer is already
     * running then it will be first canceled before re-scheduling.
     *
     * @param delayMillis
     *            how long to wait before the timer elapses, in milliseconds
     */
    public void schedule(int delayMillis) {
        if (delayMillis < 0) {
            throw new IllegalArgumentException("must be non-negative");
        }
        if (isRunning()) {
            cancel();
        }
        isRepeating = false;
        timerId = setTimeout(createCallback(this, cancelCounter), delayMillis);
    }

    /**
     * Schedules a timer that elapses repeatedly. If the timer is already
     * running then it will be first canceled before re-scheduling.
     *
     * @param periodMillis
     *            how long to wait before the timer elapses, in milliseconds,
     *            between each repetition
     */
    public void scheduleRepeating(int periodMillis) {
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("must be positive");
        }
        if (isRunning()) {
            cancel();
        }
        isRepeating = true;
        timerId = setInterval(createCallback(this, cancelCounter), periodMillis);
    }

    /*
     * Called by native code when this timer fires.
     *
     * Only call run() if cancelCounter has not changed since the timer was
     * scheduled.
     */
    final void fire(int scheduleCancelCounter) {
        // Workaround for broken clearTimeout in IE.
        if (scheduleCancelCounter != cancelCounter) {
            return;
        }

        if (!isRepeating) {
            timerId = null;
        }

        // Run the timer's code.
        run();
    }

    private static TimerHandler createCallback(Timer timer, int cancelCounter) {
        return new TimerHandler() {
            @Override
            public void onTimer() {
                timer.fire(cancelCounter);
            }
        };
    }

    private static int setInterval(TimerHandler func, int time) {
        return Window.setInterval(func, time);
    }

    private static int setTimeout(TimerHandler func, int time) {
        return Window.setTimeout(func, time);
    }

    private static void clearInterval(int timerId) {
        Window.clearInterval(timerId);
    }

    private static void clearTimeout(int timerId) {
        Window.clearTimeout(timerId);
    }
}