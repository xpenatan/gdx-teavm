package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetInstance;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoader;
import com.github.xpenatan.gdx.examples.tests.LoadingTest;
import com.github.xpenatan.gdx.examples.utils.LoadingBar;

public class LoadingTestLauncher extends ApplicationAdapter {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.preloadAssets = false;
        new TeaApplication(new LoadingTestLauncher(), config);
    }

    enum STEPS {
        PRELOAD_SCENE2D_ASSETS,
        LOADING_GAME_ASSETS
    }

    private STEPS steps = STEPS.PRELOAD_SCENE2D_ASSETS;

    private Stage stage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;
    private float percent;

    private Actor loadingBar;

    private TeaApplication teaApplication;
    private AssetLoader assetLoader;

    // Using manager just to load scene2d assets after downloading.
    private AssetManager manager;

    private float assetsCount;

    @Override
    public void create() {
        teaApplication = TeaApplication.get();

        assetLoader = AssetInstance.getLoaderInstance();

        stage = new Stage();
        stage.setViewport(new ScreenViewport());

        manager = new AssetManager();

        preloadScene2dAssets();
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = stage.getViewport();
        viewport.update(width, height, true);
        if(steps == STEPS.LOADING_GAME_ASSETS) {
            resizeScene(width, height);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        int queue = assetLoader.getQueue();

        if(steps == STEPS.LOADING_GAME_ASSETS) {
            float progress = (assetsCount - queue) / assetsCount;
            percent = Interpolation.linear.apply(percent, progress, 0.1f);
            loadingBarHidden.setX(startX + endX * percent);
            loadingBg.setX(loadingBarHidden.getX() + 30);
            loadingBg.setWidth(450 - 450 * percent);
            loadingBg.invalidate();

            if(progress == 1.0f && percent > 0.98f) {
                loadGameScreen();
            }
        }
        stage.act();
        stage.draw();

        if(manager.update()) {
        }

        if(queue == 0) {
            if(steps == STEPS.PRELOAD_SCENE2D_ASSETS) {
                loadScene2dAssets();
            }
        }
    }

    private void preloadScene2dAssets() {
//        preloader.loadSingleAsset(preloader.getAsset("data/loading/loading.pack"));
//        preloader.loadSingleAsset(preloader.getAsset("data/loading/loading.png"));
    }

    private void loadScene2dAssets() {
        manager.load("data/loading/loading.pack", TextureAtlas.class);
        manager.load("data/loading/loading.png", Texture.class);
        manager.finishLoading();
        initLoadingAssets();
        loadGameAssets();
        steps = STEPS.LOADING_GAME_ASSETS;
    }

    private void loadGameAssets() {
//        assetsCount = preloader.loadAssets();
        System.out.println("AssetsCount: " + assetsCount);
    }

    private void initLoadingAssets() {
        // Loading solution is from https://github.com/Matsemann/libgdx-loading-screen

        // Get our textureatlas from the manager
        TextureAtlas atlas = manager.get("data/loading/loading.pack", TextureAtlas.class);

        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(Animation.PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        resizeScene(width, height);
    }

    private void resizeScene(int width, int height) {
        // Make the background fill the screen
        screenBg.setSize(width, height);

        // Place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 100);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    private void loadGameScreen() {
        teaApplication.setApplicationListener(new LoadingTest());
    }

    @Override
    public void dispose() {
//        manager.dispose();
//        stage.dispose();
    }
}
