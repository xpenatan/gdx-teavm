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

package emu.com.badlogic.gdx.controllers.teavm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.AbstractControllerManager;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import org.teavm.jso.JSBody;
import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Navigator;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.gamepad.Gamepad;
import org.teavm.jso.gamepad.GamepadButton;
import org.teavm.jso.gamepad.GamepadEvent;

public class TeavmControllers extends AbstractControllerManager implements GamepadSupportListener {

    private final IntMap<TeavmController> controllerMap = new IntMap<>();
    private final Array<ControllerListener> listeners = new Array<>();
    private final Array<TeavmControllerEvent> eventQueue = new Array<>();
    private final Pool<TeavmControllerEvent> eventPool = new Pool<>() {
        @Override
        protected TeavmControllerEvent newObject() {
            return new TeavmControllerEvent();
        }
    };
    private final IntMap<Gamepad> gamepads = new IntMap<>();
    private final IntMap<Gamepad> gamepadsTemp = new IntMap<>();
    private final Ticker ticker = new Ticker();

    private TeavmControllers() {
        nativeInit();
        listeners.add(new ManageCurrentControllerListener());
        setupEventQueue();
    }

    public void setupEventQueue() {
        new Runnable() {
            @Override
            public void run() {
                synchronized (eventQueue) {
                    for (TeavmControllerEvent event : eventQueue) {
                        switch (event.type) {
                            case TeavmControllerEvent.CONNECTED:
                                controllers.add(event.controller);
                                for (ControllerListener listener : listeners) {
                                    listener.connected(event.controller);
                                }
                                break;
                            case TeavmControllerEvent.DISCONNECTED:
                                controllers.removeValue(event.controller, true);
                                for (ControllerListener listener : listeners) {
                                    listener.disconnected(event.controller);
                                }
                                for (ControllerListener listener : event.controller.getListeners()) {
                                    listener.disconnected(event.controller);
                                }
                                break;
                            case TeavmControllerEvent.BUTTON_DOWN:
                                event.controller.buttonsPut(event.code, event.amount);
                                for (ControllerListener listener : listeners) {
                                    if (listener.buttonDown(event.controller, event.code)) break;
                                }
                                for (ControllerListener listener : event.controller.getListeners()) {
                                    if (listener.buttonDown(event.controller, event.code)) break;
                                }
                                break;
                            case TeavmControllerEvent.BUTTON_UP:
                                event.controller.buttonsRemove(event.code, event.amount);
                                for (ControllerListener listener : listeners) {
                                    if (listener.buttonUp(event.controller, event.code)) break;
                                }
                                for (ControllerListener listener : event.controller.getListeners()) {
                                    if (listener.buttonUp(event.controller, event.code)) break;
                                }
                                break;
                            case TeavmControllerEvent.AXIS:
                                event.controller.axes[event.code] = event.amount;
                                for (ControllerListener listener : listeners) {
                                    if (listener.axisMoved(event.controller, event.code, event.amount)) break;
                                }
                                for (ControllerListener listener : event.controller.getListeners()) {
                                    if (listener.axisMoved(event.controller, event.code, event.amount)) break;
                                }
                                break;
                            default:
                        }
                    }
                    eventPool.freeAll(eventQueue);
                    eventQueue.clear();
                }
                Gdx.app.postRunnable(this);
            }
        }.run();
    }

