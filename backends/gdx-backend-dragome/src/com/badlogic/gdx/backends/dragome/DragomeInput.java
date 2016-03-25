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

import org.w3c.dom.Document;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;

/** Ported from GWT backend. //FIXME need to finish.
 * @author xpenatan */
public class DragomeInput implements Input {

	Document document;
	static final int MAX_TOUCHES = 20;
	boolean justTouched = false;
	private IntMap<Integer> touchMap = new IntMap<Integer>(20);
	private boolean[] touched = new boolean[MAX_TOUCHES];
	private int[] touchX = new int[MAX_TOUCHES];
	private int[] touchY = new int[MAX_TOUCHES];
	private int[] deltaX = new int[MAX_TOUCHES];
	private int[] deltaY = new int[MAX_TOUCHES];
	IntSet pressedButtons = new IntSet();
	int pressedKeyCount = 0;
	boolean[] pressedKeys = new boolean[256];
	boolean keyJustPressed = false;
	boolean[] justPressedKeys = new boolean[256];
	InputProcessor processor;
	char lastKeyCharPressed;
	float keyRepeatTimer;
	long currentEventTimeStamp;
	final HTMLCanvasElementExtension canvas;
	boolean hasFocus = true;
	
	public DragomeInput (DragomeApplication app) {
		this.canvas = app.graphics.canvas;
		document = app.elementBySelector.getDocument();
//		hookEvents();
	}
	
	private void hookEvents () {
		addEventListener(canvas, "mousedown", this, true);
		addEventListener(document, "mousedown", this, true);
		addEventListener(canvas, "mouseup", this, true);
		addEventListener(document, "mouseup", this, true);
		addEventListener(canvas, "mousemove", this, true);
		addEventListener(document, "mousemove", this, true);
		addEventListener(canvas, getMouseWheelEvent(), this, true);
		addEventListener(document, "keydown", this, false);
		addEventListener(document, "keyup", this, false);
		addEventListener(document, "keypress", this, false);

		addEventListener(canvas, "touchstart", this, true);
		addEventListener(canvas, "touchmove", this, true);
		addEventListener(canvas, "touchcancel", this, true);
		addEventListener(canvas, "touchend", this, true);

	}
	
