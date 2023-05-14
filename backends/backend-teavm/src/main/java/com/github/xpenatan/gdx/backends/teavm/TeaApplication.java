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
import com.github.xpenatan.gdx.backends.teavm.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.EventWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloadImpl;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloader;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloader.AssetDownload;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.SoundManagerCallback;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.TeaSoundManager;
import org.teavm.jso.browser.Storage;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLElement;

/**
 * @author xpenatan
 */
public class TeaApplication implements Application, Runnable {

    private static TeaAgentInfo agentInfo;

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
    private TeaAudio audio;
    private TeaApplicationConfiguration config;
    private ApplicationListener queueAppListener;
    private ApplicationListener appListener;
    private final Array<LifecycleListener> lifecycleListeners = new Array<LifecycleListener>(4);
    private TeaWindow window;

    private AppState initState = AppState.LOAD_ASSETS;

    private int lastWidth = -1;
    private int lastHeight = 1;

    private ApplicationLogger logger;
    private int logLevel = LOG_ERROR;

    private Preloader preloader;

    private ObjectMap<String, Preferences> prefs = new ObjectMap<>();

    private TeaClipboard clipboard;

    private Array<Runnable> runnables = new Array<Runnable>();
    private Array<Runnable> runnablesHelper = new Array<Runnable>();

    private String hostPageBaseURL;

    public TeaApplication(ApplicationListener appListener, TeaApplicationConfiguration config) {
        this.window = TeaWindow.get();
        this.config = config;
        setApplicationListener(appListener);
        init();
    }

    private void init() {
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

        AssetDownloader.setInstance(new AssetDownloadImpl(config.showDownloadLogs));

        AssetDownload instance = AssetDownloader.getInstance();
        hostPageBaseURL = instance.getHostPageBaseURL();

        if(hostPageBaseURL.contains(".html")) {
            // TODO use regex
            hostPageBaseURL = hostPageBaseURL.replace("index.html", "");
            hostPageBaseURL = hostPageBaseURL.replace("index-debug.html", "");
        }
        int indexQM = hostPageBaseURL.indexOf('?');
        if (indexQM >= 0) {
          hostPageBaseURL = hostPageBaseURL.substring(0, indexQM);
        }
        graphics = new TeaGraphics(config);

        preloader = new Preloader(hostPageBaseURL, graphics.canvas, this);
        AssetLoaderListener<Object> assetListener = new AssetLoaderListener();
        preloader.preload(config.preloadAssets, "assets.txt");

        input = new TeaInput(graphics.canvas);
        files = new TeaFiles(preloader);
        net = new TeaNet();
        logger = new TeaApplicationLogger();
        clipboard = new TeaClipboard();

        if(config.useNativePixmap) {
            initGdx();
        }
        initSound();

        //TODO remove script loading from application
        initBulletPhysics(this);
        initBox2dPhysics(this);
        initImGui(this);

        Gdx.app = this;
        Gdx.graphics = graphics;
        Gdx.gl = graphics.getGL20();
        Gdx.gl20 = graphics.getGL20();
        Gdx.input = input;
        Gdx.files = files;
        Gdx.net = net;

        window.getDocument().addEventListener("visibilitychange", new EventListenerWrapper() {
            @Override
            public void handleEvent(EventWrapper evt) {
                // notify of state change
                String state = window.getDocument().getVisibilityState();
                if ("hidden".equals(state)) {
                    // hidden: i.e. we are paused
                    synchronized (lifecycleListeners) {
                        for (LifecycleListener listener : lifecycleListeners) {
                            listener.pause();
                        }
                    }
                    appListener.pause();
                }
                else {
                    // visible: i.e. we resume
                    synchronized (lifecycleListeners) {
                        for (LifecycleListener listener : lifecycleListeners) {
                            listener.resume();
                        }
                    }
                    appListener.resume();
                }
            }
        });

        window.requestAnimationFrame(this);

        if(config.isAutoSizeApplication()) {
            window.addEventListener("resize", new EventListenerWrapper() {
                @Override
                public void handleEvent(EventWrapper evt) {
                    int width = window.getClientWidth() - config.padHorizontal;
                    int height = window.getClientHeight() - config.padVertical;

                    if(width <= 0 || height <= 0) {
                        return;
                    }

                    if(graphics != null) {
                        // event calls us with logical pixel size, so if we use physical pixels internally,
                        // we need to convert them
                        if(config.usePhysicalPixels) {
                            double density = graphics.getNativeScreenDensity();
                            width = (int)(width * density);
                            height = (int)(height * density);
                        }
                        graphics.setCanvasSize(width, height);
                    }
                }
            });
        }
    }