    @Override
    public void addListener(ControllerListener listener) {
        synchronized (eventQueue) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(ControllerListener listener) {
        synchronized (eventQueue) {
            listeners.removeValue(listener, true);
        }
    }

    @Override
    public void onGamepadConnected(int index) {
        Gamepad gamepad = getGamepad(index);
        TeavmController controller = new TeavmController(gamepad.getIndex(), gamepad.getId());
        controllerMap.put(index, controller);
        synchronized (eventQueue) {
            TeavmControllerEvent event = eventPool.obtain();
            event.type = TeavmControllerEvent.CONNECTED;
            event.controller = controller;
            eventQueue.add(event);
        }
    }

    @Override
    public void onGamepadDisconnected(int index) {
        TeavmController controller = controllerMap.remove(index);
        if (controller != null) {
            synchronized (eventQueue) {
                controller.connected = false;
                TeavmControllerEvent event = eventPool.obtain();
                event.type = TeavmControllerEvent.DISCONNECTED;
                event.controller = controller;
                eventQueue.add(event);
            }
        }
    }

    @Override
    public void onGamepadUpdated(int index) {
        Gamepad gamepad = getGamepad(index);
        TeavmController controller = controllerMap.get(index);
        if (gamepad != null && controller != null) {
            // Determine what changed
            double[] axes = gamepad.getAxes();
            GamepadButton[] buttons = gamepad.getButtons();
            synchronized (eventQueue) {
                for (int i = 0, j = axes.length; i < j; i++) {
                    float oldAxis = controller.getAxis(i);
                    float newAxis = (float) axes[i];
                    if (oldAxis != newAxis) {
                        TeavmControllerEvent event = eventPool.obtain();
                        event.type = TeavmControllerEvent.AXIS;
                        event.controller = controller;
                        event.code = i;
                        event.amount = newAxis;
                        eventQueue.add(event);
                    }
                }
                for (int i = 0, j = buttons.length; i < j; i++) {
                    float newButton = (float) buttons[i].getValue();
                    float oldButton = controller.getButtonValue(i);
                    if (oldButton != newButton) {
                        if ((oldButton < 0.5f && newButton < 0.5f) || (oldButton >= 0.5f && newButton >= 0.5f)) {
                            controller.buttonsPut(i, newButton);
                            continue;
                        }
                        TeavmControllerEvent event = eventPool.obtain();
                        event.type = newButton >= 0.5f ? TeavmControllerEvent.BUTTON_DOWN : TeavmControllerEvent.BUTTON_UP;
                        event.controller = controller;
                        event.code = i;
                        event.amount = newButton;
                        eventQueue.add(event);
                    }
                }
            }
        }
    }

    @Override
    public void clearListeners() {
        listeners.clear();
        listeners.add(new ManageCurrentControllerListener());
    }

    @Override
    public Array<ControllerListener> getListeners() {
        return listeners;
    }

    public void nativeInit() {
        if (cannotUseGamepadsWithJS()) return;
        Window window = Window.current();
        window.addEventListener("gamepadconnected", (EventListener<GamepadEvent>) e -> {
            handleGamepadConnect(e.getGamepad());
        });
        window.addEventListener("gamepaddisconnected", (EventListener<GamepadEvent>) e -> {
            handleGamepadDisconnect(e.getGamepad());
        });
        Gamepad[] initialGamepads = Navigator.getGamepads();
        for (Gamepad initialGamepad : initialGamepads) {
            if (initialGamepad != null) {
                handleGamepadConnect(initialGamepad);
            }
        }
        startPolling();
    }

    public void startPolling() {
        ticker.start();
    }

    public void stopPolling() {
        ticker.stop();
    }

    private void handleGamepadDisconnect(Gamepad gamepad) {
        gamepads.remove(gamepad.getIndex());
        onGamepadDisconnected(gamepad.getIndex());
    }

    public void handleGamepadConnect(Gamepad gamepad) {
        gamepads.put(gamepad.getIndex(), gamepad);
        onGamepadConnected(gamepad.getIndex());
    }

    public Gamepad getGamepad(int index) {
        return gamepads.get(index);
    }

    private class Ticker implements AnimationFrameCallback {
        private boolean ticking = false;

        @Override
        public void onAnimationFrame(double timestamp) {
            if (ticking) {
                pollGamepads();
                pollGamepadsStatus();
                Window.requestAnimationFrame(this);
            }

        }

        public void start() {
            if (!ticking) {
                ticking = true;
                Window.requestAnimationFrame(this);
            }
        }

        public void stop() {
            ticking = false;
        }

    }

    public void pollGamepads() {
        Gamepad[] currentGamepads = Navigator.getGamepads();
        if (currentGamepads != null) {
            gamepadsTemp.clear();
            gamepadsTemp.putAll(gamepads);
            for (Gamepad gamepad : currentGamepads) {
                if (gamepad != null) {
                    if (!gamepadsTemp.containsKey(gamepad.getIndex())) {
                        handleGamepadConnect(gamepad);
                    } else {
                        gamepads.put(gamepad.getIndex(), gamepad);
                    }
                    gamepadsTemp.remove(gamepad.getIndex());
                }
            }
            for (Gamepad gamepad : gamepadsTemp.values()) {
                handleGamepadDisconnect(gamepad);
            }
        }
    }

    public void pollGamepadsStatus() {
        for (Gamepad gamepad : gamepads.values()) {
            if (previousTimestampWithJS(gamepad) != gamepad.getTimestamp()) {
                onGamepadUpdated(gamepad.getIndex());
            }
            setPreviousTimestampWithJS(gamepad, gamepad.getTimestamp());
        }
    }

    public Controller getCurrent() {
        return getCurrentController();
    }

    private static final class InstanceHolder {
        static final TeavmControllers instance = new TeavmControllers();
    }

    public static TeavmControllers getInstance() {
        return InstanceHolder.instance;
    }

    @JSBody(params = "gamepad", script = "return gamepad.previousTimestamp;")
    private static native double previousTimestampWithJS(Gamepad gamepad);

    @JSBody(params = {"gamepad", "time"}, script = "gamepad.previousTimestamp = time;")
    private static native void setPreviousTimestampWithJS(Gamepad gamepad, double time);

    @JSBody(script = "return !!! navigator.getGamepads")
    private static native boolean cannotUseGamepadsWithJS();

}
