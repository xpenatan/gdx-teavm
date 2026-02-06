package com.github.xpenatan.gdx.teavm.backends.web;

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
import com.github.xpenatan.gdx.teavm.backends.web.agent.WebAgentInfo;
import com.github.xpenatan.gdx.teavm.backends.web.agent.WebWebAgent;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetDownloadImpl;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetInstance;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoadImpl;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoaderListener;
import com.github.xpenatan.gdx.teavm.backends.web.dom.impl.WebWindow;
import com.github.xpenatan.gdx.teavm.backends.web.utils.WebNavigator;
import com.github.xpenatan.gdx.teavm.backends.web.webaudio.howler.HowlTeaAudio;
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
public class WebApplication implements Application {

    private static String WEB_SCRIPT_PATH = "WEB_SCRIPT_PATH";
    private static String WEB_ASSET_PATH = "WEB_ASSET_PATH";

    private static WebAgentInfo agentInfo;

    private int initQueue;

    public static WebAgentInfo getAgentInfo() {
        return agentInfo;
    }

    public static WebApplication get() {
        return (WebApplication)Gdx.app;
    }

    private WebGraphics graphics;
    private WebInput input;
    private WebFiles files;
    private WebNet net;
    private WebApplicationConfiguration config;
    private ApplicationListener appListener;
    private ApplicationListener curListener;
    private final Array<LifecycleListener> lifecycleListeners = new Array<>(4);
    private WebWindow window;

    private AppState initState = AppState.INIT;
    private boolean isPreloadReady = false;

    private int lastWidth = -1;
    private int lastHeight = 1;

    private ApplicationLogger logger;
    private int logLevel = LOG_INFO;

    private AssetLoadImpl assetLoader;

    private ObjectMap<String, Preferences> prefs = new ObjectMap<>();

    private WebClipboard clipboard;

    private final Array<Runnable> runnables = new Array<>();
    private final Array<Runnable> runnablesHelper = new Array<>();

    private boolean stepError;

    public WebApplication(ApplicationListener appListener, WebApplicationConfiguration config) {
        this(appListener, null, config);
    }

    public WebApplication(ApplicationListener appListener, ApplicationListener preloadAppListener, WebApplicationConfiguration config) {
        this.window = WebWindow.get();
        this.config = config;
        this.appListener = appListener;
        if(preloadAppListener == null) {
            preloadAppListener = createDefaultPreloadAppListener();
        }
        curListener = preloadAppListener;
        init();
    }

    protected ApplicationListener createDefaultPreloadAppListener() {
        return new WebPreloadApplicationListener();
    }

    protected void init() {
        updateBrowserErrorStack();
        WebApplication.agentInfo = WebWebAgent.computeAgentInfo();
        System.setProperty("java.runtime.name", "");
        System.setProperty("userAgent", WebApplication.agentInfo.getUserAgent());
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

        input = new WebInput(this, graphics.canvas);
        files = new WebFiles(config, this);
        net = new WebNet();
        logger = new WebApplicationLogger();
        clipboard = new WebClipboard();

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
                if(stepError) {
                    return;
                }
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
                if(stepError) {
                    return;
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
            stepError = true;
            onError(t);
        }
    }

    protected void onError(Throwable error) {
        printErrorStack(error);
    }

    public static void printErrorStack(Object obj) {
        Throwable error = (Throwable)obj;
        ArrayList<JSObject> errors = new ArrayList<>();
        ArrayList<String> throwables = new ArrayList<>();
        Throwable root = error;
        while(root != null) {
            JSObject jsException = JSExceptions.getJSException(root);
            errors.add(0, jsException);
            String msg = root.getMessage();
            if(msg == null) msg = "";
            throwables.add(0, root.getClass().getSimpleName() + " " + msg);
            root = root.getCause();
        }
        JSObject[] errorsJS = new JSObject[errors.size()];
        String[] exceptions = new String[errors.size()];
        errors.toArray(errorsJS);
        throwables.toArray(exceptions);
        groupCollapsed("%cFatal Error" + ": " + exceptions[0], "color: #FF0000");

        for(int i = 0; i < errorsJS.length; i++) {
            int count = i + 1;
            JSObject errorJS = errorsJS[i];
            if(i > 0) { // Already logged the first one
                consoleLog("%cException " + count + ": " + exceptions[i], "color: #FF0000");
            }
            consoleLogError(errorJS);
        }
        groupEnd();
    }

    @JSBody(params = { "error" }, script = "console.log(error);")
    private static native void consoleLogError(JSObject error);

    @JSBody(script = "" +
            "if(typeof Error.stackTraceLimit !== 'undefined') {\n" +
            "  Error.stackTraceLimit = Infinity;\n" +
            "}")
    private static native void updateBrowserErrorStack();

    @JSBody(params = { "msg" , "param"}, script = "console.log(msg, param);")
    private static native void consoleLog(String msg, String param);

    @JSBody(params = { "text", "param" }, script = "console.groupCollapsed(text, param);")
    private static native void groupCollapsed(String text, String param);

    @JSBody(script = "console.groupEnd();")
    private static native void groupEnd();

    public WebApplicationConfiguration getConfig() {
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
            pref = new WebPreferences(storage, config.storagePrefix + ":" + name, config.shouldEncodePreference);
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
        Matcher m = p.matcher(WebNavigator.getUserAgent().toLowerCase());
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

    protected WebGraphics createGraphics (WebApplicationConfiguration config) {
        return new WebGLGraphics(config);
    }
}