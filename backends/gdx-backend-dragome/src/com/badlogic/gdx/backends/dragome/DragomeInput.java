/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.dragome;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/** @author xpenatan */
public class DragomeInput implements Input {

	public void reset () {

	}

	@Override
	public float getAccelerometerX () {
		return 0;
	}

	@Override
	public float getAccelerometerY () {
		return 0;
	}

	@Override
	public float getAccelerometerZ () {
		return 0;
	}

	@Override
	public float getGyroscopeX () {
		return 0;
	}

	@Override
	public float getGyroscopeY () {
		return 0;
	}

	@Override
	public float getGyroscopeZ () {
		return 0;
	}

	@Override
	public int getX () {
		return 0;
	}

	@Override
	public int getX (int pointer) {
		return 0;
	}

	@Override
	public int getDeltaX () {
		return 0;
	}

	@Override
	public int getDeltaX (int pointer) {
		return 0;
	}

	@Override
	public int getY () {
		return 0;
	}

	@Override
	public int getY (int pointer) {
		return 0;
	}

	@Override
	public int getDeltaY () {
		return 0;
	}

	@Override
	public int getDeltaY (int pointer) {
		return 0;
	}

	@Override
	public boolean isTouched () {
		return false;
	}

	@Override
	public boolean justTouched () {
		return false;
	}

	@Override
	public boolean isTouched (int pointer) {
		return false;
	}

	@Override
	public boolean isButtonPressed (int button) {
		return false;
	}

	@Override
	public boolean isKeyPressed (int key) {
		return false;
	}

	@Override
	public boolean isKeyJustPressed (int key) {
		return false;
	}

	@Override
	public void getTextInput (TextInputListener listener, String title, String text, String hint) {
	}

	@Override
	public void setOnscreenKeyboardVisible (boolean visible) {
	}

	@Override
	public void vibrate (int milliseconds) {
	}

	@Override
	public void vibrate (long[] pattern, int repeat) {
	}

	@Override
	public void cancelVibrate () {
	}

	@Override
	public float getAzimuth () {
		return 0;
	}

	@Override
	public float getPitch () {
		return 0;
	}

	@Override
	public float getRoll () {
		return 0;
	}

	@Override
	public void getRotationMatrix (float[] matrix) {
	}

	@Override
	public long getCurrentEventTime () {
		return 0;
	}

	@Override
	public void setCatchBackKey (boolean catchBack) {
	}

	@Override
	public boolean isCatchBackKey () {
		return false;
	}

	@Override
	public void setCatchMenuKey (boolean catchMenu) {
	}

	@Override
	public boolean isCatchMenuKey () {
		return false;
	}

	@Override
	public void setInputProcessor (InputProcessor processor) {
	}

	@Override
	public InputProcessor getInputProcessor () {
		return null;
	}

	@Override
	public boolean isPeripheralAvailable (Peripheral peripheral) {
		return false;
	}

	@Override
	public int getRotation () {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation () {
		return null;
	}

	@Override
	public void setCursorCatched (boolean catched) {
	}

	@Override
	public boolean isCursorCatched () {
		return false;
	}

	@Override
	public void setCursorPosition (int x, int y) {
	}

}