	// kindly borrowed from our dear playn friends...
		static void addEventListener (Object target, String name, DragomeInput handler, boolean capture) {
			
			
			ScriptHelper.evalNoResult("target.addEventListener(name,function(e){ },capture)", null);
			
//			target
//					.addEventListener(
//							name,
//							function(e) {
//								handler.@com.badlogic.gdx.backends.gwt.GwtInput::handleEvent(Lcom/google/gwt/dom/client/NativeEvent;)(e);
//							}, capture);
		};
		
		
	/** Kindly borrowed from PlayN. **/
	protected static String getMouseWheelEvent() {
		if (ScriptHelper.evalInt("navigator.userAgent.toLowerCase().indexOf('firefox')", null) != -1) {
			return "DOMMouseScroll";
		} else {
			return "mousewheel";
		}
	};
	
//	private void handleEvent (Event e) {
//		if (e.getType().equals("mousedown")) {
//			if (!e.getTarget().equals(canvas) || touched[0]) {
//				float mouseX = getRelativeX(e, canvas);
//				float mouseY = getRelativeY(e, canvas);
//				if (mouseX < 0 || mouseX > Gdx.graphics.getWidth() || mouseY < 0 || mouseY > Gdx.graphics.getHeight()) {
//					hasFocus = false;
//				}
//				return;
//			}
//			hasFocus = true;
//			this.justTouched = true;
//			this.touched[0] = true;
//			this.pressedButtons.add(getButton(e.getButton()));
//			this.deltaX[0] = 0;
//			this.deltaY[0] = 0;
//			if (isCursorCatched()) {
//				this.touchX[0] += getMovementXJSNI(e);
//				this.touchY[0] += getMovementYJSNI(e);
//			} else {
//				this.touchX[0] = getRelativeX(e, canvas);
//				this.touchY[0] = getRelativeY(e, canvas);
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			if (processor != null) processor.touchDown(touchX[0], touchY[0], 0, getButton(e.getButton()));
//		}
//
//		if (e.getType().equals("mousemove")) {
//			if (isCursorCatched()) {
//				this.deltaX[0] = (int)getMovementXJSNI(e);
//				this.deltaY[0] = (int)getMovementYJSNI(e);
//				this.touchX[0] += getMovementXJSNI(e);
//				this.touchY[0] += getMovementYJSNI(e);
//			} else {
//				this.deltaX[0] = getRelativeX(e, canvas) - touchX[0];
//				this.deltaY[0] = getRelativeY(e, canvas) - touchY[0];
//				this.touchX[0] = getRelativeX(e, canvas);
//				this.touchY[0] = getRelativeY(e, canvas);
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			if (processor != null) {
//				if (touched[0])
//					processor.touchDragged(touchX[0], touchY[0], 0);
//				else
//					processor.mouseMoved(touchX[0], touchY[0]);
//			}
//		}
//
//		if (e.getType().equals("mouseup")) {
//			if (!touched[0]) return;
//			this.pressedButtons.remove(getButton(e.getButton()));
//			this.touched[0] = pressedButtons.size > 0;
//			if (isCursorCatched()) {
//				this.deltaX[0] = (int)getMovementXJSNI(e);
//				this.deltaY[0] = (int)getMovementYJSNI(e);
//				this.touchX[0] += getMovementXJSNI(e);
//				this.touchY[0] += getMovementYJSNI(e);
//			} else {
//				this.deltaX[0] = getRelativeX(e, canvas) - touchX[0];
//				this.deltaY[0] = getRelativeY(e, canvas) - touchY[0];
//				this.touchX[0] = getRelativeX(e, canvas);
//				this.touchY[0] = getRelativeY(e, canvas);
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			this.touched[0] = false;
//			if (processor != null) processor.touchUp(touchX[0], touchY[0], 0, getButton(e.getButton()));
//		}
//		if (e.getType().equals(getMouseWheelEvent())) {
//			if (processor != null) {
//				processor.scrolled((int)getMouseWheelVelocity(e));
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			e.preventDefault();
//		}
//		if (e.getType().equals("keydown") && hasFocus) {
//			// System.out.println("keydown");
//			int code = keyForCode(e.getKeyCode());
//			if (code == 67) {
//				e.preventDefault();
//				if (processor != null) {
//					processor.keyDown(code);
//					processor.keyTyped('\b');
//				}
//			} else {
//				if (!pressedKeys[code]) {
//					pressedKeyCount++;
//					pressedKeys[code] = true;
//					keyJustPressed = true;
//					justPressedKeys[code] = true;
//					if (processor != null) {
//						processor.keyDown(code);
//					}
//				}
//			}
//		}
//
//		if (e.getType().equals("keypress") && hasFocus) {
//			// System.out.println("keypress");
//			char c = (char)e.getCharCode();
//			if (processor != null) processor.keyTyped(c);
//		}
//
//		if (e.getType().equals("keyup") && hasFocus) {
//			// System.out.println("keyup");
//			int code = keyForCode(e.getKeyCode());
//			if (pressedKeys[code]) {
//				pressedKeyCount--;
//				pressedKeys[code] = false;
//			}
//			if (processor != null) {
//				processor.keyUp(code);
//			}
//		}
//
//		if (e.getType().equals("touchstart")) {
//			this.justTouched = true;
//			JsArray<Touch> touches = e.getChangedTouches();
//			for (int i = 0, j = touches.length(); i < j; i++) {
//				Touch touch = touches.get(i);
//				int real = touch.getIdentifier();
//				int touchId;
//				touchMap.put(real, touchId = getAvailablePointer());
//				touched[touchId] = true;
//				touchX[touchId] = getRelativeX(touch, canvas);
//				touchY[touchId] = getRelativeY(touch, canvas);
//				deltaX[touchId] = 0;
//				deltaY[touchId] = 0;
//				if (processor != null) {
//					processor.touchDown(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
//				}
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			e.preventDefault();
//		}
//		if (e.getType().equals("touchmove")) {
//			JsArray<Touch> touches = e.getChangedTouches();
//			for (int i = 0, j = touches.length(); i < j; i++) {
//				Touch touch = touches.get(i);
//				int real = touch.getIdentifier();
//				int touchId = touchMap.get(real);
//				deltaX[touchId] = getRelativeX(touch, canvas) - touchX[touchId];
//				deltaY[touchId] = getRelativeY(touch, canvas) - touchY[touchId];
//				touchX[touchId] = getRelativeX(touch, canvas);
//				touchY[touchId] = getRelativeY(touch, canvas);
//				if (processor != null) {
//					processor.touchDragged(touchX[touchId], touchY[touchId], touchId);
//				}
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			e.preventDefault();
//		}
//		if (e.getType().equals("touchcancel")) {
//			JsArray<Touch> touches = e.getChangedTouches();
//			for (int i = 0, j = touches.length(); i < j; i++) {
//				Touch touch = touches.get(i);
//				int real = touch.getIdentifier();
//				int touchId = touchMap.get(real);
//				touchMap.remove(real);
//				touched[touchId] = false;
//				deltaX[touchId] = getRelativeX(touch, canvas) - touchX[touchId];
//				deltaY[touchId] = getRelativeY(touch, canvas) - touchY[touchId];
//				touchX[touchId] = getRelativeX(touch, canvas);
//				touchY[touchId] = getRelativeY(touch, canvas);
//				if (processor != null) {
//					processor.touchUp(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
//				}
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			e.preventDefault();
//		}
//		if (e.getType().equals("touchend")) {
//			JsArray<Touch> touches = e.getChangedTouches();
//			for (int i = 0, j = touches.length(); i < j; i++) {
//				Touch touch = touches.get(i);
//				int real = touch.getIdentifier();
//				int touchId = touchMap.get(real);
//				touchMap.remove(real);
//				touched[touchId] = false;
//				deltaX[touchId] = getRelativeX(touch, canvas) - touchX[touchId];
//				deltaY[touchId] = getRelativeY(touch, canvas) - touchY[touchId];
//				touchX[touchId] = getRelativeX(touch, canvas);
//				touchY[touchId] = getRelativeY(touch, canvas);
//				if (processor != null) {
//					processor.touchUp(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
//				}
//			}
//			this.currentEventTimeStamp = TimeUtils.nanoTime();
//			e.preventDefault();
//		}
//	}
	
//	/** Kindly borrowed from PlayN. **/
//	protected int getRelativeX (NativeEvent e, HTMLCanvasElement target) {
//		float xScaleRatio = target.getWidth() * 1f / target.getClientWidth(); // Correct for canvas CSS scaling
//		return Math.round(xScaleRatio
//			* (e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument().getScrollLeft()));
//	}
//
//	/** Kindly borrowed from PlayN. **/
//	protected int getRelativeY (NativeEvent e, HTMLCanvasElement target) {
//		float yScaleRatio = target.getHeight() * 1f / target.getClientHeight(); // Correct for canvas CSS scaling
//		return Math.round(yScaleRatio
//			* (e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument().getScrollTop()));
//	}
//
//	protected int getRelativeX (Touch touch, HTMLCanvasElement target) {
//		float xScaleRatio = target.getWidth() * 1f / target.getClientWidth(); // Correct for canvas CSS scaling
//		return Math.round(xScaleRatio * touch.getRelativeX(target));
//	}
//
//	protected int getRelativeY (Touch touch, HTMLCanvasElement target) {
//		float yScaleRatio = target.getHeight() * 1f / target.getClientHeight(); // Correct for canvas CSS scaling
//		return Math.round(yScaleRatio * touch.getRelativeY(target));
//	}
	
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
