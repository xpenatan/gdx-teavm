package com.github.xpenatan.gdx.teavm.backends.psp;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPCoreApi;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPDebugApi;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi;
import com.github.xpenatan.gdx.teavm.backends.shared.SharedApplicationLogger;

public class PSPApplication implements Application {

    private PSPGraphics graphics;
    private ApplicationListener applicationListener;
    private ApplicationLogger applicationLogger;

    private int logLevel = LOG_INFO;

    private boolean applicationInitialized = false;

    private PSPApplicationConfiguration config;

    public PSPApplication(ApplicationListener listener) {
        this(listener, new PSPApplicationConfiguration());
    }

    public PSPApplication(ApplicationListener listener, PSPApplicationConfiguration config) {
        this.config = config;
        PSPCoreApi.init();
        PSPGraphicsApi.grInitGraphics();

        graphics = new PSPGraphics();
        applicationListener = listener;
        setApplicationLogger(new SharedApplicationLogger());

        Gdx.app = this;
        Gdx.graphics = graphics;

        try {
            while(PSPCoreApi.isRunning()) {
                loop();
            }
            applicationListener.pause();
            applicationListener.dispose();
        }
        catch(Throwable t) {
            t.printStackTrace(System.out);
        }
        PSPGraphicsApi.sceGuDisplay(PSPGraphicsApi.GU_FALSE);
        PSPGraphicsApi.sceGuTerm();
    }

    private void loop() {
        if (config.logMemory) {
            PSPDebugApi.logUsedMemory(config.logMemoryDelayMilli);
        }

        int dialog = PSPGraphicsApi.GU_FALSE;
        PSPGraphicsApi.grStartFrame(dialog);
        graphics.update();
        if(!applicationInitialized) {
            applicationInitialized = true;
            applicationListener.create();
            applicationListener.resize(PSPGraphicsApi.GU_SCR_WIDTH, PSPGraphicsApi.GU_SCR_HEIGHT);
        }
        else {
            applicationListener.render();
        }

        int vsync = PSPGraphicsApi.GU_TRUE;
        PSPGraphicsApi.grSwapBuffers(vsync, dialog);
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return applicationListener;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return null;
    }

    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Files getFiles() {
        return null;
    }

    @Override
    public Net getNet() {
        return null;
    }

    @Override
    public void debug(String tag, String message) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
    }

    @Override
    public void log(String tag, String message) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return applicationLogger;
    }

    @Override
    public ApplicationType getType() {
        return null;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return 0;
    }

    @Override
    public long getNativeHeap() {
        return 0;
    }

    @Override
    public Preferences getPreferences(String name) {
        return null;
    }

    @Override
    public Clipboard getClipboard() {
        return null;
    }

    @Override
    public void postRunnable(Runnable runnable) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }
}
