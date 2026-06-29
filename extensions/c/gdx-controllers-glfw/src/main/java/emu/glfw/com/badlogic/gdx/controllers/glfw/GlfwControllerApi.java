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

package emu.glfw.com.badlogic.gdx.controllers.glfw;

import org.teavm.interop.Import;
import org.teavm.interop.Structure;
import org.teavm.interop.c.Include;

@Include("GLFW/glfw3.h")
final class GlfwControllerApi {

    private GlfwControllerApi() {
    }

    @Import(name = "glfwJoystickPresent")
    static native int glfwJoystickPresent(int joystickId);

    @Import(name = "glfwJoystickIsGamepad")
    static native int glfwJoystickIsGamepad(int joystickId);

    @Import(name = "glfwGetGamepadName")
    static native String glfwGetGamepadName(int joystickId);

    @Import(name = "glfwGetJoystickName")
    static native String glfwGetJoystickName(int joystickId);

    @Import(name = "glfwGetGamepadState")
    static native int glfwGetGamepadState(int joystickId, GlfwGamepadState state);

    static final class GlfwGamepadState extends Structure {
        public byte buttonA;
        public byte buttonB;
        public byte buttonX;
        public byte buttonY;
        public byte buttonLeftBumper;
        public byte buttonRightBumper;
        public byte buttonBack;
        public byte buttonStart;
        public byte buttonGuide;
        public byte buttonLeftThumb;
        public byte buttonRightThumb;
        public byte buttonDpadUp;
        public byte buttonDpadRight;
        public byte buttonDpadDown;
        public byte buttonDpadLeft;
        public float axisLeftX;
        public float axisLeftY;
        public float axisRightX;
        public float axisRightY;
        public float axisLeftTrigger;
        public float axisRightTrigger;
    }
}
