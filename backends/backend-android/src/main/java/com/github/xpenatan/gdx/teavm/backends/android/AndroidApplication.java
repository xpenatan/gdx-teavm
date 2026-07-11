package com.github.xpenatan.gdx.teavm.backends.android;

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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.teavm.backends.shared.SharedApplicationLogger;
import com.github.xpenatan.gdx.teavm.backends.shared.mock.SharedClipboard;
import com.github.xpenatan.gdx.teavm.backends.shared.mock.TeaMemoryPreferences;
import org.teavm.interop.Export;
import org.teavm.runtime.Fiber;

public class AndroidApplication implements Application {
    private static AndroidApplication current;
    private static boolean preserveCallbackExports;

    private final ApplicationListener listener;
    private final AndroidGraphics graphics;
    private final AndroidInput input;
    private final AndroidAudio audio;
    private final AndroidFiles files;
    private final AndroidNet net;
    private final Clipboard clipboard;
    private final Array<Runnable> runnables = new Array<>();
    private final Array<Runnable> executedRunnables = new Array<>();
    private final Array<LifecycleListener> lifecycleListeners = new Array<>();
    private final ObjectMap<String, Preferences> preferences = new ObjectMap<>();
    private ApplicationLogger applicationLogger;
    private int logLevel = LOG_INFO;
    private boolean created;
    private boolean paused;
    private boolean disposed;

    public AndroidApplication(ApplicationListener listener) {
        this(listener, new AndroidApplicationConfiguration());
    }

    public AndroidApplication(ApplicationListener listener, AndroidApplicationConfiguration config) {
        AndroidApplicationConfiguration finalConfig = config == null ? new AndroidApplicationConfiguration() : config;
        this.listener = listener;
        this.graphics = new AndroidGraphics(finalConfig);
        this.input = new AndroidInput();
        this.audio = new AndroidAudio();
        this.files = new AndroidFiles();
        this.net = new AndroidNet();
        this.clipboard = new SharedClipboard();
        setApplicationLogger(new SharedApplicationLogger());
        preserveCallbackExports();

        current = this;
        Gdx.app = this;
        Gdx.graphics = graphics;
        Gdx.input = input;
        Gdx.audio = audio;
        Gdx.files = files;
        Gdx.net = net;
    }

    @Export(name = "gdx_teavm_android_resize")
    public static void resize(int width, int height) {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(() -> app.onResize(width, height));
        }
    }

    @Export(name = "gdx_teavm_android_render")
    public static void render() {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(app::onRender);
        }
    }

    @Export(name = "gdx_teavm_android_pause")
    public static void pause() {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(app::onPause);
        }
    }

    @Export(name = "gdx_teavm_android_resume")
    public static void resume() {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(app::onResume);
        }
    }

    @Export(name = "gdx_teavm_android_dispose")
    public static void dispose() {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(app::onDispose);
        }
    }

    @Export(name = "gdx_teavm_android_touch")
    public static void touch(int type, int pointer, int x, int y, float pressure) {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(() -> app.input.onTouch(type, pointer, x, y, pressure));
        }
    }

    @Export(name = "gdx_teavm_android_key")
    public static void key(int type, int keycode) {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(() -> app.input.onKey(type, keycode));
        }
    }

    @Export(name = "gdx_teavm_android_key_typed")
    public static void keyTyped(char character) {
        AndroidApplication app = current;
        if(app != null) {
            runOnTeaVMFiber(() -> app.input.onKeyTyped(character));
        }
    }

    private static void runOnTeaVMFiber(Fiber.FiberRunner runner) {
        Fiber.start(runner, true);
    }

    private static void preserveCallbackExports() {
        if(preserveCallbackExports) {
            resize(0, 0);
            render();
            pause();
            resume();
            dispose();
            touch(0, 0, 0, 0, 0);
            key(0, 0);
            keyTyped((char)0);
        }
    }

    private void onResize(int width, int height) {
        graphics.resize(width, height);
        if(created && !disposed) {
            listener.resize(width, height);
        }
    }

    private void onRender() {
        if(disposed || paused) {
            return;
        }
        loopRunnables();
        graphics.initiateGL();
        graphics.update();
        if(!created) {
            created = true;
            listener.create();
            listener.resize(graphics.getWidth(), graphics.getHeight());
        }
        listener.render();
        input.endFrame();
    }

    private void onPause() {
        if(disposed || paused) {
            return;
        }
        paused = true;
        for(int i = 0; i < lifecycleListeners.size; i++) {
            lifecycleListeners.get(i).pause();
        }
        if(created) {
            listener.pause();
        }
    }

    private void onResume() {
        if(disposed) {
            return;
        }
        boolean wasPaused = paused;
        paused = false;
        if(created && wasPaused) {
            for(int i = 0; i < lifecycleListeners.size; i++) {
                lifecycleListeners.get(i).resume();
            }
            listener.resume();
        }
    }

    private void onDispose() {
        if(disposed) {
            return;
        }
        disposed = true;
        if(created && !paused) {
            listener.pause();
        }
        for(int i = 0; i < lifecycleListeners.size; i++) {
            lifecycleListeners.get(i).dispose();
        }
        lifecycleListeners.clear();
        if(created) {
            listener.dispose();
        }
        audio.dispose();
        current = null;
    }

    private void loopRunnables() {
        if(runnables.size == 0) {
            return;
        }
        executedRunnables.clear();
        executedRunnables.addAll(runnables);
        runnables.clear();
        for(int i = 0; i < executedRunnables.size; i++) {
            executedRunnables.get(i).run();
        }
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public Net getNet() {
        return net;
    }

    @Override
    public void debug(String tag, String message) {
        if(logLevel >= LOG_DEBUG) {
            getApplicationLogger().debug(tag, message);
        }
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if(logLevel >= LOG_DEBUG) {
            getApplicationLogger().debug(tag, message, exception);
        }
    }

    @Override
    public void log(String tag, String message) {
        if(logLevel >= LOG_INFO) {
            getApplicationLogger().log(tag, message);
        }
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if(logLevel >= LOG_INFO) {
            getApplicationLogger().log(tag, message, exception);
        }
    }

    @Override
    public void error(String tag, String message) {
        if(logLevel >= LOG_ERROR) {
            getApplicationLogger().error(tag, message);
        }
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if(logLevel >= LOG_ERROR) {
            getApplicationLogger().error(tag, message, exception);
        }
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
        return ApplicationType.Android;
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
        Preferences prefs = preferences.get(name);
        if(prefs == null) {
            prefs = new TeaMemoryPreferences();
            preferences.put(name, prefs);
        }
        return prefs;
    }

    @Override
    public Clipboard getClipboard() {
        return clipboard;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        runnables.add(runnable);
    }

    @Override
    public void exit() {
        onDispose();
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.removeValue(listener, true);
    }
}
