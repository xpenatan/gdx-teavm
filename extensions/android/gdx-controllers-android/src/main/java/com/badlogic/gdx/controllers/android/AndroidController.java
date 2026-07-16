package com.badlogic.gdx.controllers.android;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.ControllerPowerLevel;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidControllerSupport;

final class AndroidController implements Controller {

    private static final float BUTTON_PRESS_THRESHOLD = 0.5f;

    private final Array<ControllerListener> listeners = new Array<>();
    private final AndroidControllerManager manager;
    private final int deviceId;
    private final float[] buttonValues = new float[AndroidControllerSupport.BUTTON_COUNT];
    private final float[] axes = new float[AndroidControllerSupport.AXIS_COUNT];
    private final String name;
    private boolean connected = true;

    AndroidController(AndroidControllerManager manager, int deviceId) {
        this.manager = manager;
        this.deviceId = deviceId;
        this.name = AndroidControllerSupport.getDefaultName(deviceId);
    }

    void setConnected(boolean connected) {
        this.connected = connected;
    }

    void updateState(AndroidControllerSupport.ControllerSnapshot snapshot) {
        for(int i = 0; i < buttonValues.length; i++) {
            updateButton(i, snapshot.buttons[i]);
        }
        for(int i = 0; i < axes.length; i++) {
            updateAxis(i, snapshot.axes[i]);
        }

        syncVirtualButton(
                AndroidControllerSupport.BUTTON_L2,
                snapshot.buttons[AndroidControllerSupport.BUTTON_L2],
                axes[AndroidControllerSupport.AXIS_LEFT_TRIGGER] >= BUTTON_PRESS_THRESHOLD
        );
        syncVirtualButton(
                AndroidControllerSupport.BUTTON_R2,
                snapshot.buttons[AndroidControllerSupport.BUTTON_R2],
                axes[AndroidControllerSupport.AXIS_RIGHT_TRIGGER] >= BUTTON_PRESS_THRESHOLD
        );
        syncVirtualButton(
                AndroidControllerSupport.BUTTON_DPAD_LEFT,
                snapshot.buttons[AndroidControllerSupport.BUTTON_DPAD_LEFT],
                axes[AndroidControllerSupport.AXIS_HAT_X] <= -BUTTON_PRESS_THRESHOLD
        );
        syncVirtualButton(
                AndroidControllerSupport.BUTTON_DPAD_RIGHT,
                snapshot.buttons[AndroidControllerSupport.BUTTON_DPAD_RIGHT],
                axes[AndroidControllerSupport.AXIS_HAT_X] >= BUTTON_PRESS_THRESHOLD
        );
        syncVirtualButton(
                AndroidControllerSupport.BUTTON_DPAD_UP,
                snapshot.buttons[AndroidControllerSupport.BUTTON_DPAD_UP],
                axes[AndroidControllerSupport.AXIS_HAT_Y] <= -BUTTON_PRESS_THRESHOLD
        );
        syncVirtualButton(
                AndroidControllerSupport.BUTTON_DPAD_DOWN,
                snapshot.buttons[AndroidControllerSupport.BUTTON_DPAD_DOWN],
                axes[AndroidControllerSupport.AXIS_HAT_Y] >= BUTTON_PRESS_THRESHOLD
        );
    }

    private void syncVirtualButton(int buttonCode, float directValue, boolean derivedPressed) {
        float value = Math.max(directValue, derivedPressed ? 1f : 0f);
        updateButton(buttonCode, value);
    }

    private void updateButton(int buttonCode, float value) {
        if(buttonCode < 0 || buttonCode >= buttonValues.length) {
            return;
        }

        float oldValue = buttonValues[buttonCode];
        if(Float.compare(oldValue, value) == 0) {
            return;
        }

        boolean wasPressed = oldValue >= BUTTON_PRESS_THRESHOLD;
        boolean pressed = value >= BUTTON_PRESS_THRESHOLD;
        buttonValues[buttonCode] = value;

        if(!wasPressed && pressed) {
            notifyButtonDown(buttonCode);
        }
        else if(wasPressed && !pressed) {
            notifyButtonUp(buttonCode);
        }
    }

    private void updateAxis(int axisCode, float value) {
        if(axisCode < 0 || axisCode >= axes.length || Float.compare(axes[axisCode], value) == 0) {
            return;
        }

        axes[axisCode] = value;
        notifyAxisMoved(axisCode, value);
    }

    private void notifyButtonDown(int buttonCode) {
        Array<ControllerListener> managerListeners = manager.getListeners();
        for(int i = 0, n = managerListeners.size; i < n; i++) {
            if(managerListeners.get(i).buttonDown(this, buttonCode)) {
                return;
            }
        }
        for(int i = 0, n = listeners.size; i < n; i++) {
            if(listeners.get(i).buttonDown(this, buttonCode)) {
                break;
            }
        }
    }

    private void notifyButtonUp(int buttonCode) {
        Array<ControllerListener> managerListeners = manager.getListeners();
        for(int i = 0, n = managerListeners.size; i < n; i++) {
            if(managerListeners.get(i).buttonUp(this, buttonCode)) {
                return;
            }
        }
        for(int i = 0, n = listeners.size; i < n; i++) {
            if(listeners.get(i).buttonUp(this, buttonCode)) {
                break;
            }
        }
    }

    private void notifyAxisMoved(int axisCode, float value) {
        Array<ControllerListener> managerListeners = manager.getListeners();
        for(int i = 0, n = managerListeners.size; i < n; i++) {
            if(managerListeners.get(i).axisMoved(this, axisCode, value)) {
                return;
            }
        }
        for(int i = 0, n = listeners.size; i < n; i++) {
            if(listeners.get(i).axisMoved(this, axisCode, value)) {
                break;
            }
        }
    }

    @Override
    public boolean getButton(int buttonCode) {
        return buttonCode >= 0
                && buttonCode < buttonValues.length
                && buttonValues[buttonCode] >= BUTTON_PRESS_THRESHOLD;
    }

    @Override
    public float getAxis(int axisIndex) {
        if(axisIndex < 0 || axisIndex >= axes.length) {
            return 0f;
        }
        return axes[axisIndex];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUniqueId() {
        return "android-" + deviceId;
    }

    @Override
    public int getMinButtonIndex() {
        return 0;
    }

    @Override
    public int getMaxButtonIndex() {
        return buttonValues.length - 1;
    }

    @Override
    public int getAxisCount() {
        return axes.length;
    }

    @Override
    public boolean isConnected() {
        return connected;
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
        return Controller.PLAYER_IDX_UNSET;
    }

    @Override
    public void setPlayerIndex(int index) {
    }

    @Override
    public ControllerMapping getMapping() {
        return AndroidControllerMapping.getInstance();
    }

    @Override
    public ControllerPowerLevel getPowerLevel() {
        return ControllerPowerLevel.POWER_UNKNOWN;
    }

    @Override
    public void addListener(ControllerListener listener) {
        if(!listeners.contains(listener, true)) {
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
