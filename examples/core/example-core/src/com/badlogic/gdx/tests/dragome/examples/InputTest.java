package com.badlogic.gdx.tests.dragome.examples;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class InputTest implements ApplicationListener, InputProcessor {

	@Override
	public void create () {
// Gdx.input = new RemoteInput();
		Gdx.input.setInputProcessor(this);
// Gdx.input.setCursorCatched(true);
	}

	@Override
	public void render () {
		if (Gdx.input.justTouched()) {
			Gdx.app.log("Input Test", "just touched, button: " + (Gdx.input.isButtonPressed(Buttons.LEFT) ? "left " : "")
				+ (Gdx.input.isButtonPressed(Buttons.MIDDLE) ? "middle " : "")
				+ (Gdx.input.isButtonPressed(Buttons.RIGHT) ? "right" : "")
				+ (Gdx.input.isButtonPressed(Buttons.BACK) ? "back" : "")
				+ (Gdx.input.isButtonPressed(Buttons.FORWARD) ? "forward" : ""));
		}

		for (int i = 0; i < 10; i++) {
			if (Gdx.input.getDeltaX(i) != 0 || Gdx.input.getDeltaY(i) != 0) {
				Gdx.app.log("Input Test", "delta[" + i + "]: " + Gdx.input.getDeltaX(i) + ", " + Gdx.input.getDeltaY(i));
			}
		}
// Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
// if(Gdx.input.isTouched()) {
// Gdx.app.log("Input Test", "is touched");
// }
	}

	@Override
	public boolean keyDown (int keycode) {
		Gdx.app.log("Input Test", "key down: " + keycode);
		if (keycode == Keys.G) Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
		return false;
	}

	@Override
	public boolean keyTyped (char character) {
		Gdx.app.log("Input Test", "key typed: '" + character + "'");
		return false;
	}

	@Override
	public boolean keyUp (int keycode) {
		Gdx.app.log("Input Test", "key up: " + keycode);
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		Gdx.app.log("Input Test", "touch down: " + x + ", " + y + ", button: " + getButtonString(button));
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		Gdx.app.log("Input Test", "touch dragged: " + x + ", " + y + ", pointer: " + pointer);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		Gdx.app.log("Input Test", "touch up: " + x + ", " + y + ", button: " + getButtonString(button));
		return false;
	}

	@Override
	public boolean mouseMoved (int x, int y) {
		Gdx.app.log("Input Test", "touch moved: " + x + ", " + y);
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		Gdx.app.log("Input Test", "scrolled: " + amount);
		return false;
	}

	private String getButtonString (int button) {
		if (button == Buttons.LEFT) return "left";
		if (button == Buttons.RIGHT) return "right";
		if (button == Buttons.MIDDLE) return "middle";
		if (button == Buttons.BACK) return "back";
		if (button == Buttons.FORWARD) return "forward";
		return "unknown";
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
