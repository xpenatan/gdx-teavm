package com.github.xpenatan.gdx.backends.teavm;

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
import com.github.xpenatan.gdx.backends.teavm.agent.TeaAgentInfo;
import com.github.xpenatan.gdx.backends.teavm.agent.TeaWebAgent;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetDownloadImpl;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetInstance;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoadImpl;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.utils.TeaNavigator;
import com.github.xpenatan.gdx.backends.teavm.webaudio.howler.HowlTeaAudio;
import com.github.xpenatan.jmultiplatform.core.JMultiplatform;
import com.github.xpenatan.jmultiplatform.core.JPlatformMap;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Storage;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;

/**
 * @author xpenatan
 */
public class TeaApplication implements Application {

    private static String WEB_SCRIPT_PATH = "WEB_SCRIPT_PATH";
    private static String WEB_ASSET_PATH = "WEB_ASSET_PATH";

    private static TeaAgentInfo agentInfo;

    private int initQueue;

    public static TeaAgentInfo getAgentInfo() {
        return agentInfo;
    }

    public static TeaApplication get() {
        return (TeaApplication)Gdx.app;
    }

    private TeaGraphics graphics;
    private TeaInput input;
    private TeaFiles files;
    private TeaNet net;
    private TeaApplicationConfiguration config;
    private ApplicationListener appListener;
    private ApplicationListener curListener;
    private final Array<LifecycleListener> lifecycleListeners = new Array<>(4);
    private TeaWindow window;

    private AppState initState = AppState.INIT;
    private boolean isPreloadReady = false;

    private int lastWidth = -1;
    private int lastHeight = 1;

    private ApplicationLogger logger;
    private int logLevel = LOG_INFO;

    private AssetLoadImpl assetLoader;

    private ObjectMap<String, Preferences> prefs = new ObjectMap<>();

    private TeaClipboard clipboard;

    private final Array<Runnable> runnables = new Array<>();
    private final Array<Runnable> runnablesHelper = new Array<>();

    public TeaApplication(ApplicationListener appListener, TeaApplicationConfiguration config) {
        this(appListener, null, config);
    }

    public TeaApplication(ApplicationListener appListener, ApplicationListener preloadAppListener, TeaApplicationConfiguration config) {
        this.window = TeaWindow.get();
        this.config = config;
        this.appListener = appListener;
        if(preloadAppListener == null) {
            preloadAppListener = createDefaultPreloadAppListener();
        }
        curListener = preloadAppListener;
        init();
    }

    protected ApplicationListener createDefaultPreloadAppListener() {
        return new TeaPreloadApplicationListener();
    }

