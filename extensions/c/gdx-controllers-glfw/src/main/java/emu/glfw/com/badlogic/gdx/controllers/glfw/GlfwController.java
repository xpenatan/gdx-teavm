/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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

package emu.glfw.com.badlogic.gdx.controllers.glfw;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.ControllerPowerLevel;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.teavm.backends.glfw.utils.GLFW;

final class GlfwController implements Controller {

    private static final int BUTTON_COUNT = GLFW.GLFW_GAMEPAD_BUTTON_LAST + 1;
    private static final int AXIS_COUNT = GLFW.GLFW_GAMEPAD_AXIS_LAST + 1;

    private static final ControllerMapping DEFAULT_MAPPING = new ControllerMapping(
        GLFW.GLFW_GAMEPAD_AXIS_LEFT_X,
        GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y,
        GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X,
        GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y,
        GLFW.GLFW_GAMEPAD_BUTTON_A,
        GLFW.GLFW_GAMEPAD_BUTTON_B,
        GLFW.GLFW_GAMEPAD_BUTTON_X,
        GLFW.GLFW_GAMEPAD_BUTTON_Y,
        GLFW.GLFW_GAMEPAD_BUTTON_BACK,
        GLFW.GLFW_GAMEPAD_BUTTON_START,
        GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER,
        ControllerMapping.UNDEFINED,
        GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER,
        ControllerMapping.UNDEFINED,
        GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB,
        GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB,
        GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP,
        GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN,
        GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT,
        GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT
    ) {};

    private final Array<ControllerListener> listeners = new Array<>();
    private final GlfwControllerManager manager;
    private final int joystickId;
    private final byte[] buttons = new byte[BUTTON_COUNT];
    private final float[] axes = new float[AXIS_COUNT];
    private String name = "";
    private boolean connected = true;

    GlfwController(GlfwControllerManager manager, int joystickId) {
        this.manager = manager;
        this.joystickId = joystickId;
        updateName();
    }

    void updateName() {
        String controllerName = GlfwControllerApi.glfwGetGamepadName(joystickId);
        if (controllerName == null || controllerName.isBlank()) {
            controllerName = GlfwControllerApi.glfwGetJoystickName(joystickId);
        }
        name = controllerName == null ? "" : controllerName;
    }

    void setConnected(boolean connected) {
        this.connected = connected;
    }

    void updateState(GlfwControllerApi.GlfwGamepadState state) {
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_A, state.buttonA);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_B, state.buttonB);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_X, state.buttonX);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_Y, state.buttonY);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, state.buttonLeftBumper);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, state.buttonRightBumper);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_BACK, state.buttonBack);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_START, state.buttonStart);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_GUIDE, state.buttonGuide);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB, state.buttonLeftThumb);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, state.buttonRightThumb);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, state.buttonDpadUp);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, state.buttonDpadRight);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, state.buttonDpadDown);
        updateButton(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, state.buttonDpadLeft);

        updateAxis(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, state.axisLeftX);
        updateAxis(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, state.axisLeftY);
        updateAxis(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X, state.axisRightX);
        updateAxis(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y, state.axisRightY);
        updateAxis(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, state.axisLeftTrigger);
        updateAxis(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, state.axisRightTrigger);
    }

    private void updateButton(int buttonCode, byte value) {
        if (buttonCode < 0 || buttonCode >= buttons.length || buttons[buttonCode] == value) {
            return;
        }

        buttons[buttonCode] = value;
        if (value == GLFW.GLFW_PRESS) {
            notifyButtonDown(buttonCode);
        }
        else {
            notifyButtonUp(buttonCode);
        }
    }

    private void updateAxis(int axisCode, float value) {
        if (axisCode < 0 || axisCode >= axes.length || Float.compare(axes[axisCode], value) == 0) {
            return;
        }

        axes[axisCode] = value;
        notifyAxisMoved(axisCode, value);
    }

    private void notifyButtonDown(int buttonCode) {
        for (ControllerListener listener : manager.getListeners()) {
            if (listener.buttonDown(this, buttonCode)) {
                return;
            }
        }
        for (ControllerListener listener : listeners) {
            if (listener.buttonDown(this, buttonCode)) {
                break;
            }
        }
    }

    private void notifyButtonUp(int buttonCode) {
        for (ControllerListener listener : manager.getListeners()) {
            if (listener.buttonUp(this, buttonCode)) {
                return;
            }
        }
        for (ControllerListener listener : listeners) {
            if (listener.buttonUp(this, buttonCode)) {
                break;
            }
        }
    }

    private void notifyAxisMoved(int axisCode, float value) {
        for (ControllerListener listener : manager.getListeners()) {
            if (listener.axisMoved(this, axisCode, value)) {
                return;
            }
        }
        for (ControllerListener listener : listeners) {
            if (listener.axisMoved(this, axisCode, value)) {
                break;
            }
        }
    }

    @Override
    public boolean getButton(int buttonCode) {
        return buttonCode >= 0 && buttonCode < buttons.length && buttons[buttonCode] == GLFW.GLFW_PRESS;
    }

    @Override
    public float getAxis(int axisIndex) {
        if (axisIndex < 0 || axisIndex >= axes.length) {
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
        return "glfw-" + joystickId;
    }

    @Override
    public int getMinButtonIndex() {
        return 0;
    }

    @Override
    public int getMaxButtonIndex() {
        return buttons.length - 1;
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
