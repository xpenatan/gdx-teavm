package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.NativeInputConfiguration;

public class TeaMockInput implements Input {
    private InputProcessor inputProcessor;
    private KeyboardHeightObserver keyboardHeightObserver;

    @Override
    public float getAccelerometerX() {
        return 0;
    }

    @Override
    public float getAccelerometerY() {
        return 0;
    }

    @Override
    public float getAccelerometerZ() {
        return 0;
    }

    @Override
    public float getGyroscopeX() {
        return 0;
    }

    @Override
    public float getGyroscopeY() {
        return 0;
    }

    @Override
    public float getGyroscopeZ() {
        return 0;
    }

    @Override
    public int getMaxPointers() {
        return 1;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getX(int pointer) {
        return 0;
    }

    @Override
    public int getDeltaX() {
        return 0;
    }

    @Override
    public int getDeltaX(int pointer) {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getY(int pointer) {
        return 0;
    }

    @Override
    public int getDeltaY() {
        return 0;
    }

    @Override
    public int getDeltaY(int pointer) {
        return 0;
    }

    @Override
    public boolean isTouched() {
        return false;
    }

    @Override
    public boolean justTouched() {
        return false;
    }

    @Override
    public boolean isTouched(int pointer) {
        return false;
    }

    @Override
    public float getPressure() {
        return 0;
    }

    @Override
    public float getPressure(int pointer) {
        return 0;
    }

    @Override
    public boolean isButtonPressed(int button) {
        return false;
    }

    @Override
    public boolean isButtonJustPressed(int button) {
        return false;
    }

    @Override
    public boolean isKeyPressed(int key) {
        return false;
    }

    @Override
    public boolean isKeyJustPressed(int key) {
        return false;
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint) {
        if(listener != null) {
            listener.canceled();
        }
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint, OnscreenKeyboardType type) {
        if(listener != null) {
            listener.canceled();
        }
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean visible) {
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean visible, OnscreenKeyboardType type) {
    }

    @Override
    public void openTextInputField(NativeInputConfiguration configuration) {
    }

    @Override
    public void closeTextInputField(boolean isConfirmative) {
    }

    @Override
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        keyboardHeightObserver = observer;
    }

    public KeyboardHeightObserver getKeyboardHeightObserver() {
        return keyboardHeightObserver;
    }

    @Override
    public void vibrate(int milliseconds) {
    }

    @Override
    public void vibrate(int milliseconds, boolean fallback) {
    }

    @Override
    public void vibrate(int milliseconds, int amplitude, boolean fallback) {
    }

    @Override
    public void vibrate(VibrationType vibrationType) {
    }

    @Override
    public float getAzimuth() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public void getRotationMatrix(float[] matrix) {
        if(matrix == null || matrix.length < 9) {
            return;
        }
        for(int i = 0; i < matrix.length; i++) {
            matrix[i] = 0;
        }
        matrix[0] = 1;
        matrix[4] = 1;
        matrix[8] = 1;
    }

    @Override
    public long getCurrentEventTime() {
        return 0;
    }

    @Override
    public void setCatchKey(int keycode, boolean catchKey) {
    }

    @Override
    public boolean isCatchKey(int keycode) {
        return false;
    }

    @Override
    public void setInputProcessor(InputProcessor processor) {
        inputProcessor = processor;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        return false;
    }

    @Override
    public int getRotation() {
        return 0;
    }

    @Override
    public Orientation getNativeOrientation() {
        return Orientation.Landscape;
    }

    @Override
    public void setCursorCatched(boolean catched) {
    }

    @Override
    public boolean isCursorCatched() {
        return false;
    }

    @Override
    public void setCursorPosition(int x, int y) {
    }
}
