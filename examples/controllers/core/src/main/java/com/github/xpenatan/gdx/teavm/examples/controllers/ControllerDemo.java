package com.github.xpenatan.gdx.teavm.examples.controllers;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.xpenatan.gdx.teavm.examples.shared.ExampleFpsLogger;

public class ControllerDemo implements ApplicationListener, ControllerListener {

    private static final float DEAD_ZONE = 0.2f;
    private static final float MOVE_SPEED = 260f;
    private static final float BASE_WIDTH = 800f;
    private static final float BASE_HEIGHT = 480f;
    private static final float BASE_MARKER_SIZE = 44f;
    private static final float BASE_PLAY_AREA_PADDING = 28f;
    private static final float BASE_TEXT_X = 36f;
    private static final float BASE_TEXT_TOP = 42f;
    private static final float BASE_TEXT_LINE_HEIGHT = 28f;
    private static final float BASE_CENTER_DOT_RADIUS = 7f;

    private ShapeRenderer shapes;
    private SpriteBatch batch;
    private BitmapFont font;
    private ExampleFpsLogger fpsLogger;
    private float width = BASE_WIDTH;
    private float height = BASE_HEIGHT;
    private float markerX = 378f;
    private float markerY = 218f;
    private float axisX;
    private float axisY;
    private float uiScale = 1f;
    private int lastControllerCount = -1;
    private int lastButton = ControllerMapping.UNDEFINED;
    private String lastEvent = "Waiting for controller input";
    private String controllerSummary = "Controllers: scanning";

    @Override
    public void create() {
        fpsLogger = new ExampleFpsLogger();
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("com/badlogic/gdx/utils/lsans-15.fnt"), false);
        updateUiScale();
        Controllers.addListener(this);
        refreshControllerSummary();
    }

    @Override
    public void resize(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
        updateUiScale();
        markerX = MathUtils.clamp(markerX, 0f, this.width - markerSize());
        markerY = MathUtils.clamp(markerY, 0f, this.height - markerSize());
    }

    @Override
    public void render() {
        updateControllerState();
        updateMarker();

        ScreenUtils.clear(0.08f, 0.09f, 0.11f, 1f);

        float playAreaPadding = playAreaPadding();
        float markerSize = markerSize();

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(0.16f, 0.19f, 0.23f, 1f);
        shapes.rect(playAreaPadding, playAreaPadding, width - playAreaPadding * 2f, height - playAreaPadding * 2f);
        shapes.setColor(0.1f, 0.58f, 0.82f, 1f);
        shapes.rect(markerX, markerY, markerSize, markerSize);
        shapes.setColor(0.9f, 0.78f, 0.3f, 1f);
        shapes.circle(markerX + markerSize * 0.5f, markerY + markerSize * 0.5f, scaled(BASE_CENTER_DOT_RADIUS), 24);
        shapes.end();

        batch.begin();
        font.setColor(Color.WHITE);
        float textX = scaled(BASE_TEXT_X);
        float textY = height - scaled(BASE_TEXT_TOP);
        float lineHeight = scaled(BASE_TEXT_LINE_HEIGHT);
        font.draw(batch, "gdx-controllers example", textX, textY);
        font.draw(batch, controllerSummary, textX, textY - lineHeight);
        font.draw(batch, "Move with left stick or arrow keys", textX, textY - lineHeight * 2f);
        font.draw(batch, "Axis: " + round(axisX) + ", " + round(axisY), textX, textY - lineHeight * 3f);
        font.draw(batch, "Button: " + buttonText(), textX, textY - lineHeight * 4f);
        font.draw(batch, "Event: " + lastEvent, textX, textY - lineHeight * 5f);
        batch.end();
        fpsLogger.log();
    }

    private void updateControllerState() {
        Array<Controller> controllers = Controllers.getControllers();
        if(controllers.size != lastControllerCount) {
            refreshControllerSummary(controllers);
        }

        Controller controller = Controllers.getCurrent();
        if(controller == null && controllers.size > 0) {
            controller = controllers.first();
        }

        axisX = readAxis(controller, true);
        axisY = readAxis(controller, false);
    }

    private void updateMarker() {
        float moveX = axisX;
        float moveY = -axisY;
        float playAreaPadding = playAreaPadding();
        float markerSize = markerSize();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            moveX -= 1f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            moveX += 1f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            moveY -= 1f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            moveY += 1f;
        }

        float delta = Gdx.graphics.getDeltaTime() * MOVE_SPEED * uiScale;
        markerX = MathUtils.clamp(markerX + moveX * delta, playAreaPadding, width - playAreaPadding - markerSize);
        markerY = MathUtils.clamp(markerY + moveY * delta, playAreaPadding, height - playAreaPadding - markerSize);
    }

    private float readAxis(Controller controller, boolean horizontal) {
        if(controller == null) {
            return 0f;
        }

        ControllerMapping mapping = controller.getMapping();
        int mappedAxis = mapping == null
                ? ControllerMapping.UNDEFINED
                : horizontal ? mapping.axisLeftX : mapping.axisLeftY;
        int fallbackAxis = horizontal ? 0 : 1;
        int axis = mappedAxis == ControllerMapping.UNDEFINED ? fallbackAxis : mappedAxis;
        if(axis < 0 || axis >= controller.getAxisCount()) {
            return 0f;
        }

        float value = controller.getAxis(axis);
        return Math.abs(value) < DEAD_ZONE ? 0f : value;
    }

    private void refreshControllerSummary() {
        refreshControllerSummary(Controllers.getControllers());
    }

    private void refreshControllerSummary(Array<Controller> controllers) {
        lastControllerCount = controllers.size;
        if(controllers.size == 0) {
            controllerSummary = "Controllers: none connected";
            return;
        }

        StringBuilder builder = new StringBuilder("Controllers: ");
        for(int i = 0; i < controllers.size; i++) {
            if(i > 0) {
                builder.append(", ");
            }
            String name = controllers.get(i).getName();
            builder.append(name == null || name.isEmpty() ? "Unnamed" : name);
        }
        controllerSummary = builder.toString();
    }

    private String buttonText() {
        return lastButton == ControllerMapping.UNDEFINED ? "none" : String.valueOf(lastButton);
    }

    private String round(float value) {
        return String.valueOf(Math.round(value * 100f) / 100f);
    }

    private void updateUiScale() {
        uiScale = Math.max(1f, Math.min(width / BASE_WIDTH, height / BASE_HEIGHT));
        font.getData().setScale(uiScale);
    }

    private float markerSize() {
        return scaled(BASE_MARKER_SIZE);
    }

    private float playAreaPadding() {
        return scaled(BASE_PLAY_AREA_PADDING);
    }

    private float scaled(float value) {
        return value * uiScale;
    }

    @Override
    public void connected(Controller controller) {
        lastEvent = "Connected " + controller.getName();
        refreshControllerSummary();
    }

    @Override
    public void disconnected(Controller controller) {
        lastEvent = "Disconnected " + controller.getName();
        refreshControllerSummary();
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        lastButton = buttonCode;
        lastEvent = controller.getName() + " button down " + buttonCode;
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        lastButton = ControllerMapping.UNDEFINED;
        lastEvent = controller.getName() + " button up " + buttonCode;
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        lastEvent = controller.getName() + " axis " + axisCode + " = " + round(value);
        return false;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        Controllers.removeListener(this);
        font.dispose();
        batch.dispose();
        shapes.dispose();
    }
}
