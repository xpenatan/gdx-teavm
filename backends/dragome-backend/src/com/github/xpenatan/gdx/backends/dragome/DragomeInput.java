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

package com.github.xpenatan.gdx.backends.dragome;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.KeyboardEvent;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.Touch;
import org.w3c.dom.events.TouchEvent;
import org.w3c.dom.events.TouchList;
import org.w3c.dom.html.HTMLCanvasElement;
import org.w3c.dom.html.HTMLDocument;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;
import com.github.xpenatan.gdx.backend.web.utils.KeyCodes;
import com.github.xpenatan.gdx.backends.dragome.utils.AgentInfo;

/** Ported from GWT backend.
 * @author xpenatan */
public class DragomeInput implements Input {

	Document document;
	static final int MAX_TOUCHES = 20;
	boolean justTouched = false;
	private IntMap<Integer> touchMap = new IntMap<>(20);
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

	/** The left JS mouse button. */
	private static final int BUTTON_LEFT = 1;
	/** The middle JS mouse button. */
	private static final int BUTTON_MIDDLE = 4;
	/** The right JS mouse button. */
	private static final int BUTTON_RIGHT = 2;

	public DragomeInput (DragomeApplication app) {
		this.canvas = app.graphics.canvas;
		document = app.elementBySelector.getDocument();
		hookEvents();
	}

	private void hookEvents () {

		EventListener eventKeyboardListener = new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				DragomeInput.this.handleKeyboardEvent(evt);
			}
		};
		EventListener mouseEventListener = new EventListener() {
			@Override
			public void handleEvent(Event evt) {
				DragomeInput.this.handleMouseEvent(evt);
			}
		};

		EventTarget docTarget = JsCast.castTo(document, EventTarget.class);
		EventTarget canvasTarget = JsCast.castTo(canvas, EventTarget.class);

//		canvasTarget.addEventListener("mousedown", mouseEventListener, false);
		docTarget.addEventListener("mousedown", mouseEventListener, false);
//		canvasTarget.addEventListener("mouseup", mouseEventListener, false);
		docTarget.addEventListener("mouseup", mouseEventListener, false);