    @Override
    public void run() {
        AppState state = initState;
        try {
            switch(state) {
                case LOAD_ASSETS:
                    int queue = AssetDownloader.getInstance().getQueue();
                    if(queue == 0) {
                        initState = AppState.APP_LOOP;

                        // remove loading indicator
                        HTMLElement element = Window.current().getDocument().getElementById("progress");
                        if (element != null) {
                          element.getStyle().setProperty("display", "none");
                        }
                    }
                    else {
                        // update progress bar once we know the total number of assets that are loaded
                        int total = preloader.assetTotal;
                        if (total > 0) {
                          // we have the actual total and can update the progress bar
                          int minPercentage = 25;
                          int percentage = minPercentage + (((100 - minPercentage) * (total - queue)) / total);
                          HTMLElement progressBar = Window.current().getDocument().getElementById("progress-bar");
                          if (progressBar != null) {
                            progressBar.getStyle().setProperty("width", percentage + "%");
                          }
                        }
                    }
                    break;
                case APP_LOOP:
                    if(queueAppListener != null) {
                        if(appListener != null) {
                            appListener.pause();
                            appListener.dispose();
                        }
                        input.setInputProcessor(null);
                        input.reset();
                        runnables.clear();
                        appListener = queueAppListener;
                        queueAppListener = null;
                        initState = AppState.APP_CREATE;
                        graphics.frameId  = 0;
                    }
                    step(appListener);
                    break;
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
            throw t;
        }

        window.requestAnimationFrame(this);
    }

    private void step(ApplicationListener appListener) {
        graphics.update();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        boolean resizeBypass = false;

        if(initState == AppState.APP_CREATE) {
            initState = AppState.APP_LOOP;
            appListener.create();
            appListener.resume();
            resizeBypass = true;
        }

        if((width != lastWidth || height != lastHeight) || resizeBypass) {
            lastWidth = width;
            lastHeight = height;
            Gdx.gl.glViewport(0, 0, width, height);
            appListener.resize(width, height);
        }

        runnablesHelper.addAll(runnables);
        runnables.clear();
        for(int i = 0; i < runnablesHelper.size; i++) {
            runnablesHelper.get(i).run();
        }
        runnablesHelper.clear();
        graphics.frameId++;
        if(graphics.frameId > 60) { // A bit of delay before rendering so fps don't start with 0
            appListener.render();
        }
        input.reset();
    }

    public void setApplicationListener(ApplicationListener applicationListener) {
        this.queueAppListener = applicationListener;
    }

    public Preloader getPreloader() {
        return preloader;
    }

    public TeaApplicationConfiguration getConfig() {
        return config;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return appListener;
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
            pref = new TeaPreferences(storage, config.storagePrefix + name);
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

    public String getAssetUrl() {
        return preloader.getAssetUrl();
    }

    public enum AppState {
        LOAD_ASSETS,
        APP_CREATE,
        APP_LOOP
    }

    // ##################### NATIVE CALLS #####################

    private void initGdx() {
        preloader.loadScript(true, "gdx.wasm.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                return true;
            }
        });
    }

    private void initSound() {
        preloader.loadScript(true, "soundmanager2-jsmin.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                TeaSoundManager soundManager = new TeaSoundManager();
                soundManager.setup(hostPageBaseURL, new SoundManagerCallback() {
                    @Override
                    public void onready() {
                        audio = new TeaAudio(soundManager);
                        Gdx.audio = audio;
                    }

                    @Override
                    public void ontimeout() {
                    }
                });
                return true;
            }
        });
    }

    public void initBulletPhysics(TeaApplication application) {
        Preloader preloader = application.getPreloader();
        initBulletPhysicsWasm(preloader);
    }

    public void initBox2dPhysics(TeaApplication application) {
        Preloader preloader = application.getPreloader();
        initBox2DPhysicsWasm(preloader);
    }

    public void initImGui(TeaApplication application) {
        //TODO script loading should be inside lib and not application
        Preloader preloader = application.getPreloader();
        initImGuiWasm(preloader);
    }

    private void initBulletPhysicsWasm(Preloader preloader) {
        preloader.loadScript(false, "bullet.wasm.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }

    private void initImGuiWasm(Preloader preloader) {
        preloader.loadScript(false, "imgui.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }

    // Box2D

    private void initBox2DPhysicsWasm(Preloader preloader) {
        preloader.loadScript(false, "box2D.wasm.js", new AssetLoaderListener<Object>() {
            @Override
            public boolean onSuccess(String url, Object result) {
                return true;
            }

            @Override
            public void onFailure(String url) {
            }
        });
    }
}
