package com.github.xpenatan.gdx.teavm.backends.android;

import com.badlogic.gdx.InputProcessor;
import com.github.xpenatan.gdx.teavm.backends.shared.mock.TeaMockInput;

public class AndroidInput extends TeaMockInput {
    private static final int MAX_POINTERS = 20;
    private static final int MAX_KEYS = 256;
    private static final int TOUCH_DOWN = 0;
    private static final int TOUCH_UP = 1;
    private static final int TOUCH_DRAGGED = 2;
    private static final int TOUCH_CANCELLED = 3;
    private static final int KEY_DOWN = 0;
    private static final int KEY_UP = 1;

    private final int[] x = new int[MAX_POINTERS];
    private final int[] y = new int[MAX_POINTERS];
    private final int[] deltaX = new int[MAX_POINTERS];
    private final int[] deltaY = new int[MAX_POINTERS];
    private final float[] pressure = new float[MAX_POINTERS];
    private final boolean[] touched = new boolean[MAX_POINTERS];
    private final boolean[] keys = new boolean[MAX_KEYS];
    private final boolean[] justPressedKeys = new boolean[MAX_KEYS];
    private final boolean[] catchKeys = new boolean[MAX_KEYS];
    private boolean justTouched;
    private long currentEventTime;

    void onTouch(int type, int pointer, int screenX, int screenY, float touchPressure) {
        if(pointer < 0 || pointer >= MAX_POINTERS) {
            return;
        }
        currentEventTime = System.nanoTime();
        deltaX[pointer] = screenX - x[pointer];
        deltaY[pointer] = screenY - y[pointer];
        x[pointer] = screenX;
        y[pointer] = screenY;
        pressure[pointer] = touchPressure;
        InputProcessor processor = getInputProcessor();
        if(type == TOUCH_DOWN) {
            touched[pointer] = true;
            justTouched = true;
            if(processor != null) {
                processor.touchDown(screenX, screenY, pointer, Buttons.LEFT);
            }
        }
        else if(type == TOUCH_UP || type == TOUCH_CANCELLED) {
            touched[pointer] = false;
            pressure[pointer] = 0;
            if(processor != null) {
                processor.touchUp(screenX, screenY, pointer, Buttons.LEFT);
            }
        }
        else if(type == TOUCH_DRAGGED) {
            touched[pointer] = true;
            if(processor != null) {
                processor.touchDragged(screenX, screenY, pointer);
            }
        }
    }

    void onKey(int type, int keycode) {
        if(keycode < 0 || keycode >= MAX_KEYS) {
            return;
        }
        currentEventTime = System.nanoTime();
        InputProcessor processor = getInputProcessor();
        if(type == KEY_DOWN) {
            if(!keys[keycode]) {
                justPressedKeys[keycode] = true;
            }
            keys[keycode] = true;
            if(processor != null) {
                processor.keyDown(keycode);
            }
        }
        else if(type == KEY_UP) {
            keys[keycode] = false;
            if(processor != null) {
                processor.keyUp(keycode);
            }
        }
    }

    void endFrame() {
        justTouched = false;
        for(int i = 0; i < MAX_POINTERS; i++) {
            deltaX[i] = 0;
            deltaY[i] = 0;
        }
        for(int i = 0; i < MAX_KEYS; i++) {
            justPressedKeys[i] = false;
        }
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        return peripheral == Peripheral.MultitouchScreen || peripheral == Peripheral.OnscreenKeyboard;
    }

    @Override
    public int getMaxPointers() {
        return MAX_POINTERS;
    }

    @Override
    public int getX() {
        return getX(0);
    }

    @Override
    public int getX(int pointer) {
        return pointer >= 0 && pointer < MAX_POINTERS ? x[pointer] : 0;
    }

    @Override
    public int getDeltaX() {
        return getDeltaX(0);
    }

    @Override
    public int getDeltaX(int pointer) {
        return pointer >= 0 && pointer < MAX_POINTERS ? deltaX[pointer] : 0;
    }

    @Override
    public int getY() {
        return getY(0);
    }

    @Override
    public int getY(int pointer) {
        return pointer >= 0 && pointer < MAX_POINTERS ? y[pointer] : 0;
    }

    @Override
    public int getDeltaY() {
        return getDeltaY(0);
    }

    @Override
    public int getDeltaY(int pointer) {
        return pointer >= 0 && pointer < MAX_POINTERS ? deltaY[pointer] : 0;
    }

    @Override
    public boolean isTouched() {
        return isTouched(0);
    }

    @Override
    public boolean justTouched() {
        return justTouched;
    }

    @Override
    public boolean isTouched(int pointer) {
        return pointer >= 0 && pointer < MAX_POINTERS && touched[pointer];
    }

    @Override
    public float getPressure() {
        return getPressure(0);
    }

    @Override
    public float getPressure(int pointer) {
        return pointer >= 0 && pointer < MAX_POINTERS ? pressure[pointer] : 0;
    }

    @Override
    public boolean isButtonPressed(int button) {
        return button == Buttons.LEFT && isTouched();
    }

    @Override
    public boolean isButtonJustPressed(int button) {
        return button == Buttons.LEFT && justTouched;
    }

    @Override
    public boolean isKeyPressed(int key) {
        if(key == Keys.ANY_KEY) {
            for(boolean pressed : keys) {
                if(pressed) {
                    return true;
                }
            }
            return false;
        }
        return key >= 0 && key < MAX_KEYS && keys[key];
    }

    @Override
    public boolean isKeyJustPressed(int key) {
        if(key == Keys.ANY_KEY) {
            for(boolean pressed : justPressedKeys) {
                if(pressed) {
                    return true;
                }
            }
            return false;
        }
        return key >= 0 && key < MAX_KEYS && justPressedKeys[key];
    }

    @Override
    public long getCurrentEventTime() {
        return currentEventTime;
    }

    @Override
    public void setCatchKey(int keycode, boolean catchKey) {
        if(keycode >= 0 && keycode < MAX_KEYS) {
            catchKeys[keycode] = catchKey;
        }
    }

    @Override
    public boolean isCatchKey(int keycode) {
        return keycode >= 0 && keycode < MAX_KEYS && catchKeys[keycode];
    }
}