//		canvasTarget.addEventListener("mousemove", mouseEventListener, false);
		docTarget.addEventListener("mousemove", mouseEventListener, false);

		docTarget.addEventListener(getMouseWheelEvent(), mouseEventListener, false);
		docTarget.addEventListener("keydown", eventKeyboardListener, false);
		docTarget.addEventListener("keyup", eventKeyboardListener, false);
		docTarget.addEventListener("keypress", eventKeyboardListener, false);

		canvasTarget.addEventListener("touchstart", mouseEventListener, true);
		canvasTarget.addEventListener("touchmove", mouseEventListener, true);
		canvasTarget.addEventListener("touchcancel", mouseEventListener, true);
		canvasTarget.addEventListener("touchend", mouseEventListener, true);
	}

	/** Kindly borrowed from PlayN. **/
	protected static String getMouseWheelEvent() {
		if (ScriptHelper.evalInt("navigator.userAgent.toLowerCase().indexOf('firefox')", null) != -1) {
			return "DOMMouseScroll";
		} else {
			return "mousewheel";
		}
	};

	/** from https://github.com/toji/game-shim/blob/master/game-shim.js
	 * @param event JavaScript Mouse Event
	 * @return movement in x direction */
	private float getMovementXJS (Event event) {
		ScriptHelper.put("event", event, this);
		return ScriptHelper.evalFloat("event.node.movementX || event.node.webkitMovementX || 0", this);
	};

	/** from https://github.com/toji/game-shim/blob/master/game-shim.js
	 * @param event JavaScript Mouse Event
	 * @return movement in y direction */
	private float getMovementYJS (Event event) {
		ScriptHelper.put("event", event, this);
		return ScriptHelper.evalFloat("event.node.movementY || event.node.webkitMovementY || 0", this);
	};

	private static boolean isTouchScreen () {
		return ScriptHelper.evalBoolean("(('ontouchstart' in window) || (navigator.msMaxTouchPoints > 0))", null);
	};

	private int getButton (int button) {
		if (button == BUTTON_LEFT) return Buttons.LEFT;
		if (button == BUTTON_RIGHT) return Buttons.RIGHT;
		if (button == BUTTON_MIDDLE) return Buttons.MIDDLE;
		return Buttons.LEFT;
	}

	private void handleMouseEvent (Event evt) {
		String type = evt.getType();
		if (type.equals("mousedown")) {
			MouseEvent e = JsCast.castTo(evt, MouseEvent.class);
			ScriptHelper.put("target1", e.getTarget(), this);
			ScriptHelper.put("target2", canvas, this);
			boolean equals = ScriptHelper.evalBoolean("target1.node == target2.node", this);
			if (!equals || touched[0]) { // TODO needs check equals bug
				float mouseX = getRelativeX(e, canvas);
				float mouseY = getRelativeY(e, canvas);
				if (mouseX < 0 || mouseX > Gdx.graphics.getWidth() || mouseY < 0 || mouseY > Gdx.graphics.getHeight()) {
					hasFocus = false;
				}
				return;
			}
			hasFocus = true;
			this.justTouched = true;
			this.touched[0] = true;
			this.pressedButtons.add(getButton(e.getButton()));
			this.deltaX[0] = 0;
			this.deltaY[0] = 0;
			if (isCursorCatched()) {
				this.touchX[0] += getMovementXJS(e);
				this.touchY[0] += getMovementYJS(e);
			} else {
				this.touchX[0] = getRelativeX(e, canvas);
				this.touchY[0] = getRelativeY(e, canvas);
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			if (processor != null)
				processor.touchDown(touchX[0], touchY[0], 0, getButton(e.getButton()));
		}
		if (type.equals("mousemove")) {
			MouseEvent e = JsCast.castTo(evt, MouseEvent.class);
			if (isCursorCatched()) {
				setDelta(0, (int)getMovementXJS(e), (int)getMovementYJS(e));
				this.touchX[0] += getMovementXJS(e);
				this.touchY[0] += getMovementYJS(e);
			} else {
				int relativeX = getRelativeX(e, canvas);
				int relativeY = getRelativeY(e, canvas);
				setDelta(0, relativeX - touchX[0], relativeY - touchY[0]);
				this.touchX[0] = relativeX;
				this.touchY[0] = relativeY;
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			if (processor != null) {
				if (touched[0])
					processor.touchDragged(touchX[0], touchY[0], 0);
				else
					processor.mouseMoved(touchX[0], touchY[0]);
			}
		}
		if (type.equals("mouseup")) {
			MouseEvent e = JsCast.castTo(evt, MouseEvent.class);
			if (!touched[0]) return;
			this.pressedButtons.remove(getButton(e.getButton()));
			this.touched[0] = pressedButtons.size > 0;
			if (isCursorCatched()) {
				setDelta(0, (int)getMovementXJS(e), (int)getMovementYJS(e));
				this.touchX[0] += getMovementXJS(e);
				this.touchY[0] += getMovementYJS(e);
			} else {
				setDelta(0, getRelativeX(e, canvas) - touchX[0], getRelativeY(e, canvas) - touchY[0]);
				this.touchX[0] = getRelativeX(e, canvas);
				this.touchY[0] = getRelativeY(e, canvas);
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			this.touched[0] = false;
			if (processor != null) processor.touchUp(touchX[0], touchY[0], 0, getButton(e.getButton()));
		}
		if (type.equals(getMouseWheelEvent())) {
			MouseEvent e = JsCast.castTo(evt, MouseEvent.class);
			if (processor != null) {
				processor.scrolled((int)getMouseWheelVelocity(e));
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			e.preventDefault();
		}
		if (type.equals("touchstart")) {
			this.justTouched = true;
			TouchEvent e = JsCast.castTo(evt, TouchEvent.class);
			TouchList touches = e.getChangedTouches();
			for (int i = 0, j = touches.getLength(); i < j; i++) {
				Touch touch = touches.item(i);
				int real = touch.getIdentifier();
				int touchId;
				touchMap.put(real, touchId = getAvailablePointer());
				touched[touchId] = true;
				touchX[touchId] = getRelativeX(touch, canvas);
				touchY[touchId] = getRelativeY(touch, canvas);
				deltaX[touchId] = 0;
				deltaY[touchId] = 0;
				if (processor != null) {
					processor.touchDown(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
				}
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			e.preventDefault();
		}
		if (type.equals("touchmove")) {
			TouchEvent e = JsCast.castTo(evt, TouchEvent.class);
			TouchList touches = e.getChangedTouches();
			for (int i = 0, j = touches.getLength(); i < j; i++) {
				Touch touch = touches.item(i);
				int real = touch.getIdentifier();
				int touchId = touchMap.get(real);
				setDelta(touchId, getRelativeX(touch, canvas) - touchX[touchId], getRelativeY(touch, canvas) - touchY[touchId]);
				touchX[touchId] = getRelativeX(touch, canvas);
				touchY[touchId] = getRelativeY(touch, canvas);
				if (processor != null) {
					processor.touchDragged(touchX[touchId], touchY[touchId], touchId);
				}
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			e.preventDefault();
		}
		if (type.equals("touchcancel")) {
			TouchEvent e = JsCast.castTo(evt, TouchEvent.class);
			TouchList touches = e.getChangedTouches();
			for (int i = 0, j = touches.getLength(); i < j; i++) {
				Touch touch = touches.item(i);
				int real = touch.getIdentifier();
				int touchId = touchMap.get(real);
				touchMap.remove(real);
				touched[touchId] = false;
				int relativeX = getRelativeX(touch, canvas);
				int relativeY = getRelativeY(touch, canvas);
				setDelta(touchId, relativeX - touchX[touchId], relativeY - touchY[touchId]);
				touchX[touchId] = relativeX;
				touchY[touchId] = relativeY;
				if (processor != null) {
					processor.touchUp(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
				}
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			e.preventDefault();
		}
		if (type.equals("touchend")) {
			TouchEvent e = JsCast.castTo(evt, TouchEvent.class);
			TouchList touches = e.getChangedTouches();
			for (int i = 0, j = touches.getLength(); i < j; i++) {
				Touch touch = touches.item(i);
				int real = touch.getIdentifier();
				int touchId = touchMap.get(real);
				touchMap.remove(real);
				touched[touchId] = false;
				int relativeX = getRelativeX(touch, canvas);
				int relativeY = getRelativeY(touch, canvas);
				setDelta(touchId, relativeX - touchX[touchId], relativeY - touchY[touchId]);
				touchX[touchId] = relativeX;
				touchY[touchId] = relativeY;
				if (processor != null) {
					processor.touchUp(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
				}
			}
			this.currentEventTimeStamp = TimeUtils.nanoTime();
			e.preventDefault();
		}

	}

	private void handleKeyboardEvent (Event evt) {
		String type = evt.getType();
		if (type.equals("keydown") && hasFocus) {
			KeyboardEvent e = JsCast.castTo(evt, KeyboardEvent.class);
			int code = keyForCode(e.getKeyCode());
			char keyChar = 0;

			switch (code) {
				case Keys.DEL:
					keyChar = 8;
					break;
				case Keys.FORWARD_DEL:
					keyChar = 127;
					break;
				}

			if (code == Input.Keys.DEL || code == Keys.FORWARD_DEL) {
				e.preventDefault();
				if (processor != null) {
					processor.keyDown(code);
					processor.keyTyped(keyChar);
				}
			}
			else {
				if (!pressedKeys[code]) {
					pressedKeyCount++;
					pressedKeys[code] = true;
					keyJustPressed = true;
					justPressedKeys[code] = true;
					if (processor != null) {
						processor.keyDown(code);
					}
				}
			}
		}
		if (type.equals("keypress") && hasFocus) {
			KeyboardEvent e = JsCast.castTo(evt, KeyboardEvent.class);
			char c = (char)e.getCharCode();
			if (processor != null) processor.keyTyped(c);
		}

		if (type.equals("keyup") && hasFocus) {
			KeyboardEvent e = JsCast.castTo(evt, KeyboardEvent.class);
			int code = keyForCode(e.getKeyCode());
			if (pressedKeys[code]) {
				pressedKeyCount--;
				pressedKeys[code] = false;
			}
			if (processor != null) {
				processor.keyUp(code);
			}
		}
	}

	public void setDelta(int touchId, int x, int y) {

//		if(x == 0 && y == 0) {
//
//			System.out.println("TOUCHHHHHH333");
//		}
		deltaX[touchId] = x;
		deltaY[touchId] = y;
	}

	private int getAvailablePointer () {
		for (int i = 0; i < MAX_TOUCHES; i++) {
			if (!touchMap.containsValue(i, false)) return i;
		}
		return -1;
	}

	private static float getMouseWheelVelocity (MouseEvent event) {
		ScriptHelper.put("event", event, null);
		float delta = 0;
		ScriptHelper.put("delta", delta, null);
		AgentInfo agent = DragomeApplication.agentInfo();
		ScriptHelper.put("agent", agent, null);
		Object agentInfo = ScriptHelper.eval("agent.node", null);
		ScriptHelper.put("agentInfo", agentInfo, null);
		Object evt = ScriptHelper.eval("event.node", null);
		ScriptHelper.put("evt", evt, null);
		ScriptHelper.evalNoResult("if(agentInfo.isFirefox){if(agentInfo.isMacOS){delta=1.0*evt.detail;}else{delta = 1.0*evt.detail/3;}}"
			+ "else if(agentInfo.isOpera){if(agentInfo.isLinux){delta=-1.0*evt.wheelDelta/80;}else{delta=-1.0*evt.wheelDelta/40;}}"
			+ "else if(agentInfo.isChrome||agentInfo.isSafari){delta=-1.0*evt.wheelDelta/120;"
			+ "if(Math.abs(delta)<1){if(agentInfo.isWindows){delta=-1.0*evt.wheelDelta;}else if(agentInfo.isMacOS){delta=-1.0*evt.wheelDelta/3;}}}", null);
		return ScriptHelper.evalFloat("delta", null);
	}

	private static int toInt32 (double val) {
		ScriptHelper.put("val", val, null);
		return ScriptHelper.evalInt("val | 0", null);
	}

	private int getClientWidth (HTMLCanvasElement target) {
		ScriptHelper.put("target", target, this);
		int val = ScriptHelper.evalInt("target.node.clientWidth", this); // FIXME change when dragome is fixed.
		int int32 = toInt32(val);
		return int32;
	}

	private int getClientHeight (HTMLCanvasElement target) {
		ScriptHelper.put("target", target, this);
		int val = ScriptHelper.evalInt("target.node.clientHeight", this); // FIXME change when dragome is fixed.
		return toInt32(val);
	}

	private double getSubPixelAbsoluteLeft (Element elem) {
		ScriptHelper.put("elem", elem, this);
		ScriptHelper.evalNoResult("var left = 0;var curr = elem;"
			+ "while(curr.offsetParent){left -= curr.scrollLeft; curr = curr.parentNode;}"
			+ "while(elem){left += elem.offsetLeft; elem = elem.offsetParent;}", this);
		return ScriptHelper.evalDouble("left", this);
	}

	private double getSubPixelAbsoluteTop (Element elem) {
		ScriptHelper.put("elem", elem, this);
		ScriptHelper.evalNoResult("var top = 0;var curr = elem;"
			+ "while(curr.offsetParent){top -= curr.scrollTop;curr = curr.parentNode;}"
			+ "while(elem){top += elem.offsetTop;elem = elem.offsetParent;}", this);
		return ScriptHelper.evalDouble("top", this);
	}

	private int getAbsoluteLeft (HTMLCanvasElement target) {
		return toInt32(getSubPixelAbsoluteLeft(target));
	}

	private int getAbsoluteTop (HTMLCanvasElement target) {
		return toInt32(getSubPixelAbsoluteTop(target));
	}

	private Element getCompatMode (HTMLDocument target) {
		ScriptHelper.put("target", target, this);
		String compatMode = (String)ScriptHelper.eval("target.node.compatMode", this); // FIXME change when dragome is fixed.
		boolean isComp = compatMode.equals("CSS1Compat");
//		Element element = isComp ? target.getDocumentElement() : target.getBody(); //FIXME getBody dont exist. need to investigate.
		Element element = isComp ? target.getDocumentElement() : (Element)ScriptHelper.eval("target", this);
		return element;
	}

	private int getScrollLeft (Element target) {
		ScriptHelper.put("target", target, this);
		int val = ScriptHelper.evalInt("target.node.scrollLeft", this); // FIXME change when dragome is fixed.
		return toInt32(val);
	}

	private int getScrollLeft (HTMLDocument target) {
		Element element = getCompatMode(target);
		return getScrollLeft(element);
	}

	private int getScrollTop (Element target) {
		ScriptHelper.put("target", target, this);
		int val = ScriptHelper.evalInt("target.node.scrollTop", this); // FIXME change when dragome is fixed.
		return toInt32(val);
	}

	private int getScrollTop (HTMLDocument target) {
		Element element = getCompatMode(target);
		return getScrollTop(element);
	}

	private int getRelativeX (HTMLCanvasElement target, Touch touch) {
		return touch.getClientX() - getAbsoluteLeft(target) + getScrollLeft(target)
			+ getScrollLeft((HTMLDocument)target.getOwnerDocument());
	}

	private int getRelativeY (HTMLCanvasElement target, Touch touch) {
		return touch.getClientY() - getAbsoluteTop(target) + getScrollTop(target)
			+ getScrollTop((HTMLDocument)target.getOwnerDocument());
	}

//	/** Kindly borrowed from PlayN. **/
	protected int getRelativeX (MouseEvent e, HTMLCanvasElement target) {
		float xScaleRatio = target.getWidth() * 1f / getClientWidth(target); // Correct for canvas CSS scaling
		return Math.round(xScaleRatio
			* (e.getClientX() - getAbsoluteLeft(target) + getScrollLeft(target) + getScrollLeft((HTMLDocument)target.getOwnerDocument())));
	}

	/** Kindly borrowed from PlayN. **/
	protected int getRelativeY (MouseEvent e, HTMLCanvasElement target) {
		float yScaleRatio = target.getHeight() * 1f / getClientHeight(target); // Correct for canvas CSS scaling
		return Math.round(yScaleRatio
			* (e.getClientY() - getAbsoluteTop(target) + getScrollTop(target) + getScrollTop((HTMLDocument)target.getOwnerDocument())));
	}

	protected int getRelativeX (Touch touch, HTMLCanvasElement target) {
		float xScaleRatio = target.getWidth() * 1f / getClientWidth(target); // Correct for canvas CSS scaling
		return Math.round(xScaleRatio * getRelativeX(target, touch));
	}

	protected int getRelativeY (Touch touch, HTMLCanvasElement target) {
		float yScaleRatio = target.getHeight() * 1f / getClientHeight(target); // Correct for canvas CSS scaling
		return Math.round(yScaleRatio * getRelativeY(target, touch));
	}

	public void reset () {
		justTouched = false;
		if (keyJustPressed) {
			keyJustPressed = false;
			for (int i = 0; i < justPressedKeys.length; i++) {
				justPressedKeys[i] = false;
			}
		}
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
		return touchX[0];
	}

	@Override
	public int getX (int pointer) {
		return touchX[pointer];
	}

	@Override
	public int getDeltaX () {
		return deltaX[0];
	}

	@Override
	public int getDeltaX (int pointer) {
		return deltaX[pointer];
	}

	@Override
	public int getY () {
		return touchY[0];
	}

	@Override
	public int getY (int pointer) {
		return touchY[pointer];
	}

	@Override
	public int getDeltaY () {
		return deltaY[0];
	}

	@Override
	public int getDeltaY (int pointer) {
		return deltaY[pointer];
	}

	@Override
	public boolean isTouched () {
		for (int pointer = 0; pointer < MAX_TOUCHES; pointer++) {
			if (touched[pointer]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean justTouched () {
		return justTouched;
	}

	@Override
	public boolean isTouched (int pointer) {
		return touched[pointer];
	}

	@Override
	public boolean isButtonPressed (int button) {
		return pressedButtons.contains(button) && touched[0];
	}

	@Override
	public boolean isKeyPressed (int key) {
		if (key == Keys.ANY_KEY) {
			return pressedKeyCount > 0;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return pressedKeys[key];
	}

	@Override
	public boolean isKeyJustPressed (int key) {
		if (key == Keys.ANY_KEY) {
			return keyJustPressed;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return justPressedKeys[key];
	}

	@Override
	public void getTextInput (TextInputListener listener, String title, String text, String hint) {
//		TextInputDialogBox dialog = new TextInputDialogBox(title, text, hint); // TODO need impl
//		final TextInputListener capturedListener = listener;
//		dialog.setListener(new TextInputDialogListener() {
//			@Override
//			public void onPositive (String text) {
//				if (capturedListener != null) {
//					capturedListener.input(text);
//				}
//			}
//
//			@Override
//			public void onNegative () {
//				if (capturedListener != null) {
//					capturedListener.canceled();
//				}
//			}
//		});
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
		return currentEventTimeStamp;
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
		this.processor = processor;
	}

	@Override
	public InputProcessor getInputProcessor () {
		return processor;
	}

	@Override
	public boolean isPeripheralAvailable (Peripheral peripheral) {
		if (peripheral == Peripheral.Accelerometer) return false;
		if (peripheral == Peripheral.Compass) return false;
		if (peripheral == Peripheral.HardwareKeyboard) return true;
		if (peripheral == Peripheral.MultitouchScreen) return isTouchScreen();
		if (peripheral == Peripheral.OnscreenKeyboard) return false;
		if (peripheral == Peripheral.Vibrator) return false;
		return false;
	}

	@Override
	public int getRotation () {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation () {
		return Orientation.Landscape;
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

	/** borrowed from PlayN, thanks guys **/
	private static int keyForCode (int keyCode) {
		switch (keyCode) {
		case KeyCodes.KEY_ALT:
			return Keys.ALT_LEFT;
		case KeyCodes.KEY_BACKSPACE:
			return Keys.BACKSPACE;
		case KeyCodes.KEY_CTRL:
			return Keys.CONTROL_LEFT;
		case KeyCodes.KEY_DELETE:
			return Keys.FORWARD_DEL;
		case KeyCodes.KEY_DOWN:
			return Keys.DOWN;
		case KeyCodes.KEY_END:
			return Keys.END;
		case KeyCodes.KEY_ENTER:
			return Keys.ENTER;
		case KeyCodes.KEY_ESCAPE:
			return Keys.ESCAPE;
		case KeyCodes.KEY_HOME:
			return Keys.HOME;
		case KeyCodes.KEY_LEFT:
			return Keys.LEFT;
		case KeyCodes.KEY_PAGEDOWN:
			return Keys.PAGE_DOWN;
		case KeyCodes.KEY_PAGEUP:
			return Keys.PAGE_UP;
		case KeyCodes.KEY_RIGHT:
			return Keys.RIGHT;
		case KeyCodes.KEY_SHIFT:
			return Keys.SHIFT_LEFT;
		case KeyCodes.KEY_TAB:
			return Keys.TAB;
		case KeyCodes.KEY_UP:
			return Keys.UP;

		case KEY_PAUSE:
			return Keys.UNKNOWN; // FIXME
		case KEY_CAPS_LOCK:
			return Keys.UNKNOWN; // FIXME
		case KEY_SPACE:
			return Keys.SPACE;
		case KEY_INSERT:
			return Keys.INSERT;
		case KEY_0:
			return Keys.NUM_0;
		case KEY_1:
			return Keys.NUM_1;
		case KEY_2:
			return Keys.NUM_2;
		case KEY_3:
			return Keys.NUM_3;
		case KEY_4:
			return Keys.NUM_4;
		case KEY_5:
			return Keys.NUM_5;
		case KEY_6:
			return Keys.NUM_6;
		case KEY_7:
			return Keys.NUM_7;
		case KEY_8:
			return Keys.NUM_8;
		case KEY_9:
			return Keys.NUM_9;
		case KEY_A:
			return Keys.A;
		case KEY_B:
			return Keys.B;
		case KEY_C:
			return Keys.C;
		case KEY_D:
			return Keys.D;
		case KEY_E:
			return Keys.E;
		case KEY_F:
			return Keys.F;
		case KEY_G:
			return Keys.G;
		case KEY_H:
			return Keys.H;
		case KEY_I:
			return Keys.I;
		case KEY_J:
			return Keys.J;
		case KEY_K:
			return Keys.K;
		case KEY_L:
			return Keys.L;
		case KEY_M:
			return Keys.M;
		case KEY_N:
			return Keys.N;
		case KEY_O:
			return Keys.O;
		case KEY_P:
			return Keys.P;
		case KEY_Q:
			return Keys.Q;
		case KEY_R:
			return Keys.R;
		case KEY_S:
			return Keys.S;
		case KEY_T:
			return Keys.T;
		case KEY_U:
			return Keys.U;
		case KEY_V:
			return Keys.V;
		case KEY_W:
			return Keys.W;
		case KEY_X:
			return Keys.X;
		case KEY_Y:
			return Keys.Y;
		case KEY_Z:
			return Keys.Z;
		case KEY_LEFT_WINDOW_KEY:
			return Keys.UNKNOWN; // FIXME
		case KEY_RIGHT_WINDOW_KEY:
			return Keys.UNKNOWN; // FIXME
			// case KEY_SELECT_KEY: return Keys.SELECT_KEY;
		case KEY_NUMPAD0:
			return Keys.NUMPAD_0;
		case KEY_NUMPAD1:
			return Keys.NUMPAD_1;
		case KEY_NUMPAD2:
			return Keys.NUMPAD_2;
		case KEY_NUMPAD3:
			return Keys.NUMPAD_3;
		case KEY_NUMPAD4:
			return Keys.NUMPAD_4;
		case KEY_NUMPAD5:
			return Keys.NUMPAD_5;
		case KEY_NUMPAD6:
			return Keys.NUMPAD_6;
		case KEY_NUMPAD7:
			return Keys.NUMPAD_7;
		case KEY_NUMPAD8:
			return Keys.NUMPAD_8;
		case KEY_NUMPAD9:
			return Keys.NUMPAD_9;
		case KEY_MULTIPLY:
			return Keys.UNKNOWN; // FIXME
		case KEY_ADD:
			return Keys.PLUS;
		case KEY_SUBTRACT:
			return Keys.MINUS;
		case KEY_DECIMAL_POINT_KEY:
			return Keys.PERIOD;
		case KEY_DIVIDE:
			return Keys.UNKNOWN; // FIXME
		case KEY_F1:
			return Keys.F1;
		case KEY_F2:
			return Keys.F2;
		case KEY_F3:
			return Keys.F3;
		case KEY_F4:
			return Keys.F4;
		case KEY_F5:
			return Keys.F5;
		case KEY_F6:
			return Keys.F6;
		case KEY_F7:
			return Keys.F7;
		case KEY_F8:
			return Keys.F8;
		case KEY_F9:
			return Keys.F9;
		case KEY_F10:
			return Keys.F10;
		case KEY_F11:
			return Keys.F11;
		case KEY_F12:
			return Keys.F12;
		case KEY_NUM_LOCK:
			return Keys.NUM;
		case KEY_SCROLL_LOCK:
			return Keys.UNKNOWN; // FIXME
		case KEY_SEMICOLON:
			return Keys.SEMICOLON;
		case KEY_EQUALS:
			return Keys.EQUALS;
		case KEY_COMMA:
			return Keys.COMMA;
		case KEY_DASH:
			return Keys.MINUS;
		case KEY_PERIOD:
			return Keys.PERIOD;
		case KEY_FORWARD_SLASH:
			return Keys.SLASH;
		case KEY_GRAVE_ACCENT:
			return Keys.UNKNOWN; // FIXME
		case KEY_OPEN_BRACKET:
			return Keys.LEFT_BRACKET;
		case KEY_BACKSLASH:
			return Keys.BACKSLASH;
		case KEY_CLOSE_BRACKET:
			return Keys.RIGHT_BRACKET;
		case KEY_SINGLE_QUOTE:
			return Keys.APOSTROPHE;
		default:
			return Keys.UNKNOWN;
		}
	}

	// these are absent from KeyCodes; we know not why...
	private static final int KEY_PAUSE = 19;
	private static final int KEY_CAPS_LOCK = 20;
	private static final int KEY_SPACE = 32;
	private static final int KEY_INSERT = 45;
	private static final int KEY_0 = 48;
	private static final int KEY_1 = 49;
	private static final int KEY_2 = 50;
	private static final int KEY_3 = 51;
	private static final int KEY_4 = 52;
	private static final int KEY_5 = 53;
	private static final int KEY_6 = 54;
	private static final int KEY_7 = 55;
	private static final int KEY_8 = 56;
	private static final int KEY_9 = 57;
	private static final int KEY_A = 65;
	private static final int KEY_B = 66;
	private static final int KEY_C = 67;
	private static final int KEY_D = 68;
	private static final int KEY_E = 69;
	private static final int KEY_F = 70;
	private static final int KEY_G = 71;
	private static final int KEY_H = 72;
	private static final int KEY_I = 73;
	private static final int KEY_J = 74;
	private static final int KEY_K = 75;
	private static final int KEY_L = 76;
	private static final int KEY_M = 77;
	private static final int KEY_N = 78;
	private static final int KEY_O = 79;
	private static final int KEY_P = 80;
	private static final int KEY_Q = 81;
	private static final int KEY_R = 82;
	private static final int KEY_S = 83;
	private static final int KEY_T = 84;
	private static final int KEY_U = 85;
	private static final int KEY_V = 86;
	private static final int KEY_W = 87;
	private static final int KEY_X = 88;
	private static final int KEY_Y = 89;
	private static final int KEY_Z = 90;
	private static final int KEY_LEFT_WINDOW_KEY = 91;
	private static final int KEY_RIGHT_WINDOW_KEY = 92;
	private static final int KEY_SELECT_KEY = 93;
	private static final int KEY_NUMPAD0 = 96;
	private static final int KEY_NUMPAD1 = 97;
	private static final int KEY_NUMPAD2 = 98;
	private static final int KEY_NUMPAD3 = 99;
	private static final int KEY_NUMPAD4 = 100;
	private static final int KEY_NUMPAD5 = 101;
	private static final int KEY_NUMPAD6 = 102;
	private static final int KEY_NUMPAD7 = 103;
	private static final int KEY_NUMPAD8 = 104;
	private static final int KEY_NUMPAD9 = 105;
	private static final int KEY_MULTIPLY = 106;
	private static final int KEY_ADD = 107;
	private static final int KEY_SUBTRACT = 109;
	private static final int KEY_DECIMAL_POINT_KEY = 110;
	private static final int KEY_DIVIDE = 111;
	private static final int KEY_F1 = 112;
	private static final int KEY_F2 = 113;
	private static final int KEY_F3 = 114;
	private static final int KEY_F4 = 115;
	private static final int KEY_F5 = 116;
	private static final int KEY_F6 = 117;
	private static final int KEY_F7 = 118;
	private static final int KEY_F8 = 119;
	private static final int KEY_F9 = 120;
	private static final int KEY_F10 = 121;
	private static final int KEY_F11 = 122;
	private static final int KEY_F12 = 123;
	private static final int KEY_NUM_LOCK = 144;
	private static final int KEY_SCROLL_LOCK = 145;
	private static final int KEY_SEMICOLON = 186;
	private static final int KEY_EQUALS = 187;
	private static final int KEY_COMMA = 188;
	private static final int KEY_DASH = 189;
	private static final int KEY_PERIOD = 190;
	private static final int KEY_FORWARD_SLASH = 191;
	private static final int KEY_GRAVE_ACCENT = 192;
	private static final int KEY_OPEN_BRACKET = 219;
	private static final int KEY_BACKSLASH = 220;
	private static final int KEY_CLOSE_BRACKET = 221;
	private static final int KEY_SINGLE_QUOTE = 222;

	public int getMaxPointers () {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getPressure () {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getPressure (int pointer) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isButtonJustPressed (int button) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCatchKey (int keycode, boolean catchKey) {
		// TODO Auto-generated method stub

	}

	public boolean isCatchKey (int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

}
