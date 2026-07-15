package com.github.xpenatan.gdx.teavm.android;

import android.content.Context;
import android.hardware.input.InputManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidControllerSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TeaAndroidView extends GLSurfaceView implements GLSurfaceView.Renderer, View.OnTouchListener,
        InputManager.InputDeviceListener {
    private static final int TOUCH_DOWN = 0;
    private static final int TOUCH_UP = 1;
    private static final int TOUCH_DRAGGED = 2;
    private static final int TOUCH_CANCELLED = 3;
    private static final int KEY_DOWN = 0;
    private static final int KEY_UP = 1;
    private static final int BUTTON_UNDEFINED = -1;

    private boolean started;
    private boolean disposed;
    private InputManager inputManager;
    private final SparseBooleanArray connectedControllers = new SparseBooleanArray();

    static {
        System.loadLibrary("app");
    }

    public TeaAndroidView(Context context) {
        super(context);
        initialize();
    }

    public TeaAndroidView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        requestFocus();

        Object inputService = getContext().getSystemService(Context.INPUT_SERVICE);
        if(inputService instanceof InputManager) {
            inputManager = (InputManager)inputService;
            inputManager.registerInputDeviceListener(this, null);
        }
    }

    @Override
    public void onPause() {
        if(started && !disposed) {
            nativePause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(started && !disposed) {
            syncConnectedControllers();
            nativeResume();
        }
    }

    public void dispose() {
        if(disposed) {
            return;
        }
        disposed = true;
        if(inputManager != null) {
            inputManager.unregisterInputDeviceListener(this);
            inputManager = null;
        }
        connectedControllers.clear();
        if(started) {
            nativeDispose();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        dispose();
        super.onDetachedFromWindow();
    }

    @Override
    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl,
                                 javax.microedition.khronos.egl.EGLConfig config) {
        if(!started) {
            started = true;
            nativeStart(prepareAssetRoot().getAbsolutePath());
            syncConnectedControllers();
        }
        else if(!disposed) {
            syncConnectedControllers();
            nativeResume();
        }
    }

    @Override
    public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl, int width, int height) {
        if(!disposed) {
            nativeResize(width, height);
        }
    }

    @Override
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        if(!disposed) {
            nativeRender();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(disposed) {
            return true;
        }
        final int action = event.getActionMasked();
        final int actionIndex = event.getActionIndex();
        if(action == MotionEvent.ACTION_MOVE) {
            final int pointerCount = event.getPointerCount();
            for(int i = 0; i < pointerCount; i++) {
                queueTouch(TOUCH_DRAGGED, event, i);
            }
        }
        else if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            queueTouch(TOUCH_DOWN, event, actionIndex);
        }
        else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            queueTouch(TOUCH_UP, event, actionIndex);
        }
        else if(action == MotionEvent.ACTION_CANCEL) {
            final int pointerCount = event.getPointerCount();
            for(int i = 0; i < pointerCount; i++) {
                queueTouch(TOUCH_CANCELLED, event, i);
            }
        }
        return true;
    }

    private void queueTouch(final int type, MotionEvent event, int pointerIndex) {
        final int pointerId = event.getPointerId(pointerIndex);
        final int x = (int)event.getX(pointerIndex);
        final int y = (int)event.getY(pointerIndex);
        final float pressure = event.getPressure(pointerIndex);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeTouch(type, pointerId, x, y, pressure);
            }
        });
    }

    @Override
    public boolean onKeyDown(final int keyCode, KeyEvent event) {
        if(disposed) {
            return true;
        }
        if(handleControllerKey(true, keyCode, event)) {
            return true;
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeKey(KEY_DOWN, keyCode);
            }
        });
        return true;
    }

    @Override
    public boolean onKeyUp(final int keyCode, KeyEvent event) {
        if(disposed) {
            return true;
        }
        if(handleControllerKey(false, keyCode, event)) {
            return true;
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeKey(KEY_UP, keyCode);
            }
        });
        return true;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if(disposed) {
            return true;
        }

        InputDevice device = event.getDevice();
        if(!started || !isControllerDevice(device) || !isControllerSource(event.getSource())) {
            return super.onGenericMotionEvent(event);
        }

        if(event.getAction() == MotionEvent.ACTION_MOVE) {
            ensureControllerConnected(device.getId());
            queueControllerAxes(device, event);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        if(!started || disposed) {
            return;
        }
        syncControllerDevice(deviceId);
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        if(!started || disposed) {
            return;
        }
        if(connectedControllers.get(deviceId)) {
            connectedControllers.delete(deviceId);
            queueControllerDisconnected(deviceId);
        }
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {
        if(!started || disposed) {
            return;
        }
        syncControllerDevice(deviceId);
    }

    private File prepareAssetRoot() {
        Context context = getContext();
        File root = context.getFilesDir();
        File assetRoot = new File(root, "assets");
        try {
            copyAssets("", assetRoot);
        }
        catch(IOException e) {
            throw new RuntimeException("Unable to prepare gdx-teavm assets", e);
        }
        return root;
    }

    private void copyAssets(String path, File root) throws IOException {
        String[] children = getContext().getAssets().list(path);
        if(children == null || children.length == 0) {
            copyAssetFile(path, new File(root, path));
            return;
        }
        File directory = path.isEmpty() ? root : new File(root, path);
        if(!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Unable to create asset directory: " + directory);
        }
        for(String child : children) {
            copyAssets(path.isEmpty() ? child : path + "/" + child, root);
        }
    }

    private void copyAssetFile(String path, File output) throws IOException {
        File parent = output.getParentFile();
        if(parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create asset directory: " + parent);
        }
        try(InputStream input = getContext().getAssets().open(path);
            FileOutputStream outputStream = new FileOutputStream(output)) {
            byte[] buffer = new byte[16 * 1024];
            while(true) {
                int read = input.read(buffer);
                if(read < 0) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
        }
    }

    private boolean handleControllerKey(final boolean down, int keyCode, KeyEvent event) {
        InputDevice device = event.getDevice();
        if(!started || !isControllerDevice(device)) {
            return false;
        }

        ensureControllerConnected(device.getId());
        int controllerButton = mapControllerButton(keyCode);
        if(controllerButton == BUTTON_UNDEFINED || (down && event.getRepeatCount() > 0)) {
            return true;
        }

        final int deviceId = device.getId();
        final float value = down ? 1f : 0f;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeControllerButton(deviceId, controllerButton, value);
            }
        });
        return true;
    }

    private void queueControllerAxes(InputDevice device, MotionEvent event) {
        final int deviceId = device.getId();
        final float leftX = getAxisValue(event, device, MotionEvent.AXIS_X);
        final float leftY = getAxisValue(event, device, MotionEvent.AXIS_Y);
        final float rightX = getAxisValue(event, device, MotionEvent.AXIS_Z, MotionEvent.AXIS_RX);
        final float rightY = getAxisValue(event, device, MotionEvent.AXIS_RZ, MotionEvent.AXIS_RY);
        final float leftTrigger = getAxisValue(event, device, MotionEvent.AXIS_LTRIGGER, MotionEvent.AXIS_BRAKE);
        final float rightTrigger = getAxisValue(event, device, MotionEvent.AXIS_RTRIGGER, MotionEvent.AXIS_GAS);
        final float hatX = getAxisValue(event, device, MotionEvent.AXIS_HAT_X);
        final float hatY = getAxisValue(event, device, MotionEvent.AXIS_HAT_Y);

        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_LEFT_X, leftX);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_LEFT_Y, leftY);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_RIGHT_X, rightX);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_RIGHT_Y, rightY);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_LEFT_TRIGGER, leftTrigger);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_RIGHT_TRIGGER, rightTrigger);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_HAT_X, hatX);
                nativeControllerAxis(deviceId, AndroidControllerSupport.AXIS_HAT_Y, hatY);
            }
        });
    }

    private void syncConnectedControllers() {
        int[] deviceIds = InputDevice.getDeviceIds();
        SparseBooleanArray activeControllers = new SparseBooleanArray(deviceIds.length);
        for(int deviceId : deviceIds) {
            InputDevice device = InputDevice.getDevice(deviceId);
            if(isControllerDevice(device)) {
                activeControllers.put(deviceId, true);
                ensureControllerConnected(deviceId);
            }
        }

        for(int i = connectedControllers.size() - 1; i >= 0; i--) {
            int deviceId = connectedControllers.keyAt(i);
            if(!activeControllers.get(deviceId)) {
                connectedControllers.delete(deviceId);
                queueControllerDisconnected(deviceId);
            }
        }
    }

    private void syncControllerDevice(int deviceId) {
        InputDevice device = InputDevice.getDevice(deviceId);
        if(isControllerDevice(device)) {
            ensureControllerConnected(deviceId);
        }
        else if(connectedControllers.get(deviceId)) {
            connectedControllers.delete(deviceId);
            queueControllerDisconnected(deviceId);
        }
    }

    private void ensureControllerConnected(int deviceId) {
        if(connectedControllers.get(deviceId)) {
            return;
        }
        connectedControllers.put(deviceId, true);
        queueControllerConnected(deviceId);
    }

    private void queueControllerConnected(final int deviceId) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeControllerConnected(deviceId);
            }
        });
    }

    private void queueControllerDisconnected(final int deviceId) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeControllerDisconnected(deviceId);
            }
        });
    }

    private boolean isControllerDevice(InputDevice device) {
        if(device == null) {
            return false;
        }
        if(device.getId() < 0) {
            return false;
        }
        int sources = device.getSources();
        return hasSource(sources, InputDevice.SOURCE_GAMEPAD)
                || hasSource(sources, InputDevice.SOURCE_JOYSTICK);
    }

    private boolean isControllerSource(int sources) {
        return hasSource(sources, InputDevice.SOURCE_GAMEPAD) || hasSource(sources, InputDevice.SOURCE_JOYSTICK);
    }

    private boolean hasSource(int sources, int source) {
        return (sources & source) == source;
    }

    private float getAxisValue(MotionEvent event, InputDevice device, int axis) {
        return hasAxis(device, axis) ? event.getAxisValue(axis) : 0f;
    }

    private float getAxisValue(MotionEvent event, InputDevice device, int primaryAxis, int secondaryAxis) {
        if(hasAxis(device, primaryAxis)) {
            return event.getAxisValue(primaryAxis);
        }
        if(hasAxis(device, secondaryAxis)) {
            return event.getAxisValue(secondaryAxis);
        }
        return 0f;
    }

    private boolean hasAxis(InputDevice device, int axis) {
        return device != null && device.getMotionRange(axis) != null;
    }

    private int mapControllerButton(int keyCode) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
                return AndroidControllerSupport.BUTTON_A;
            case KeyEvent.KEYCODE_BUTTON_B:
                return AndroidControllerSupport.BUTTON_B;
            case KeyEvent.KEYCODE_BUTTON_X:
                return AndroidControllerSupport.BUTTON_X;
            case KeyEvent.KEYCODE_BUTTON_Y:
                return AndroidControllerSupport.BUTTON_Y;
            case KeyEvent.KEYCODE_BUTTON_L1:
                return AndroidControllerSupport.BUTTON_L1;
            case KeyEvent.KEYCODE_BUTTON_R1:
                return AndroidControllerSupport.BUTTON_R1;
            case KeyEvent.KEYCODE_BUTTON_L2:
                return AndroidControllerSupport.BUTTON_L2;
            case KeyEvent.KEYCODE_BUTTON_R2:
                return AndroidControllerSupport.BUTTON_R2;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
            case KeyEvent.KEYCODE_BACK:
                return AndroidControllerSupport.BUTTON_BACK;
            case KeyEvent.KEYCODE_BUTTON_START:
                return AndroidControllerSupport.BUTTON_START;
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                return AndroidControllerSupport.BUTTON_LEFT_STICK;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                return AndroidControllerSupport.BUTTON_RIGHT_STICK;
            case KeyEvent.KEYCODE_DPAD_UP:
                return AndroidControllerSupport.BUTTON_DPAD_UP;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return AndroidControllerSupport.BUTTON_DPAD_DOWN;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return AndroidControllerSupport.BUTTON_DPAD_LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return AndroidControllerSupport.BUTTON_DPAD_RIGHT;
            default:
                return BUTTON_UNDEFINED;
        }
    }

    private native void nativeStart(String workingDirectory);
    private native void nativeResize(int width, int height);
    private native void nativeRender();
    private native void nativePause();
    private native void nativeResume();
    private native void nativeDispose();
    private native void nativeTouch(int type, int pointer, int x, int y, float pressure);
    private native void nativeKey(int type, int keycode);
    private native void nativeControllerConnected(int deviceId);
    private native void nativeControllerDisconnected(int deviceId);
    private native void nativeControllerButton(int deviceId, int buttonCode, float value);
    private native void nativeControllerAxis(int deviceId, int axisCode, float value);
}
