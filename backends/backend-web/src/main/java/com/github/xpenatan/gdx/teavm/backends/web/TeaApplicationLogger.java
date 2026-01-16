package com.github.xpenatan.gdx.teavm.backends.web;

import com.badlogic.gdx.ApplicationLogger;

/**
 * @author xpenatan
 */
public class TeaApplicationLogger implements ApplicationLogger {

    @Override
    public void log(String tag, String message) {
        System.out.println(tag + ": " + message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        System.out.println(tag + ": " + message);
        exception.printStackTrace();
    }

    @Override
    public void error(String tag, String message) {
        System.err.println(tag + ": " + message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        System.err.println(tag + ": " + message);
        exception.printStackTrace();
    }

    @Override
    public void debug(String tag, String message) {
        System.err.println(tag + ": " + message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        System.err.println(tag + ": " + message);
        exception.printStackTrace();
    }
}
