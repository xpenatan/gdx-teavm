package com.github.xpenatan.gdx.teavm.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidInputBridge;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TeaAndroidView extends GLSurfaceView implements GLSurfaceView.Renderer, View.OnTouchListener {
    private static final int TOUCH_DOWN = 0;
    private static final int TOUCH_UP = 1;
    private static final int TOUCH_DRAGGED = 2;
    private static final int TOUCH_CANCELLED = 3;
    private static final int KEY_DOWN = 0;
    private static final int KEY_UP = 1;

    private boolean started;
    private boolean disposed;

    private final AndroidInputBridge.OnscreenKeyboardController keyboardController =
            new AndroidInputBridge.OnscreenKeyboardController() {
                @Override
                public void setVisible(final boolean visible) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if(disposed) {
                                return;
                            }
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if(inputMethodManager == null) {
                                return;
                            }
                            requestFocus();
                            if(visible) {
                                inputMethodManager.showSoftInput(TeaAndroidView.this, InputMethodManager.SHOW_IMPLICIT);
                            }
                            else {
                                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
                            }
                        }
                    });
                }
            };

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
        AndroidInputBridge.setOnscreenKeyboardController(keyboardController);
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
            nativeResume();
        }
    }

    public void dispose() {
        if(disposed) {
            return;
        }
        disposed = true;
        AndroidInputBridge.clearOnscreenKeyboardController(keyboardController);
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
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        outAttrs.inputType = InputType.TYPE_CLASS_TEXT;
        outAttrs.initialSelStart = 0;
        outAttrs.initialSelEnd = 0;
        return new TeaAndroidInputConnection(this);
    }

    @Override
    public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl,
                                 javax.microedition.khronos.egl.EGLConfig config) {
        if(!started) {
            started = true;
            nativeStart(prepareAssetRoot().getAbsolutePath());
        }
        else if(!disposed) {
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
        final char typedCharacter = resolveTypedCharacter(keyCode, event);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeKey(KEY_DOWN, keyCode);
                if(typedCharacter != 0) {
                    nativeKeyTyped(typedCharacter);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onKeyUp(final int keyCode, KeyEvent event) {
        if(disposed) {
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
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if(disposed) {
            return true;
        }
        if(event != null && event.getCharacters() != null && !event.getCharacters().isEmpty()) {
            queueTypedText(event.getCharacters());
            return true;
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
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

    private char resolveTypedCharacter(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL) {
            return '\b';
        }
        if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
            return '\n';
        }
        if(keyCode == KeyEvent.KEYCODE_TAB) {
            return '\t';
        }
        if(event == null || event.isCtrlPressed() || event.isAltPressed() || event.isMetaPressed()) {
            return 0;
        }
        int unicodeChar = event.getUnicodeChar();
        return unicodeChar == 0 ? 0 : (char)unicodeChar;
    }

    private void queueTypedText(CharSequence text) {
        if(text == null || text.length() == 0) {
            return;
        }
        final String value = text.toString();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < value.length(); i++) {
                    nativeKeyTyped(value.charAt(i));
                }
            }
        });
    }

    private void queueBackspace(int count) {
        if(count <= 0) {
            return;
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < count; i++) {
                    nativeKeyTyped('\b');
                }
            }
        });
    }

    private void queueNewline() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                nativeKeyTyped('\n');
            }
        });
    }

    private native void nativeStart(String workingDirectory);
    private native void nativeResize(int width, int height);
    private native void nativeRender();
    private native void nativePause();
    private native void nativeResume();
    private native void nativeDispose();
    private native void nativeTouch(int type, int pointer, int x, int y, float pressure);
    private native void nativeKey(int type, int keycode);
    private native void nativeKeyTyped(char character);

    private class TeaAndroidInputConnection extends BaseInputConnection {
        TeaAndroidInputConnection(View targetView) {
            super(targetView, false);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            queueTypedText(text);
            return true;
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if(beforeLength > 0) {
                queueBackspace(beforeLength);
                return true;
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if(event == null) {
                return false;
            }
            int keyCode = event.getKeyCode();
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    queueBackspace(1);
                    return true;
                }
                if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    queueNewline();
                    return true;
                }
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean performEditorAction(int actionCode) {
            if(actionCode == EditorInfo.IME_ACTION_DONE
                    || actionCode == EditorInfo.IME_ACTION_GO
                    || actionCode == EditorInfo.IME_ACTION_SEND
                    || actionCode == EditorInfo.IME_ACTION_NEXT) {
                queueNewline();
                return true;
            }
            return super.performEditorAction(actionCode);
        }
    }
}
