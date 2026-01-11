package com.github.xpenatan.gdx.example.basic.tests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

public class TeaVMInputTest extends InputAdapter implements ApplicationListener {
    ShapeRenderer renderer;
    int x = 0;
    int y = 0;

    @Override
    public void create() {
        Gdx.input.setCatchKey(Keys.F1, true);
        Gdx.input.setCatchKey(Keys.F3, true);
        Gdx.input.setCatchKey(Keys.F5, true);
        Gdx.input.setCatchKey(Keys.F6, true);
        Gdx.input.setCatchKey(Keys.F7, true);
        Gdx.input.setCatchKey(Keys.F8, true);
        Gdx.input.setCatchKey(Keys.F9, true);
        Gdx.input.setCatchKey(Keys.F10, true);

        renderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(this);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
//        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        renderer.begin(ShapeType.Filled);
        if(Gdx.input.isTouched())
            renderer.setColor(Color.RED);
        else
            renderer.setColor(Color.GREEN);
        renderer.rect(Gdx.input.getX() - 15, Gdx.graphics.getHeight() - Gdx.input.getY() - 15, 30, 30);
        renderer.rect(x, y, 30, 30);
        renderer.end();

        if(Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
            Gdx.app.log("TeaVMInputTest", "key pressed: " + "ALT_LEFT");
        }
        if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
            Gdx.app.log("TeaVMInputTest", "key pressed: " + "CTRL_LEFT");
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)) {
            x -= 1;
        }

        if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
            x += 1;
        }

        if(Gdx.input.isKeyPressed(Keys.UP)) {
            y += 1;
        }

        if(Gdx.input.isKeyPressed(Keys.DOWN)) {
            y -= 1;
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Gdx.app.log("TeaVMInputTest", "button pressed: LEFT");
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {
            Gdx.app.log("TeaVMInputTest", "button pressed: MIDDLE");
        }
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Gdx.app.log("TeaVMInputTest", "button pressed: RIGHT");
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log("GdxInputTest", "key down: " + keycode);
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        Gdx.app.log("GdxInputTest", "key typed: '" + character + "'");
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log("GdxInputTest", "key up: " + keycode);

        if(keycode == Keys.F2) {
            boolean isCursorCatched = Gdx.input.isCursorCatched();
            Gdx.input.setCursorCatched(!isCursorCatched);
        }
        return false;
    }
}
