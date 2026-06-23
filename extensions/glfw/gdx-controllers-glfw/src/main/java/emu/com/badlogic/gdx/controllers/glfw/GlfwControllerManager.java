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

package emu.com.badlogic.gdx.controllers.glfw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.controllers.AbstractControllerManager;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import org.teavm.interop.Address;
import org.teavm.interop.Structure;

public final class GlfwControllerManager extends AbstractControllerManager {

    private static final int FIRST_JOYSTICK = 0;
    private static final int LAST_JOYSTICK = 15;

    private final Array<ControllerListener> listeners = new Array<>();
    private final IntMap<GlfwController> controllerMap = new IntMap<>();
    private final byte[] gamepadStateBuffer = new byte[Structure.sizeOf(GlfwControllerApi.GlfwGamepadState.class)];

    GlfwControllerManager() {
        listeners.add(new ManageCurrentControllerListener());
        Gdx.app.addLifecycleListener(new LifecycleListener() {
            @Override
            public void resume() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void dispose() {
                clearListeners();
                controllers.clear();
                controllerMap.clear();
            }
        });
    }

    @Override
    public Array<com.badlogic.gdx.controllers.Controller> getControllers() {
        pollState();
        return super.getControllers();
    }

    @Override
    public com.badlogic.gdx.controllers.Controller getCurrentController() {
        pollState();
        return super.getCurrentController();
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

    @Override
    public Array<ControllerListener> getListeners() {
        return listeners;
    }

    @Override
    public void clearListeners() {
        listeners.clear();
        listeners.add(new ManageCurrentControllerListener());
    }

    private void pollState() {
        IntArray disconnected = controllerMap.keys().toArray();
        GlfwControllerApi.GlfwGamepadState state = Address.ofData(gamepadStateBuffer).toStructure();
        Address.pin(gamepadStateBuffer);

        for (int joystickId = FIRST_JOYSTICK; joystickId <= LAST_JOYSTICK; joystickId++) {
            if (!isGamepadAvailable(joystickId)) {
                continue;
            }

            disconnected.removeValue(joystickId);

            GlfwController controller = controllerMap.get(joystickId);
            if (controller == null) {
                controller = new GlfwController(this, joystickId);
                controllerMap.put(joystickId, controller);
                controllers.add(controller);
                controller.setConnected(true);
                notifyConnected(controller);
            }

            if (GlfwControllerApi.glfwGetGamepadState(joystickId, state) == 0) {
                continue;
            }

            controller.updateName();
            controller.setConnected(true);
            controller.updateState(state);
        }

        for (int i = 0; i < disconnected.size; i++) {
            int joystickId = disconnected.get(i);
            GlfwController controller = controllerMap.remove(joystickId);
            if (controller == null) {
                continue;
            }

            controllers.removeValue(controller, true);
            controller.setConnected(false);
            notifyDisconnected(controller);
        }
    }

    private boolean isGamepadAvailable(int joystickId) {
        return GlfwControllerApi.glfwJoystickPresent(joystickId) != 0
            && GlfwControllerApi.glfwJoystickIsGamepad(joystickId) != 0;
    }

    private void notifyConnected(GlfwController controller) {
        for (ControllerListener listener : listeners) {
            listener.connected(controller);
        }
        for (ControllerListener listener : controller.getListeners()) {
            listener.connected(controller);
        }
    }

    private void notifyDisconnected(GlfwController controller) {
        for (ControllerListener listener : listeners) {
            listener.disconnected(controller);
        }
        for (ControllerListener listener : controller.getListeners()) {
            listener.disconnected(controller);
        }
    }
}
