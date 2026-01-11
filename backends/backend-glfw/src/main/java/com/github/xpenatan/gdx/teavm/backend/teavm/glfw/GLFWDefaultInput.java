package com.github.xpenatan.gdx.teavm.backend.teavm.glfw;

import com.badlogic.gdx.AbstractInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.badlogic.gdx.input.NativeInputConfiguration;
import com.github.xpenatan.gdx.teavm.backend.teavm.glfw.utils.GLFW;
import java.util.HashMap;
import java.util.Map;
import org.teavm.interop.Address;
import org.teavm.interop.Function;

public class GLFWDefaultInput extends AbstractInput implements GLFWInput {
    private static final Map<Long, GLFWDefaultInput> byWindow = new HashMap<>();
    final GLFWWindow window;
    private InputProcessor inputProcessor;
    final InputEventQueue eventQueue = new InputEventQueue();

    int mouseX, mouseY;
    int mousePressed;
    int deltaX, deltaY;
    boolean justTouched;
    final boolean[] justPressedButtons = new boolean[5];
    char lastCharacter;

    private final GLFW.GLFWKeyCallback keyCallback = Function.get(GLFW.GLFWKeyCallback.class, GLFWDefaultInput.class, "keyCallback");

    GLFW.GLFWCharCallback charCallback = Function.get(GLFW.GLFWCharCallback.class, GLFWDefaultInput.class, "charCallback");
    int pressedKeyCount;
    boolean keyJustPressed;
    final boolean[] pressedKeys = new boolean[512];
    final boolean[] justPressedKeys = new boolean[512];

    static void charCallback(Address window, int codepoint) {
        try {
            GLFWDefaultInput input = GLFWDefaultInput.byWindow(window);

            if ((codepoint & 0xff00) == 0xf700) return;
            input.lastCharacter = (char) codepoint;
            input.window.getGraphics().requestRendering();
            input.eventQueue.keyTyped((char) codepoint, System.nanoTime());
        } catch (Throwable t) {
            Gdx.app.error("NativeInput", "Error in charCallback", t);
        }
    }

    private final GLFW.GLFWScrollCallback scrollCallback = Function.get(GLFW.GLFWScrollCallback.class, GLFWDefaultInput.class, "scrollCallback");

    public static void scrollCallback(Address window, double scrollX, double scrollY) {
        try {
            GLFWDefaultInput input = GLFWDefaultInput.byWindow(window);
            input.window.getGraphics().requestRendering();
            input.eventQueue.scrolled(-(float) scrollX, -(float) scrollY, System.nanoTime());
        } catch (Throwable t) {
            Gdx.app.error("NativeInput", "Error in scrollCallback", t);
        }
    }

    private final GLFW.GLFWCursorPosCallback cursorPosCallback = Function.get(GLFW.GLFWCursorPosCallback.class, GLFWDefaultInput.class, "cursorPosCallback");

    public static void cursorPosCallback(Address windowHandle, double x, double y) {
        try {
            GLFWDefaultInput input = GLFWDefaultInput.byWindow(windowHandle);
            int logicalMouseY = (int) y;
            int logicalMouseX = (int) x;
            input.deltaX = (int) x - logicalMouseX;
            input.deltaY = (int) y - logicalMouseY;
            input.mouseX = logicalMouseX;
            input.mouseY = logicalMouseY;

            if (input.window.getConfig().hdpiMode == HdpiMode.Pixels) {
                float xScale = input.window.getGraphics().getBackBufferWidth() / (float) input.window.getGraphics().getLogicalWidth();
                float yScale = input.window.getGraphics().getBackBufferHeight() / (float) input.window.getGraphics().getLogicalHeight();
                input.deltaX = (int) (input.deltaX * xScale);
                input.deltaY = (int) (input.deltaY * yScale);
                input.mouseX = (int) (input.mouseX * xScale);
                input.mouseY = (int) (input.mouseY * yScale);
            }

            input.window.getGraphics().requestRendering();
            long time = System.nanoTime();
            if (input.mousePressed > 0) {
                input.eventQueue.touchDragged(input.mouseX, input.mouseY, 0, time);
            } else {
                input.eventQueue.mouseMoved(input.mouseX, input.mouseY, time);
            }
        } catch (Throwable t) {
            Gdx.app.error("NativeInput", "Error in cursorPosCallback", t);
        }
    }

