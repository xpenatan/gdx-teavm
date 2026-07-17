package com.github.xpenatan.gdx.teavm.backends.android;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public final class AndroidControllerSupport {
    public static final int AXIS_LEFT_X = 0;
    public static final int AXIS_LEFT_Y = 1;
    public static final int AXIS_RIGHT_X = 2;
    public static final int AXIS_RIGHT_Y = 3;
    public static final int AXIS_LEFT_TRIGGER = 4;
    public static final int AXIS_RIGHT_TRIGGER = 5;
    public static final int AXIS_HAT_X = 6;
    public static final int AXIS_HAT_Y = 7;

    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_X = 2;
    public static final int BUTTON_Y = 3;
    public static final int BUTTON_L1 = 4;
    public static final int BUTTON_R1 = 5;
    public static final int BUTTON_L2 = 6;
    public static final int BUTTON_R2 = 7;
    public static final int BUTTON_BACK = 8;
    public static final int BUTTON_START = 9;
    public static final int BUTTON_LEFT_STICK = 10;
    public static final int BUTTON_RIGHT_STICK = 11;
    public static final int BUTTON_DPAD_UP = 12;
    public static final int BUTTON_DPAD_DOWN = 13;
    public static final int BUTTON_DPAD_LEFT = 14;
    public static final int BUTTON_DPAD_RIGHT = 15;

    public static final int AXIS_COUNT = 8;
    public static final int BUTTON_COUNT = 16;

    private static final IntMap<ControllerState> controllers = new IntMap<>();
    private static long version;

    private AndroidControllerSupport() {
    }

    public static synchronized void connect(int deviceId) {
        if(!controllers.containsKey(deviceId)) {
            controllers.put(deviceId, new ControllerState(deviceId));
            version++;
        }
    }

    public static synchronized void disconnect(int deviceId) {
        if(controllers.remove(deviceId) != null) {
            version++;
        }
    }

    public static synchronized void buttonChanged(int deviceId, int buttonCode, float value) {
        if(buttonCode < 0 || buttonCode >= BUTTON_COUNT) {
            return;
        }

        ControllerState state = controllers.get(deviceId);
        boolean created = false;
        if(state == null) {
            state = new ControllerState(deviceId);
            controllers.put(deviceId, state);
            created = true;
        }

        if(!created && Float.compare(state.buttons[buttonCode], value) == 0) {
            return;
        }

        state.buttons[buttonCode] = value;
        version++;
    }

    public static synchronized void axisChanged(int deviceId, int axisCode, float value) {
        if(axisCode < 0 || axisCode >= AXIS_COUNT) {
            return;
        }

        ControllerState state = controllers.get(deviceId);
        boolean created = false;
        if(state == null) {
            state = new ControllerState(deviceId);
            controllers.put(deviceId, state);
            created = true;
        }

        if(!created && Float.compare(state.axes[axisCode], value) == 0) {
            return;
        }

        state.axes[axisCode] = value;
        version++;
    }

    public static synchronized long getVersion() {
        return version;
    }

    public static synchronized Array<ControllerSnapshot> snapshot() {
        Array<ControllerSnapshot> snapshots = new Array<>(controllers.size);
        for(ControllerState state : controllers.values()) {
            snapshots.add(new ControllerSnapshot(state.deviceId, state.axes.clone(), state.buttons.clone()));
        }
        return snapshots;
    }

    public static String getDefaultName(int deviceId) {
        return "Android Controller " + deviceId;
    }

    private static final class ControllerState {
        final int deviceId;
        final float[] axes = new float[AXIS_COUNT];
        final float[] buttons = new float[BUTTON_COUNT];

        ControllerState(int deviceId) {
            this.deviceId = deviceId;
        }
    }

    public static final class ControllerSnapshot {
        public final int deviceId;
        public final float[] axes;
        public final float[] buttons;

        private ControllerSnapshot(int deviceId, float[] axes, float[] buttons) {
            this.deviceId = deviceId;
            this.axes = axes;
            this.buttons = buttons;
        }
    }
}