    protected void init() {
        TeaApplication.agentInfo = TeaWebAgent.computeAgentInfo();
        System.setProperty("java.runtime.name", "");
        System.setProperty("userAgent", TeaApplication.agentInfo.getUserAgent());
        if(agentInfo.isWindows())
            System.setProperty("os.name", "Windows");
        else if(agentInfo.isMacOS())
            System.setProperty("os.name", "OS X");
        else if(agentInfo.isLinux())
            System.setProperty("os.name", "Linux");
        else
            System.setProperty("os.name", "no OS");

        AssetDownloadImpl assetDownload = new AssetDownloadImpl(config.showDownloadLogs);
        AssetInstance.setInstance(assetDownload);
        String hostPageBaseURL = config.baseUrlProvider.getBaseUrl();
        assetLoader = new AssetLoadImpl(hostPageBaseURL, this, assetDownload);
        AssetInstance.setInstance(assetLoader);

        JMultiplatform instance = JMultiplatform.getInstance();
        JPlatformMap map = instance.getMap();
        map.put(WEB_SCRIPT_PATH, assetLoader.getScriptUrl());
        map.put(WEB_ASSET_PATH, assetLoader.getAssetUrl());

        graphics = createGraphics(config);
        assetLoader.setupFileDrop(graphics.canvas, this);

        input = new TeaInput(this, graphics.canvas);
        files = new TeaFiles(config, this);
        net = new TeaNet();
        logger = new TeaApplicationLogger();
        clipboard = new TeaClipboard();

        initNativeLibraries();

        if(config.preloadListener != null) {
            config.preloadListener.onPreload(assetLoader);
        }

        Gdx.app = this;
        Gdx.graphics = graphics;
        Gdx.gl = graphics.getGL20();
        Gdx.gl20 = graphics.getGL20();
        Gdx.gl30 = graphics.getGL30();
        Gdx.input = input;
        Gdx.files = files;
        Gdx.net = net;

        window.addEventListener("pagehide", new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                if(curListener != null) {
                    curListener.pause();
                    curListener.dispose();
                    curListener = null;
                }
            }
        });

        window.getDocument().addEventListener("visibilitychange", new EventListener<Event>() {
            @Override
            public void handleEvent(Event evt) {
                // notify of state change
                if(initState == AppState.APP_LOOP) {
                    String state = window.getDocument().getVisibilityState();
                    if (state.equals("hidden")) {
                        // hidden: i.e. we are paused
                        synchronized (lifecycleListeners) {
                            for (LifecycleListener listener : lifecycleListeners) {
                                listener.pause();
                            }
                        }
                        curListener.pause();
                    }
                    else if(state.equals("visible")){
                        // visible: i.e. we resume
                        synchronized (lifecycleListeners) {
                            for (LifecycleListener listener : lifecycleListeners) {
                                listener.resume();
                            }
                        }
                        curListener.resume();
                    }
                }
            }
        });

        if(config.isAutoSizeApplication()) {
            window.addEventListener("resize", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    int width = window.getClientWidth() - config.padHorizontal;
                    int height = window.getClientHeight() - config.padVertical;

                    if(width <= 0 || height <= 0) {
                        return;
                    }

                    if(graphics != null) {
                        graphics.setCanvasSize(width, height, config.usePhysicalPixels);
                    }
                }
            });
        }

        Runnable appLoop = new Runnable() {
            @Override
            public void run() {
                graphics.update();
                step(curListener);
                window.requestAnimationFrame(this);
            }
        };

        Runnable initialization = new Runnable() {
            @Override
            public void run() {
                AppState state = initState;
                graphics.update();
                switch(state) {
                    case APP_LOOP:
                        step(curListener);
                        break;
                    case INIT:
                        if(initQueue == 0) {
                            step(curListener);
                        }
                        break;
                }
                if(isPreloadReady) {
                    initState = AppState.INIT;
                    curListener.dispose();
                    curListener = appListener;
                    appListener = null;
                    window.requestAnimationFrame(appLoop);
                }
                else {
                    window.requestAnimationFrame(this);
                }
            }
        };
        window.requestAnimationFrame(initialization);
    }

    private void step(ApplicationListener appListener) {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        boolean resizeBypass = false;

        try {

            if(initState == AppState.INIT) {
                initState = AppState.APP_LOOP;
                input.setInputProcessor(null);
                input.reset();
                runnables.clear();
                graphics.frameId = 0;
                appListener.create();
                resizeBypass = true;
            }

            if((width != lastWidth || height != lastHeight) || resizeBypass) {
                lastWidth = width;
                lastHeight = height;
                graphics.resize(appListener, width, height);
            }

            runnablesHelper.addAll(runnables);
            runnables.clear();
            for(int i = 0; i < runnablesHelper.size; i++) {
                runnablesHelper.get(i).run();
            }
            runnablesHelper.clear();
            graphics.frameId++;
            if(graphics.frameId > 60) { // A bit of delay before rendering so fps don't start with 0
                graphics.render(appListener);
            }
            input.reset();

        } catch(Throwable t) {
            onError(t);
        }
    }

    protected void onError(Throwable error) {
        ArrayList<JSObject> errors = new ArrayList<>();
        ArrayList<String> throwables = new ArrayList<>();
        Throwable root = error;
        while(root != null) {
            JSObject jsException = JSExceptions.getJSException(root);
            errors.add(jsException);
            String msg = root.getMessage();
            if(msg == null) msg = "";
            throwables.add(root.getClass().getSimpleName() + " " + msg);
            root = root.getCause();
        }
        JSObject[] errorsJS = new JSObject[errors.size()];
        String[] exceptions = new String[errors.size()];
        errors.toArray(errorsJS);
        throwables.toArray(exceptions);
        printStack(errorsJS, exceptions);
    }

    @JSBody(params = { "errors", "exceptions" }, script = "" +
            "console.groupCollapsed('%cFatal Error', 'color: #FF0000');" +
            "errors.forEach((error, i) => {\n" +
            "   var count = i + 1;" +
            "   console.log('%cException ' + count + ': ' + exceptions[i], 'color: #FF0000');" +
            "   console.log(error);" +
            "});" +
            "console.groupEnd();"
    )
    private static native void printStack(JSObject[] errors, String[] exceptions);

    public TeaApplicationConfiguration getConfig() {
        return config;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return curListener;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Deprecated
    @Override
    public Audio getAudio() {
        return null;
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
    public void log(String tag, String message) {
        if(logLevel >= LOG_INFO) getApplicationLogger().log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if(logLevel >= LOG_INFO) getApplicationLogger().log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        if(logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if(logLevel >= LOG_ERROR) getApplicationLogger().error(tag, message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        if(logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if(logLevel >= LOG_DEBUG) getApplicationLogger().debug(tag, message, exception);
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
        this.logger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return logger;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.WebGL;
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
        Preferences pref = prefs.get(name);
        if(pref == null) {
            Storage storage = Storage.getLocalStorage();;
            pref = new TeaPreferences(storage, config.storagePrefix + ":" + name, config.shouldEncodePreference);
            prefs.put(name, pref);
        }
        return pref;
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
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.add(listener);
        }
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            lifecycleListeners.removeValue(listener, true);
        }
    }

    /** @return {@code true} if application runs on a mobile device */
    public static boolean isMobileDevice () {
        // RegEx pattern from detectmobilebrowsers.com (public domain)
        String pattern = "(android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec"
                + "|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)"
                + "i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)"
                + "|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(TeaNavigator.getUserAgent().toLowerCase());
        return m.matches();
    }


    public void setPreloadReady() {
        isPreloadReady = true;
    }

    public int getInitQueue() {
        return initQueue;
    }

    public void addInitQueue() {
        initQueue++;
    }

    public void subtractInitQueue() {
        initQueue--;
    }

    public enum AppState {
        INIT,
        APP_LOOP
    }

    // Testing code only
    @JSBody(params = "text", script = "console.log(text);" )
    public static native void print(String text);

    // ##################### NATIVE CALLS #####################

    private void initNativeLibraries() {
        initGdxLibrary();
        initAudio();
    }

    protected void initGdxLibrary() {
        addInitQueue();
        assetLoader.loadScript("gdx.wasm.js", new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, String result) {
                subtractInitQueue();
            }

            @Override
            public void onFailure(String url) {
                throw new RuntimeException("Gdx script failed to load");
            }
        });
    }

    protected void initAudio() {
        addInitQueue();
        assetLoader.loadScript("howler.js", new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, String result) {
                subtractInitQueue();
                Gdx.audio = new HowlTeaAudio();
            }
        });
    }

    protected TeaGraphics createGraphics (TeaApplicationConfiguration config) {
        return new TeaGraphics(config);
    }
}