    private final GLFW.GLFWMouseButtonCallback mouseButtonCallback = Function.get(GLFW.GLFWMouseButtonCallback.class, GLFWDefaultInput.class, "mouseButtonCallback");

    public static void mouseButtonCallback(Address windowHandle, int button, int action, int mods) {
        try {
            GLFWDefaultInput input = byWindow(windowHandle);
            int gdxButton = toGdxButton(button);
            if (button != -1 && gdxButton == -1) return;

            long time = System.nanoTime();
            if (action == GLFW.GLFW_PRESS) {
                input.mousePressed++;
                input.justTouched = true;
                input.justPressedButtons[gdxButton] = true;
                input.window.getGraphics().requestRendering();
                input.eventQueue.touchDown(input.mouseX, input.mouseY, 0, gdxButton, time);
            } else {
                input.mousePressed = Math.max(0, input.mousePressed - 1);
                input.window.getGraphics().requestRendering();
                input.eventQueue.touchUp(input.mouseX, input.mouseY, 0, gdxButton, time);
            }
        } catch (Throwable t) {
            Gdx.app.error("NativeInput", "Error in mouseButtonCallback", t);
        }
    }

    private static int toGdxButton(int button) {
        if (button == 0) return Buttons.LEFT;
        if (button == 1) return Buttons.RIGHT;
        if (button == 2) return Buttons.MIDDLE;
        if (button == 3) return Buttons.BACK;
        if (button == 4) return Buttons.FORWARD;
        return -1;
    }

    public GLFWDefaultInput(GLFWWindow window) {
        this.window = window;
        windowHandleChanged(window.getWindowHandle());
    }

    static void keyCallback(Address window, int key, int scancode, int action, int mods) {
        try {
            GLFWDefaultInput input = GLFWDefaultInput.byWindow(window);
            switch (action) {
                case GLFW.GLFW_PRESS:
                    key = input.getGdxKeyCode(key);
                    input.eventQueue.keyDown(key, System.nanoTime());
                    input.pressedKeyCount++;
                    input.keyJustPressed = true;
                    input.pressedKeys[key] = true;
                    input.justPressedKeys[key] = true;
                    input.window.getGraphics().requestRendering();
                    input.lastCharacter = 0;
                    char character = input.characterForKeyCode(key);
                    if (character != 0) input.charCallback.invoke(window, character);
                    break;
                case GLFW.GLFW_RELEASE:
                    key = input.getGdxKeyCode(key);
                    input.pressedKeyCount--;
                    input.pressedKeys[key] = false;
                    input.window.getGraphics().requestRendering();
                    input.eventQueue.keyUp(key, System.nanoTime());
                    break;
                case GLFW.GLFW_REPEAT:
                    if (input.lastCharacter != 0) {
                        input.window.getGraphics().requestRendering();
                        input.eventQueue.keyTyped(input.lastCharacter, System.nanoTime());
                    }
                    break;
            }
        } catch (Throwable t) {
            Gdx.app.error("NativeInput", "Error in keyCallback", t);
        }
    }

    private static GLFWDefaultInput byWindow(Address window) {
        return byWindow.get(window.toLong());
    }

    @Override
    public void resetPollingStates() {
        justTouched = false;
        keyJustPressed = false;
        for (int i = 0; i < justPressedKeys.length; i++) {
            justPressedKeys[i] = false;
        }
        for (int i = 0; i < justPressedButtons.length; i++) {
            justPressedButtons[i] = false;
        }
        eventQueue.drain(null);
    }

    @Override
    public void windowHandleChanged(long windowHandle) {
        resetPollingStates();
        byWindow.put(windowHandle, this);
        GLFW.setKeyCallback(window.getWindowHandle(), keyCallback);
        GLFW.setCharCallback(window.getWindowHandle(), charCallback);
        GLFW.setScrollCallback(window.getWindowHandle(), scrollCallback);
        GLFW.setCursorPosCallback(window.getWindowHandle(), cursorPosCallback);
        GLFW.setMouseButtonCallback(window.getWindowHandle(), mouseButtonCallback);
    }

