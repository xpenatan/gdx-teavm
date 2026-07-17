package com.badlogic.gdx.controllers.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.controllers.AbstractControllerManager;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidControllerSupport;

public class AndroidControllerManager extends AbstractControllerManager {

    private final Array<ControllerListener> listeners = new Array<>();
    private final IntMap<AndroidController> controllerMap = new IntMap<>();
    private long lastVersion = Long.MIN_VALUE;

    public AndroidControllerManager() {
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
                lastVersion = Long.MIN_VALUE;
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
        if(!listeners.contains(listener, true)) {
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
        long version = AndroidControllerSupport.getVersion();
        if(version == lastVersion) {
            return;
        }
        lastVersion = version;

        IntArray disconnected = controllerMap.keys().toArray();
        Array<AndroidControllerSupport.ControllerSnapshot> snapshots = AndroidControllerSupport.snapshot();
        for(int i = 0; i < snapshots.size; i++) {
            AndroidControllerSupport.ControllerSnapshot snapshot = snapshots.get(i);
            disconnected.removeValue(snapshot.deviceId);

            AndroidController controller = controllerMap.get(snapshot.deviceId);
            if(controller == null) {
                controller = new AndroidController(this, snapshot.deviceId);
                controllerMap.put(snapshot.deviceId, controller);
                controllers.add(controller);
                controller.setConnected(true);
                notifyConnected(controller);
            }

            controller.setConnected(true);
            controller.updateState(snapshot);
        }

        for(int i = 0; i < disconnected.size; i++) {
            int deviceId = disconnected.get(i);
            AndroidController controller = controllerMap.remove(deviceId);
            if(controller == null) {
                continue;
            }

            controllers.removeValue(controller, true);
            controller.setConnected(false);
            notifyDisconnected(controller);
        }
    }

    private void notifyConnected(AndroidController controller) {
        for(int i = 0, n = listeners.size; i < n; i++) {
            listeners.get(i).connected(controller);
        }
        Array<ControllerListener> controllerListeners = controller.getListeners();
        for(int i = 0, n = controllerListeners.size; i < n; i++) {
            controllerListeners.get(i).connected(controller);
        }
    }

    private void notifyDisconnected(AndroidController controller) {
        for(int i = 0, n = listeners.size; i < n; i++) {
            listeners.get(i).disconnected(controller);
        }
        Array<ControllerListener> controllerListeners = controller.getListeners();
        for(int i = 0, n = controllerListeners.size; i < n; i++) {
            controllerListeners.get(i).disconnected(controller);
        }
    }
}
