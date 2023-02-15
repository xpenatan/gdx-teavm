package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LoadingTest extends Game {
    AssetManager manager;

    GameScreen gameScreen;

    @Override
    public void create() {
        manager = new AssetManager();
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        super.dispose();
        gameScreen.dispose();
        manager.dispose();
    }

    public static class GameScreen extends ScreenAdapter {
        private final LoadingTest game;

        private ModelBatch batch;

        private PerspectiveCamera camera;

        private Model shipModel;
        private ModelInstance shipModelInstance;

        private ScreenViewport viewport;

        private CameraInputController cameraControl;

        public GameScreen(LoadingTest game) {
            this.game = game;
        }

        @Override
        public void show() {
            batch = new ModelBatch();
            camera = new PerspectiveCamera();

            camera.position.set(0, 1, 3);
            camera.lookAt(0, 0, 0);
            camera.update();

            viewport = new ScreenViewport(camera);
            cameraControl = new CameraInputController(camera);

            game.manager.load("data/g3d/ship/ship.obj", Model.class);
            game.manager.finishLoading();

            shipModel = game.manager.get("data/g3d/ship/ship.obj", Model.class);
            shipModelInstance = new ModelInstance(shipModel);

            Gdx.input.setInputProcessor(new InputMultiplexer(cameraControl));
        }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            if(cameraControl != null){
                cameraControl.update();
            }

            camera.update();
            batch.begin(camera);
            if(shipModelInstance != null) {
                batch.render(shipModelInstance);
            }
            batch.end();
        }

        @Override
        public void resize(int width, int height) {
            viewport.update(width, height, false);
        }
    }
}
