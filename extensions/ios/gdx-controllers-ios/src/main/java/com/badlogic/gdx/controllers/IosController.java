package com.badlogic.gdx.controllers;

import com.badlogic.gdx.controllers.ios.IosControllerApi;
import com.badlogic.gdx.utils.Array;

final class IosController implements Controller {
    static final int BUTTON_A = 0;
    static final int BUTTON_B = 1;
    static final int BUTTON_X = 2;
    static final int BUTTON_Y = 3;
    static final int BUTTON_L1 = 4;
    static final int BUTTON_R1 = 5;
    static final int BUTTON_BACK = 6;
    static final int BUTTON_START = 7;
    static final int BUTTON_L2 = 8;
    static final int BUTTON_R2 = 9;
    static final int BUTTON_LEFT_STICK = 10;
    static final int BUTTON_RIGHT_STICK = 11;
    static final int BUTTON_DPAD_UP = 12;
    static final int BUTTON_DPAD_DOWN = 13;
    static final int BUTTON_DPAD_LEFT = 14;
    static final int BUTTON_DPAD_RIGHT = 15;
    static final int BUTTON_COUNT = 16;

    static final int AXIS_LEFT_X = 0;
    static final int AXIS_LEFT_Y = 1;
    static final int AXIS_RIGHT_X = 2;
    static final int AXIS_RIGHT_Y = 3;
    static final int AXIS_LEFT_TRIGGER = 4;
    static final int AXIS_RIGHT_TRIGGER = 5;
    static final int AXIS_COUNT = 6;

    private static final ControllerMapping DEFAULT_MAPPING = new ControllerMapping(
        AXIS_LEFT_X,
        AXIS_LEFT_Y,
        AXIS_RIGHT_X,
        AXIS_RIGHT_Y,
        BUTTON_A,
        BUTTON_B,
        BUTTON_X,
        BUTTON_Y,
        BUTTON_BACK,
        BUTTON_START,
        BUTTON_L1,
        BUTTON_L2,
        BUTTON_R1,
        BUTTON_R2,
        BUTTON_LEFT_STICK,
        BUTTON_RIGHT_STICK,
        BUTTON_DPAD_UP,
        BUTTON_DPAD_DOWN,
        BUTTON_DPAD_LEFT,
        BUTTON_DPAD_RIGHT
    ) {};

    private final IosControllerManager manager;
    private final long handle;
    private final Array<ControllerListener> listeners = new Array<>();
    private final boolean[] buttons = new boolean[BUTTON_COUNT];
    private final float[] axes = new float[AXIS_COUNT];
    private boolean connected = true;

    IosController(IosControllerManager manager, long handle) {
        this.manager = manager;
        this.handle = handle;
    }

    long getHandle() {
        return handle;
    }

    void setConnected(boolean connected) {
        this.connected = connected;
    }

    void updateState() {
        for (int buttonCode = 0; buttonCode < buttons.length; buttonCode++) {
            updateButton(buttonCode, IosControllerApi.getButton(handle, buttonCode) != 0);
        }
        for (int axisCode = 0; axisCode < axes.length; axisCode++) {
            updateAxis(axisCode, IosControllerApi.getAxis(handle, axisCode));
        }
    }

    private void updateButton(int buttonCode, boolean value) {
        if (buttons[buttonCode] == value) {
            return;
        }
        buttons[buttonCode] = value;
        notifyButton(buttonCode, value);
    }

    private void updateAxis(int axisCode, float value) {
        if (Float.compare(axes[axisCode], value) == 0) {
            return;
        }
        axes[axisCode] = value;
        notifyAxis(axisCode, value);
    }

    private void notifyButton(int buttonCode, boolean pressed) {
        Array<ControllerListener> managerListeners = manager.getListeners();
        for (int i = 0, n = managerListeners.size; i < n; i++) {
            boolean handled = pressed
                ? managerListeners.get(i).buttonDown(this, buttonCode)
                : managerListeners.get(i).buttonUp(this, buttonCode);
            if (handled) {
                return;
            }
        }
        for (int i = 0, n = listeners.size; i < n; i++) {
            boolean handled = pressed
                ? listeners.get(i).buttonDown(this, buttonCode)
                : listeners.get(i).buttonUp(this, buttonCode);
            if (handled) {
                return;
            }
        }
    }

    private void notifyAxis(int axisCode, float value) {
        Array<ControllerListener> managerListeners = manager.getListeners();
        for (int i = 0, n = managerListeners.size; i < n; i++) {
            if (managerListeners.get(i).axisMoved(this, axisCode, value)) {
                return;
            }
        }
        for (int i = 0, n = listeners.size; i < n; i++) {
            if (listeners.get(i).axisMoved(this, axisCode, value)) {
                return;
            }
        }
    }

    @Override
    public boolean getButton(int buttonCode) {
        manager.pollState();
        return buttonCode >= 0 && buttonCode < buttons.length && buttons[buttonCode];
    }

    @Override
    public float getAxis(int axisCode) {
        manager.pollState();
        return axisCode >= 0 && axisCode < axes.length ? axes[axisCode] : 0f;
    }

    @Override
    public String getName() {
        return "iOS Game Controller";
    }

    @Override
    public String getUniqueId() {
        return "ios-" + handle;
    }

    @Override
    public int getMinButtonIndex() {
        return 0;
    }

    @Override
    public int getMaxButtonIndex() {
        return BUTTON_COUNT - 1;
    }

    @Override
    public int getAxisCount() {
        return AXIS_COUNT;
    }

    @Override
    public boolean isConnected() {
        return connected && IosControllerApi.isConnected(handle) != 0;
    }

    @Override
    public boolean canVibrate() {
        return false;
    }

    @Override
    public boolean isVibrating() {
        return false;
    }

    @Override
    public void startVibration(int duration, float strength) {
    }

    @Override
    public void cancelVibration() {
    }

    @Override
    public boolean supportsPlayerIndex() {
        return false;
    }

    @Override
    public int getPlayerIndex() {
        return PLAYER_IDX_UNSET;
    }

    @Override
    public void setPlayerIndex(int index) {
    }

    @Override
    public ControllerMapping getMapping() {
        return DEFAULT_MAPPING;
    }

    @Override
    public ControllerPowerLevel getPowerLevel() {
        return ControllerPowerLevel.POWER_UNKNOWN;
    }

    @Override
    public void addListener(ControllerListener listener) {
        if (!listeners.contains(listener, true)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ControllerListener listener) {
        listeners.removeValue(listener, true);
    }

    Array<ControllerListener> getListeners() {
        return listeners;
    }
}