    @Override
    public void update() {
        eventQueue.drain(inputProcessor);
    }

    @Override
    public void prepareNext() {
        if (justTouched) {
            justTouched = false;
            for (int i = 0; i < justPressedButtons.length; i++) {
                justPressedButtons[i] = false;
            }
        }

        if (keyJustPressed) {
            keyJustPressed = false;
            for (int i = 0; i < justPressedKeys.length; i++) {
                justPressedKeys[i] = false;
            }
        }
        deltaX = 0;
        deltaY = 0;
    }

    @Override
    public int getMaxPointers() {
        return 1;
    }

    @Override
    public int getX() {
        return mouseX;
    }

    @Override
    public int getX(int pointer) {
        return pointer == 0 ? mouseX : 0;
    }

    @Override
    public int getDeltaX() {
        return deltaX;
    }

    @Override
    public int getDeltaX(int pointer) {
        return pointer == 0 ? deltaX : 0;
    }

    @Override
    public int getY() {
        return mouseY;
    }

    @Override
    public int getY(int pointer) {
        return pointer == 0 ? mouseY : 0;
    }

    @Override
    public int getDeltaY() {
        return deltaY;
    }

    @Override
    public int getDeltaY(int pointer) {
        return pointer == 0 ? deltaY : 0;
    }

