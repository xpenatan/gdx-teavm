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
import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.WindowWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloadImpl;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloader;
import com.github.xpenatan.gdx.backends.teavm.preloader.AssetDownloader.AssetDownload;
import com.github.xpenatan.gdx.backends.teavm.preloader.Preloader;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.SoundManagerCallback;
import com.github.xpenatan.gdx.backends.teavm.soundmanager.TeaSoundManager;
import com.github.xpenatan.gdx.backends.teavm.util.TeaJSHelper;

/**
 * @author xpenatan
 */
public class TeaApplication implements Application, Runnable {

    private static TeaAgentInfo agentInfo;

    public static TeaAgentInfo getAgentInfo() {
        return agentInfo;
    }

    private TeaGraphics graphics;
    private TeaInput input;
    private TeaFiles files;
    private TeaAudio audio;
    private TeaApplicationConfiguration config;
    private ApplicationListener appListener;
    private TeaWindow window;

    private AppState initState = AppState.IDLE;

    private int lastWidth = -1;
    private int lastHeight = 1;

    private TeaApplicationLogger logger;

    private Preloader preloader;

    private ObjectMap<String, Preferences> prefs = new ObjectMap<>();

    private Array<Runnable> runnables = new Array<Runnable>();
    private Array<Runnable> runnablesHelper = new Array<Runnable>();

    private String hostPageBaseURL;

    public TeaApplication(ApplicationListener appListener, TeaApplicationConfiguration config) {
        TeaJSHelper jsHelper = config.getJSHelper();
        TeaJSHelper.JSHelper = jsHelper;
        this.window = TeaWindow.get();
        this.appListener = appListener;
        this.config = config;
        init();
    }

    private void init() {
        TeaApplication.agentInfo = TeaWebAgent.computeAgentInfo();
        System.setProperty("java.runtime.name", "");
        if(agentInfo.isWindows() == true)
            System.setProperty("os.name", "Windows");
        else if(agentInfo.isMacOS() == true)
            System.setProperty("os.name", "OS X");
        else if(agentInfo.isLinux() == true)
            System.setProperty("os.name", "Linux");
        else
            System.setProperty("os.name", "no OS");

        AssetDownloader.setInstance(new AssetDownloadImpl());

        AssetDownload instance = AssetDownloader.getInstance();
        hostPageBaseURL = instance.getHostPageBaseURL();

        if(hostPageBaseURL.contains(".html")) {
            // TODO use regex
            hostPageBaseURL = hostPageBaseURL.replace("index.html", "");
            hostPageBaseURL = hostPageBaseURL.replace("index-debug.html", "");
        }
        preloader = new Preloader(hostPageBaseURL);
        AssetLoaderListener<Object> assetListener = new AssetLoaderListener();
        preloader.preload("assets.txt");
//		preloader.loadAsset("com/badlogic/gdx/graphics/g3d/particles/particles.fragment.glsl", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/graphics/g3d/particles/particles.vertex.glsl", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/graphics/g3d/shaders/depth.fragment.glsl", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/graphics/g3d/shaders/depth.vertex.glsl", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/utils/arial-15.fnt", AssetType.Text, null, assetListener);
//		preloader.loadAsset("com/badlogic/gdx/utils/arial-15.png", AssetType.Image, null, assetListener);

        //TODO implement manual and automatic asset loading
//		getPreloader().loadAsset("data/uiskin.atlas", AssetType.Text, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/uiskin.json", AssetType.Text, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/uiskin.png", AssetType.Image, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/default.fnt", AssetType.Text, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/default.png", AssetType.Image, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/badlogicsmall.jpg", AssetType.Image, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/badlogic.jpg", AssetType.Image, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/jsonTest.json", AssetType.Text, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/walkanim.png", AssetType.Image, null, new AssetLoaderListener<>());
//		getPreloader().loadAsset("data/shotgun.ogg", AssetType.Audio, null, new AssetLoaderListener<>());

        graphics = new TeaGraphics(config);
        input = new TeaInput(graphics.canvas);
        files = new TeaFiles(preloader);
        logger = new TeaApplicationLogger();
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
                case IDLE:
                    int queue = AssetDownloader.getInstance().getQueue();
                    if(queue == 0)
                        initState = AppState.QUEUE_ASSETS_LOADED;
                    break;
                case APP_CREATE:
                case APP_READY:
                    step(appListener);
                    break;
                case QUEUE_ASSETS_LOADED:
                    //				initState = AppState.INIT_SOUND;
                    initState = AppState.APP_CREATE;
                    break;
                default:
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
        if(width != lastWidth || height != lastHeight) {
            lastWidth = width;
            lastHeight = height;
            Gdx.gl.glViewport(0, 0, width, height);

            if(initState == AppState.APP_CREATE) {
                initState = AppState.APP_READY;
                appListener.create();
            }
            appListener.resize(width, height);
        }

        runnablesHelper.addAll(runnables);
        runnables.clear();
        for(int i = 0; i < runnablesHelper.size; i++) {
            runnablesHelper.get(i).run();
        }
        runnablesHelper.clear();

        graphics.frameId++;
        if(graphics.frameId > 60) { // A bit of delay before rendering;
            appListener.render();
        }
        input.reset();
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

    public Preloader getPreloader() {
        return preloader;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void log(String tag, String message) {
        logger.log(tag, message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        logger.log(tag, message, exception);
    }

    @Override
    public void error(String tag, String message) {
        logger.error(tag, message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        logger.error(tag, message, exception);
    }

    @Override
    public void debug(String tag, String message) {
        logger.debug(tag, message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        logger.debug(tag, message, exception);
    }

    @Override
    public void setLogLevel(int logLevel) {
        logger.setLogLevel(logLevel);
    }

    @Override
    public int getLogLevel() {
        return logger.getLogLevel();
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
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
            StorageWrapper storage = TeaJSHelper.get().getStorage();
            pref = new TeaPreferences(storage, name);
            prefs.put(name, pref);
        }
        return pref;
    }

    @Override
    public Clipboard getClipboard() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        // TODO Auto-generated method stub

    }

    public String getAssetUrl() {
        return preloader.getAssetUrl();
    }

    public enum AppState {
        IDLE,
        QUEUE_ASSETS_LOADED,
        INIT_SOUND,
        APP_CREATE,
        APP_READY
    }

    // ##################### NATIVE CALLS #####################

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
