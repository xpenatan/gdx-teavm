package com.github.xpenatan.gdx.teavm.android;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TeaAndroidActivity extends Activity implements GLSurfaceView.Renderer, View.OnTouchListener {
    private static final int TOUCH_DOWN = 0;
    private static final int TOUCH_UP = 1;
    private static final int TOUCH_DRAGGED = 2;
    private static final int TOUCH_CANCELLED = 3;
    private static final int KEY_DOWN = 0;
    private static final int KEY_UP = 1;

    private GLSurfaceView view;
    private boolean started;

    static {
        System.loadLibrary("app");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);
        view.setRenderer(this);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener(this);
        setContentView(view);
    }

    @Override
    protected void onPause() {
        nativePause();
        if(view != null) {
            view.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(view != null) {
            view.onResume();
        }
        nativeResume();
    }

    @Override
    protected void onDestroy() {
        nativeDispose();
        super.onDestroy();
    }

    @Override
    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl,
                                 javax.microedition.khronos.egl.EGLConfig config) {
        if(!started) {
            started = true;
            nativeStart(prepareAssetRoot().getAbsolutePath());
        }
        else {
            nativeResume();
        }
    }

    @Override
    public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl, int width, int height) {
        nativeResize(width, height);
    }

    @Override
    public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl) {
        nativeRender();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
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
        view.queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeTouch(type, pointerId, x, y, pressure);
            }
        });
    }

    @Override
    public boolean onKeyDown(final int keyCode, KeyEvent event) {
        view.queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeKey(KEY_DOWN, keyCode);
            }
        });
        return true;
    }

    @Override
    public boolean onKeyUp(final int keyCode, KeyEvent event) {
        view.queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeKey(KEY_UP, keyCode);
            }
        });
        return true;
    }

    private File prepareAssetRoot() {
        File root = getFilesDir();
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
        String[] children = getAssets().list(path);
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
        try(InputStream input = getAssets().open(path);
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

    private native void nativeStart(String workingDirectory);
    private native void nativeResize(int width, int height);
    private native void nativeRender();
    private native void nativePause();
    private native void nativeResume();
    private native void nativeDispose();
    private native void nativeTouch(int type, int pointer, int x, int y, float pressure);
    private native void nativeKey(int type, int keycode);
}
