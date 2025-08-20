package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetInstance;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoader;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetLoaderListener;
import com.github.xpenatan.gdx.backends.teavm.assetloader.AssetType;
import com.github.xpenatan.gdx.backends.teavm.assetloader.TeaBlob;

public class TeaPreloadApplicationListener extends ApplicationAdapter {

    public String startupLogo = "startup-logo.png";
    public float animationSpeed = 0.9f;

    private TeaApplication teaApplication;
    private AssetLoader assetLoader;
    private Step preloadStep = Step.PRELOAD_LOADING_ASSETS;
    private Texture logoTexture;
    private Stage stage;
    private Image logo;
    private ProgressBar progressBar;
    private Table table;
    private float originalWidth;
    private float originalHeight;
    private float targetProgress = 0f;
    private float displayedProgress = 0f;
    private int assetsCount = -1;
    private boolean isAnimation = false;

    @Override
    public void create() {
        teaApplication = TeaApplication.get();
        assetLoader = AssetInstance.getLoaderInstance();

        stage = new Stage();
        stage.setViewport(new ScreenViewport());

        assetLoader.loadAsset(startupLogo, AssetType.Binary, Files.FileType.Internal, new AssetLoaderListener<>() {
            @Override
            public void onSuccess(String url, TeaBlob result) {
                Gdx.app.postRunnable(() -> {
                    preloadStep = Step.PRELOAD_ASSETS;
                    stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                    setupScene2d();
                    assetLoader.preload("assets.txt", new AssetLoaderListener<>() {
                        @Override
                        public void onSuccess(String url, Void result) {
                            assetsCount = assetLoader.getQueue();
                        }
                    });
                });
            }
        });
    }

    private void setupScene2d() {
        FileHandle internal = Gdx.files.internal(startupLogo);
        logoTexture = new Texture(internal);
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        logo = new Image(logoTexture);
        logo.setScaling(Scaling.fill);  // Fill width, maintain aspect ratio.
        originalWidth = logoTexture.getWidth();
        originalHeight = logoTexture.getHeight();

        // Create a 1x1 white pixel texture for border and fill.
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap);
        pixmap.dispose();

        // Create the border drawable (white stroke) with fixed height.
        BorderDrawable borderDrawable = new BorderDrawable(whiteTexture);
        borderDrawable.setMinHeight(40f);  // Fixed height.

        // Create the fill drawable (solid white rectangle for the knob).
        TextureRegionDrawable fillDrawable = new TextureRegionDrawable(new TextureRegion(whiteTexture));
        fillDrawable.setMinHeight(borderDrawable.getMinHeight() - borderDrawable.getTopHeight() - borderDrawable.getBottomHeight());

        // Set up the progress bar style with fixed height.
        ProgressBar.ProgressBarStyle style = new ProgressBar.ProgressBarStyle();
        style.background = borderDrawable;  // White stroke border with padding.
        style.knob = fillDrawable;  // White fill that scales with progress.
        style.knobBefore = fillDrawable;  // Ensure fill is drawn correctly.

        // Create the progress bar (0 to 1, horizontal) with fixed height.
        progressBar = new ProgressBar(0f, 1f, 0.01f, false, style);
        progressBar.setHeight(20f);  // Explicitly set height to match borderDrawable.
        progressBar.setValue(0.0f);  // Set initial progress (0%).

        // Use a Table for centered layout.
        table = new Table();
        table.setFillParent(true);  // Table fills the screen.
        table.align(Align.center);  // Center content vertically and horizontally.
        stage.addActor(table);

        // Initial layout setup.
        updateTableLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void updateTableLayout(float width, float height) {
        table.clear();
        // Calculate target width as the minimum of original width and 80% of screen width, with a minimum of 50px.
        float targetWidth = Math.min(originalWidth, Math.max(50f, width * 0.8f));
        float logoAspectRatio = originalHeight / originalWidth;
        float targetHeight = targetWidth * logoAspectRatio;

        // Add logo with scaled size, fixed small padding.
        table.add(logo).width(targetWidth).height(targetHeight).padBottom(20f).row();

        // Add progress bar, match its width and enforce fixed height.
        table.add(progressBar).width(targetWidth).height(40f);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float deltaTime = Gdx.graphics.getDeltaTime();
        stage.act();
        stage.draw();

        if(assetsCount >= 0) {
            int queue = assetLoader.getQueue();
            float progress = (float)(assetsCount - queue) / assetsCount;
            if(!isAnimation) {
                targetProgress = progress;
            }

            progressBar.setValue(displayedProgress);
            boolean downloading = assetLoader.isDownloading();
            if(progress == 1.0 && !downloading && displayedProgress == 1.0f) {
                teaApplication.setPreloadReady();
            }

            // Animate progress toward targetProgress.
            if (targetProgress > displayedProgress) {
                float progressDelta = animationSpeed * deltaTime; // Smooth increase per frame.
                displayedProgress = Math.min(targetProgress, displayedProgress + progressDelta);
                progressBar.setValue(displayedProgress);
                isAnimation = true;
            }
            else {
                isAnimation = false;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if(preloadStep == Step.PRELOAD_ASSETS) {
            stage.getViewport().update(width, height, true);
            updateTableLayout(width, height);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        logoTexture.dispose();
    }

    private enum Step {
        PRELOAD_LOADING_ASSETS,
        PRELOAD_ASSETS
    }

    private static class BorderDrawable extends BaseDrawable {
        private Texture texture;
        private float thickness = 4f;  // Border thickness in pixels.
        private float padding = 6f;    // Padding inside the border.
        private float fixedHeight = 40f;  // Fixed height for the progress bar.

        public BorderDrawable(Texture texture) {
            this.texture = texture;
        }

        @Override
        public void draw(Batch batch, float x, float y, float width, float height) {
            // Draw four borders with padding inside, enforcing fixed height.
            batch.draw(texture, x, y + fixedHeight - thickness, width, thickness);  // Top
            batch.draw(texture, x, y, width, thickness);  // Bottom
            batch.draw(texture, x, y, thickness, fixedHeight);  // Left
            batch.draw(texture, x + width - thickness, y, thickness, fixedHeight);  // Right
        }

        // Insets to include padding inside the border for the fill.
        @Override
        public float getLeftWidth() { return thickness + padding; }
        @Override
        public float getRightWidth() { return thickness + padding; }
        @Override
        public float getTopHeight() { return thickness + padding; }
        @Override
        public float getBottomHeight() { return thickness + padding; }
        @Override
        public float getMinHeight() { return fixedHeight; }  // Enforce fixed height.
    }
}
