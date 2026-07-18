package com.badlogic.gdx.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.controllers.ios.IosControllerApi;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.LongMap;

public final class IosControllerManager extends AbstractControllerManager {
    private final Array<ControllerListener> listeners = new Array<>();
    private final LongMap<IosController> controllerMap = new LongMap<>();
    private boolean polling;

    public IosControllerManager() {
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
                releaseControllers();
                clearListeners();
            }
        });
    }

    @Override
    public Array<Controller> getControllers() {
        pollState();
        return super.getControllers();
    }

    @Override
    public Controller getCurrentController() {
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

    void pollState() {
        if (polling) {
            return;
        }
        polling = true;
        try {
            LongArray disconnected = controllerMap.keys().toArray();
            int count = IosControllerApi.getControllerCount();
            for (int index = 0; index < count; index++) {
                long handle = IosControllerApi.getControllerHandle(index);
                if (handle == 0L) {
                    continue;
                }
                disconnected.removeValue(handle);

                IosController controller = controllerMap.get(handle);
                if (controller == null) {
                    controller = new IosController(this, handle);
                    controllerMap.put(handle, controller);
                    controllers.add(controller);
                    notifyConnected(controller);
                }
                controller.setConnected(true);
                controller.updateState();
            }

            for (int i = 0; i < disconnected.size; i++) {
                long handle = disconnected.get(i);
                IosController controller = controllerMap.remove(handle);
                if (controller == null) {
                    continue;
                }
                controllers.removeValue(controller, true);
                controller.setConnected(false);
                notifyDisconnected(controller);
                IosControllerApi.releaseController(handle);
            }
        }
        finally {
            polling = false;
        }
    }

    private void notifyConnected(IosController controller) {
        for (int i = 0, n = listeners.size; i < n; i++) {
            listeners.get(i).connected(controller);
        }
        Array<ControllerListener> controllerListeners = controller.getListeners();
        for (int i = 0, n = controllerListeners.size; i < n; i++) {
            controllerListeners.get(i).connected(controller);
        }
    }

    private void notifyDisconnected(IosController controller) {
        for (int i = 0, n = listeners.size; i < n; i++) {
            listeners.get(i).disconnected(controller);
        }
        Array<ControllerListener> controllerListeners = controller.getListeners();
        for (int i = 0, n = controllerListeners.size; i < n; i++) {
            controllerListeners.get(i).disconnected(controller);
        }
    }

    private void releaseControllers() {
        LongArray handles = controllerMap.keys().toArray();
        for (int i = 0; i < handles.size; i++) {
            IosControllerApi.releaseController(handles.get(i));
        }
        controllers.clear();
        controllerMap.clear();
    }
}
