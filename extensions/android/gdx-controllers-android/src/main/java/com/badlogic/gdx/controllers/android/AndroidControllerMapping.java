package com.badlogic.gdx.controllers.android;

import com.badlogic.gdx.controllers.ControllerMapping;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidControllerSupport;

final class AndroidControllerMapping extends ControllerMapping {
    private static AndroidControllerMapping instance;

    private AndroidControllerMapping() {
        super(
                AndroidControllerSupport.AXIS_LEFT_X,
                AndroidControllerSupport.AXIS_LEFT_Y,
                AndroidControllerSupport.AXIS_RIGHT_X,
                AndroidControllerSupport.AXIS_RIGHT_Y,
                AndroidControllerSupport.BUTTON_A,
                AndroidControllerSupport.BUTTON_B,
                AndroidControllerSupport.BUTTON_X,
                AndroidControllerSupport.BUTTON_Y,
                AndroidControllerSupport.BUTTON_BACK,
                AndroidControllerSupport.BUTTON_START,
                AndroidControllerSupport.BUTTON_L1,
                AndroidControllerSupport.BUTTON_L2,
                AndroidControllerSupport.BUTTON_R1,
                AndroidControllerSupport.BUTTON_R2,
                AndroidControllerSupport.BUTTON_LEFT_STICK,
                AndroidControllerSupport.BUTTON_RIGHT_STICK,
                AndroidControllerSupport.BUTTON_DPAD_UP,
                AndroidControllerSupport.BUTTON_DPAD_DOWN,
                AndroidControllerSupport.BUTTON_DPAD_LEFT,
                AndroidControllerSupport.BUTTON_DPAD_RIGHT
        );
    }

    static AndroidControllerMapping getInstance() {
        if(instance == null) {
            instance = new AndroidControllerMapping();
        }
        return instance;
    }
}