    @Override
    public boolean isTouched() {
        return GLFW.getMouseButton(window.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS
                || GLFW.getMouseButton(window.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS
                || GLFW.getMouseButton(window.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_3) == GLFW.GLFW_PRESS
                || GLFW.getMouseButton(window.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_4) == GLFW.GLFW_PRESS
                || GLFW.getMouseButton(window.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_5) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean justTouched() {
        return justTouched;
    }

    @Override
    public boolean isTouched(int pointer) {
        return pointer == 0 && isTouched();
    }

    @Override
    public float getPressure() {
        return getPressure(0);
    }

    @Override
    public float getPressure(int pointer) {
        return isTouched(pointer) ? 1 : 0;
    }

    @Override
    public boolean isButtonPressed(int button) {
        return GLFW.getMouseButton(window.getWindowHandle(), button) == GLFW.GLFW_PRESS;
    }

    @Override
    public boolean isButtonJustPressed(int button) {
        if (button < 0 || button >= justPressedButtons.length) {
            return false;
        }
        return justPressedButtons[button];
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint) {
        getTextInput(listener, title, text, hint, OnscreenKeyboardType.Default);
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint, OnscreenKeyboardType type) {
        // FIXME getTextInput does nothing
        listener.canceled();
    }

    @Override
    public long getCurrentEventTime() {
        // queue sets its event time for each event dequeued/processed
        return eventQueue.getCurrentEventTime();
    }

    @Override
    public void setInputProcessor(InputProcessor processor) {
        this.inputProcessor = processor;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void setCursorCatched(boolean catched) {
        GLFW.setInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR,
                catched ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    @Override
    public boolean isCursorCatched() {
        return GLFW.getInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR) == GLFW.GLFW_CURSOR_DISABLED;
    }

    @Override
    public void setCursorPosition(int x, int y) {
        if (window.getConfig().hdpiMode == HdpiMode.Pixels) {
            float xScale = window.getGraphics().getLogicalWidth() / (float) window.getGraphics().getBackBufferWidth();
            float yScale = window.getGraphics().getLogicalHeight() / (float) window.getGraphics().getBackBufferHeight();
            x = (int) (x * xScale);
            y = (int) (y * yScale);
        }
        GLFW.setCursorPos(window.getWindowHandle(), x, y);
        cursorPosCallback.invoke(Address.fromLong(window.getWindowHandle()), x, y);
    }

    protected char characterForKeyCode(int key) {
        // Map certain key codes to character codes.
        switch (key) {
            case Keys.BACKSPACE:
                return 8;
            case Keys.TAB:
                return '\t';
            case Keys.FORWARD_DEL:
                return 127;
            case Keys.NUMPAD_ENTER:
            case Keys.ENTER:
                return '\n';
        }
        return 0;
    }

    public int getGdxKeyCode(int lwjglKeyCode) {
        switch (lwjglKeyCode) {
            case GLFW.GLFW_KEY_SPACE:
                return Keys.SPACE;
            case GLFW.GLFW_KEY_APOSTROPHE:
                return Keys.APOSTROPHE;
            case GLFW.GLFW_KEY_COMMA:
                return Keys.COMMA;
            case GLFW.GLFW_KEY_MINUS:
                return Keys.MINUS;
            case GLFW.GLFW_KEY_PERIOD:
                return Keys.PERIOD;
            case GLFW.GLFW_KEY_SLASH:
                return Keys.SLASH;
            case GLFW.GLFW_KEY_0:
                return Keys.NUM_0;
            case GLFW.GLFW_KEY_1:
                return Keys.NUM_1;
            case GLFW.GLFW_KEY_2:
                return Keys.NUM_2;
            case GLFW.GLFW_KEY_3:
                return Keys.NUM_3;
            case GLFW.GLFW_KEY_4:
                return Keys.NUM_4;
            case GLFW.GLFW_KEY_5:
                return Keys.NUM_5;
            case GLFW.GLFW_KEY_6:
                return Keys.NUM_6;
            case GLFW.GLFW_KEY_7:
                return Keys.NUM_7;
            case GLFW.GLFW_KEY_8:
                return Keys.NUM_8;
            case GLFW.GLFW_KEY_9:
                return Keys.NUM_9;
            case GLFW.GLFW_KEY_SEMICOLON:
                return Keys.SEMICOLON;
            case GLFW.GLFW_KEY_EQUAL:
                return Keys.EQUALS;
            case GLFW.GLFW_KEY_A:
                return Keys.A;
            case GLFW.GLFW_KEY_B:
                return Keys.B;
            case GLFW.GLFW_KEY_C:
                return Keys.C;
            case GLFW.GLFW_KEY_D:
                return Keys.D;
            case GLFW.GLFW_KEY_E:
                return Keys.E;
            case GLFW.GLFW_KEY_F:
                return Keys.F;
            case GLFW.GLFW_KEY_G:
                return Keys.G;
            case GLFW.GLFW_KEY_H:
                return Keys.H;
            case GLFW.GLFW_KEY_I:
                return Keys.I;
            case GLFW.GLFW_KEY_J:
                return Keys.J;
            case GLFW.GLFW_KEY_K:
                return Keys.K;
            case GLFW.GLFW_KEY_L:
                return Keys.L;
            case GLFW.GLFW_KEY_M:
                return Keys.M;
            case GLFW.GLFW_KEY_N:
                return Keys.N;
            case GLFW.GLFW_KEY_O:
                return Keys.O;
            case GLFW.GLFW_KEY_P:
                return Keys.P;
            case GLFW.GLFW_KEY_Q:
                return Keys.Q;
            case GLFW.GLFW_KEY_R:
                return Keys.R;
            case GLFW.GLFW_KEY_S:
                return Keys.S;
            case GLFW.GLFW_KEY_T:
                return Keys.T;
            case GLFW.GLFW_KEY_U:
                return Keys.U;
            case GLFW.GLFW_KEY_V:
                return Keys.V;
            case GLFW.GLFW_KEY_W:
                return Keys.W;
            case GLFW.GLFW_KEY_X:
                return Keys.X;
            case GLFW.GLFW_KEY_Y:
                return Keys.Y;
            case GLFW.GLFW_KEY_Z:
                return Keys.Z;
            case GLFW.GLFW_KEY_LEFT_BRACKET:
                return Keys.LEFT_BRACKET;
            case GLFW.GLFW_KEY_BACKSLASH:
                return Keys.BACKSLASH;
            case GLFW.GLFW_KEY_RIGHT_BRACKET:
                return Keys.RIGHT_BRACKET;
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                return Keys.GRAVE;
            case GLFW.GLFW_KEY_WORLD_1:
            case GLFW.GLFW_KEY_WORLD_2:
                return Keys.UNKNOWN;
            case GLFW.GLFW_KEY_ESCAPE:
                return Keys.ESCAPE;
            case GLFW.GLFW_KEY_ENTER:
                return Keys.ENTER;
            case GLFW.GLFW_KEY_TAB:
                return Keys.TAB;
            case GLFW.GLFW_KEY_BACKSPACE:
                return Keys.BACKSPACE;
            case GLFW.GLFW_KEY_INSERT:
                return Keys.INSERT;
            case GLFW.GLFW_KEY_DELETE:
                return Keys.FORWARD_DEL;
            case GLFW.GLFW_KEY_RIGHT:
                return Keys.RIGHT;
            case GLFW.GLFW_KEY_LEFT:
                return Keys.LEFT;
            case GLFW.GLFW_KEY_DOWN:
                return Keys.DOWN;
            case GLFW.GLFW_KEY_UP:
                return Keys.UP;
            case GLFW.GLFW_KEY_PAGE_UP:
                return Keys.PAGE_UP;
            case GLFW.GLFW_KEY_PAGE_DOWN:
                return Keys.PAGE_DOWN;
            case GLFW.GLFW_KEY_HOME:
                return Keys.HOME;
            case GLFW.GLFW_KEY_END:
                return Keys.END;
            case GLFW.GLFW_KEY_CAPS_LOCK:
                return Keys.CAPS_LOCK;
            case GLFW.GLFW_KEY_SCROLL_LOCK:
                return Keys.SCROLL_LOCK;
            case GLFW.GLFW_KEY_PRINT_SCREEN:
                return Keys.PRINT_SCREEN;
            case GLFW.GLFW_KEY_PAUSE:
                return Keys.PAUSE;
            case GLFW.GLFW_KEY_F1:
                return Keys.F1;
            case GLFW.GLFW_KEY_F2:
                return Keys.F2;
            case GLFW.GLFW_KEY_F3:
                return Keys.F3;
            case GLFW.GLFW_KEY_F4:
                return Keys.F4;
            case GLFW.GLFW_KEY_F5:
                return Keys.F5;
            case GLFW.GLFW_KEY_F6:
                return Keys.F6;
            case GLFW.GLFW_KEY_F7:
                return Keys.F7;
            case GLFW.GLFW_KEY_F8:
                return Keys.F8;
            case GLFW.GLFW_KEY_F9:
                return Keys.F9;
            case GLFW.GLFW_KEY_F10:
                return Keys.F10;
            case GLFW.GLFW_KEY_F11:
                return Keys.F11;
            case GLFW.GLFW_KEY_F12:
                return Keys.F12;
            case GLFW.GLFW_KEY_F13:
                return Keys.F13;
            case GLFW.GLFW_KEY_F14:
                return Keys.F14;
            case GLFW.GLFW_KEY_F15:
                return Keys.F15;
            case GLFW.GLFW_KEY_F16:
                return Keys.F16;
            case GLFW.GLFW_KEY_F17:
                return Keys.F17;
            case GLFW.GLFW_KEY_F18:
                return Keys.F18;
            case GLFW.GLFW_KEY_F19:
                return Keys.F19;
            case GLFW.GLFW_KEY_F20:
                return Keys.F20;
            case GLFW.GLFW_KEY_F21:
                return Keys.F21;
            case GLFW.GLFW_KEY_F22:
                return Keys.F22;
            case GLFW.GLFW_KEY_F23:
                return Keys.F23;
            case GLFW.GLFW_KEY_F24:
                return Keys.F24;
            case GLFW.GLFW_KEY_F25:
                return Keys.UNKNOWN;
            case GLFW.GLFW_KEY_NUM_LOCK:
                return Keys.NUM_LOCK;
            case GLFW.GLFW_KEY_KP_0:
                return Keys.NUMPAD_0;
            case GLFW.GLFW_KEY_KP_1:
                return Keys.NUMPAD_1;
            case GLFW.GLFW_KEY_KP_2:
                return Keys.NUMPAD_2;
            case GLFW.GLFW_KEY_KP_3:
                return Keys.NUMPAD_3;
            case GLFW.GLFW_KEY_KP_4:
                return Keys.NUMPAD_4;
            case GLFW.GLFW_KEY_KP_5:
                return Keys.NUMPAD_5;
            case GLFW.GLFW_KEY_KP_6:
                return Keys.NUMPAD_6;
            case GLFW.GLFW_KEY_KP_7:
                return Keys.NUMPAD_7;
            case GLFW.GLFW_KEY_KP_8:
                return Keys.NUMPAD_8;
            case GLFW.GLFW_KEY_KP_9:
                return Keys.NUMPAD_9;
            case GLFW.GLFW_KEY_KP_DECIMAL:
                return Keys.NUMPAD_DOT;
            case GLFW.GLFW_KEY_KP_DIVIDE:
                return Keys.NUMPAD_DIVIDE;
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                return Keys.NUMPAD_MULTIPLY;
            case GLFW.GLFW_KEY_KP_SUBTRACT:
                return Keys.NUMPAD_SUBTRACT;
            case GLFW.GLFW_KEY_KP_ADD:
                return Keys.NUMPAD_ADD;
            case GLFW.GLFW_KEY_KP_ENTER:
                return Keys.NUMPAD_ENTER;
            case GLFW.GLFW_KEY_KP_EQUAL:
                return Keys.NUMPAD_EQUALS;
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                return Keys.SHIFT_LEFT;
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                return Keys.CONTROL_LEFT;
            case GLFW.GLFW_KEY_LEFT_ALT:
                return Keys.ALT_LEFT;
            case GLFW.GLFW_KEY_LEFT_SUPER:
                return Keys.SYM;
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                return Keys.SHIFT_RIGHT;
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                return Keys.CONTROL_RIGHT;
            case GLFW.GLFW_KEY_RIGHT_ALT:
                return Keys.ALT_RIGHT;
            case GLFW.GLFW_KEY_RIGHT_SUPER:
                return Keys.SYM;
            case GLFW.GLFW_KEY_MENU:
                return Keys.MENU;
            default:
                return Keys.UNKNOWN;
        }
    }

    @Override
    public void dispose() {
        GLFW.setKeyCallback(window.getWindowHandle(), null);
        GLFW.setCharCallback(window.getWindowHandle(), null);
        GLFW.setScrollCallback(window.getWindowHandle(), null);
        GLFW.setCursorPosCallback(window.getWindowHandle(), null);
        GLFW.setMouseButtonCallback(window.getWindowHandle(), null);
    }

    // --------------------------------------------------------------------------
    // -------------------------- Nothing to see below this line except for stubs
    // --------------------------------------------------------------------------

    @Override
    public float getAccelerometerX() {
        return 0;
    }

    @Override
    public float getAccelerometerY() {
        return 0;
    }

    @Override
    public float getAccelerometerZ() {
        return 0;
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        return peripheral == Peripheral.HardwareKeyboard;
    }

    @Override
    public int getRotation() {
        return 0;
    }

    @Override
    public Orientation getNativeOrientation() {
        return Orientation.Landscape;
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean visible) {
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean visible, OnscreenKeyboardType type) {
    }

    @Override
    public void openTextInputField(NativeInputConfiguration configuration) {

    }

    @Override
    public void closeTextInputField(boolean sendReturn) {

    }

    @Override
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {

    }

    @Override
    public void vibrate(int milliseconds) {
    }

    @Override
    public void vibrate(int milliseconds, boolean fallback) {
    }

    @Override
    public void vibrate(int milliseconds, int amplitude, boolean fallback) {
    }

    @Override
    public void vibrate(VibrationType vibrationType) {
    }

    @Override
    public float getAzimuth() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public void getRotationMatrix(float[] matrix) {
    }

    @Override
    public float getGyroscopeX() {
        return 0;
    }

    @Override
    public float getGyroscopeY() {
        return 0;
    }

    @Override
    public float getGyroscopeZ() {
        return 0;
    }
}
