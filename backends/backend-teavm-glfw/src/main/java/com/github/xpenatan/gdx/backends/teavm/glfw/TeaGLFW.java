package com.github.xpenatan.gdx.backends.teavm.glfw;

import com.badlogic.gdx.Gdx;
import org.teavm.interop.Address;
import org.teavm.interop.Function;
import org.teavm.interop.Import;
import org.teavm.interop.Strings;
import org.teavm.interop.Structure;
import org.teavm.interop.c.Include;

@Include("GLFW/glfw3.h")
public class TeaGLFW {
    public static final int GLFW_VERSION_MAJOR = 3;
    public static final int GLFW_VERSION_MINOR = 4;
    public static final int GLFW_VERSION_REVISION = 0;

    public static final int GLFW_TRUE = 1;
    public static final int GLFW_FALSE = 0;

    public static final int GLFW_RELEASE = 0;
    public static final int GLFW_PRESS = 1;
    public static final int GLFW_REPEAT = 2;

    public static final int GLFW_HAT_CENTERED = 0;
    public static final int GLFW_HAT_UP = 1;
    public static final int GLFW_HAT_RIGHT = 2;
    public static final int GLFW_HAT_DOWN = 4;
    public static final int GLFW_HAT_LEFT = 8;
    public static final int GLFW_HAT_RIGHT_UP = GLFW_HAT_RIGHT | GLFW_HAT_UP;
    public static final int GLFW_HAT_RIGHT_DOWN = GLFW_HAT_RIGHT | GLFW_HAT_DOWN;
    public static final int GLFW_HAT_LEFT_UP = GLFW_HAT_LEFT | GLFW_HAT_UP;
    public static final int GLFW_HAT_LEFT_DOWN = GLFW_HAT_LEFT | GLFW_HAT_DOWN;

    public static final int GLFW_KEY_UNKNOWN = -1;

    public static final int GLFW_KEY_SPACE = 32;
    public static final int GLFW_KEY_APOSTROPHE = 39;
    public static final int GLFW_KEY_COMMA = 44;
    public static final int GLFW_KEY_MINUS = 45;
    public static final int GLFW_KEY_PERIOD = 46;
    public static final int GLFW_KEY_SLASH = 47;
    public static final int GLFW_KEY_0 = 48;
    public static final int GLFW_KEY_1 = 49;
    public static final int GLFW_KEY_2 = 50;
    public static final int GLFW_KEY_3 = 51;
    public static final int GLFW_KEY_4 = 52;
    public static final int GLFW_KEY_5 = 53;
    public static final int GLFW_KEY_6 = 54;
    public static final int GLFW_KEY_7 = 55;
    public static final int GLFW_KEY_8 = 56;
    public static final int GLFW_KEY_9 = 57;
    public static final int GLFW_KEY_SEMICOLON = 59;
    public static final int GLFW_KEY_EQUAL = 61;
    public static final int GLFW_KEY_A = 65;
    public static final int GLFW_KEY_B = 66;
    public static final int GLFW_KEY_C = 67;
    public static final int GLFW_KEY_D = 68;
    public static final int GLFW_KEY_E = 69;
    public static final int GLFW_KEY_F = 70;
    public static final int GLFW_KEY_G = 71;
    public static final int GLFW_KEY_H = 72;
    public static final int GLFW_KEY_I = 73;
    public static final int GLFW_KEY_J = 74;
    public static final int GLFW_KEY_K = 75;
    public static final int GLFW_KEY_L = 76;
    public static final int GLFW_KEY_M = 77;
    public static final int GLFW_KEY_N = 78;
    public static final int GLFW_KEY_O = 79;
    public static final int GLFW_KEY_P = 80;
    public static final int GLFW_KEY_Q = 81;
    public static final int GLFW_KEY_R = 82;
    public static final int GLFW_KEY_S = 83;
    public static final int GLFW_KEY_T = 84;
    public static final int GLFW_KEY_U = 85;
    public static final int GLFW_KEY_V = 86;
    public static final int GLFW_KEY_W = 87;
    public static final int GLFW_KEY_X = 88;
    public static final int GLFW_KEY_Y = 89;
    public static final int GLFW_KEY_Z = 90;
    public static final int GLFW_KEY_LEFT_BRACKET = 91;
    public static final int GLFW_KEY_BACKSLASH = 92;
    public static final int GLFW_KEY_RIGHT_BRACKET = 93;
    public static final int GLFW_KEY_GRAVE_ACCENT = 96;
    public static final int GLFW_KEY_WORLD_1 = 161;
    public static final int GLFW_KEY_WORLD_2 = 162;

    public static final int GLFW_KEY_ESCAPE = 256;
    public static final int GLFW_KEY_ENTER = 257;
    public static final int GLFW_KEY_TAB = 258;
    public static final int GLFW_KEY_BACKSPACE = 259;
    public static final int GLFW_KEY_INSERT = 260;
    public static final int GLFW_KEY_DELETE = 261;
    public static final int GLFW_KEY_RIGHT = 262;
    public static final int GLFW_KEY_LEFT = 263;
    public static final int GLFW_KEY_DOWN = 264;
    public static final int GLFW_KEY_UP = 265;
    public static final int GLFW_KEY_PAGE_UP = 266;
    public static final int GLFW_KEY_PAGE_DOWN = 267;
    public static final int GLFW_KEY_HOME = 268;
    public static final int GLFW_KEY_END = 269;
    public static final int GLFW_KEY_CAPS_LOCK = 280;
    public static final int GLFW_KEY_SCROLL_LOCK = 281;
    public static final int GLFW_KEY_NUM_LOCK = 282;
    public static final int GLFW_KEY_PRINT_SCREEN = 283;
    public static final int GLFW_KEY_PAUSE = 284;
    public static final int GLFW_KEY_F1 = 290;
    public static final int GLFW_KEY_F2 = 291;
    public static final int GLFW_KEY_F3 = 292;
    public static final int GLFW_KEY_F4 = 293;
    public static final int GLFW_KEY_F5 = 294;
    public static final int GLFW_KEY_F6 = 295;
    public static final int GLFW_KEY_F7 = 296;
    public static final int GLFW_KEY_F8 = 297;
    public static final int GLFW_KEY_F9 = 298;
    public static final int GLFW_KEY_F10 = 299;
    public static final int GLFW_KEY_F11 = 300;
    public static final int GLFW_KEY_F12 = 301;
    public static final int GLFW_KEY_F13 = 302;
    public static final int GLFW_KEY_F14 = 303;
    public static final int GLFW_KEY_F15 = 304;
    public static final int GLFW_KEY_F16 = 305;
    public static final int GLFW_KEY_F17 = 306;
    public static final int GLFW_KEY_F18 = 307;
    public static final int GLFW_KEY_F19 = 308;
    public static final int GLFW_KEY_F20 = 309;
    public static final int GLFW_KEY_F21 = 310;
    public static final int GLFW_KEY_F22 = 311;
    public static final int GLFW_KEY_F23 = 312;
    public static final int GLFW_KEY_F24 = 313;
    public static final int GLFW_KEY_F25 = 314;
    public static final int GLFW_KEY_KP_0 = 320;
    public static final int GLFW_KEY_KP_1 = 321;
    public static final int GLFW_KEY_KP_2 = 322;
    public static final int GLFW_KEY_KP_3 = 323;
    public static final int GLFW_KEY_KP_4 = 324;
    public static final int GLFW_KEY_KP_5 = 325;
    public static final int GLFW_KEY_KP_6 = 326;
    public static final int GLFW_KEY_KP_7 = 327;
    public static final int GLFW_KEY_KP_8 = 328;
    public static final int GLFW_KEY_KP_9 = 329;
    public static final int GLFW_KEY_KP_DECIMAL = 330;
    public static final int GLFW_KEY_KP_DIVIDE = 331;
    public static final int GLFW_KEY_KP_MULTIPLY = 332;
    public static final int GLFW_KEY_KP_SUBTRACT = 333;
    public static final int GLFW_KEY_KP_ADD = 334;
    public static final int GLFW_KEY_KP_ENTER = 335;
    public static final int GLFW_KEY_KP_EQUAL = 336;
    public static final int GLFW_KEY_LEFT_SHIFT = 340;
    public static final int GLFW_KEY_LEFT_CONTROL = 341;
    public static final int GLFW_KEY_LEFT_ALT = 342;
    public static final int GLFW_KEY_LEFT_SUPER = 343;
    public static final int GLFW_KEY_RIGHT_SHIFT = 344;
    public static final int GLFW_KEY_RIGHT_CONTROL = 345;
    public static final int GLFW_KEY_RIGHT_ALT = 346;
    public static final int GLFW_KEY_RIGHT_SUPER = 347;
    public static final int GLFW_KEY_MENU = 348;

    public static final int GLFW_KEY_LAST = GLFW_KEY_MENU;

    public static final int GLFW_MOD_SHIFT = 0x0001;
    public static final int GLFW_MOD_CONTROL = 0x0002;
    public static final int GLFW_MOD_ALT = 0x0004;
    public static final int GLFW_MOD_SUPER = 0x0008;
    public static final int GLFW_MOD_CAPS_LOCK = 0x0010;
    public static final int GLFW_MOD_NUM_LOCK = 0x0020;

    public static final int GLFW_MOUSE_BUTTON_1 = 0;
    public static final int GLFW_MOUSE_BUTTON_2 = 1;
    public static final int GLFW_MOUSE_BUTTON_3 = 2;
    public static final int GLFW_MOUSE_BUTTON_4 = 3;
    public static final int GLFW_MOUSE_BUTTON_5 = 4;
    public static final int GLFW_MOUSE_BUTTON_6 = 5;
    public static final int GLFW_MOUSE_BUTTON_7 = 6;
    public static final int GLFW_MOUSE_BUTTON_8 = 7;
    public static final int GLFW_MOUSE_BUTTON_LAST = GLFW_MOUSE_BUTTON_8;
    public static final int GLFW_MOUSE_BUTTON_LEFT = GLFW_MOUSE_BUTTON_1;
    public static final int GLFW_MOUSE_BUTTON_RIGHT = GLFW_MOUSE_BUTTON_2;
    public static final int GLFW_MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_3;

    public static final int GLFW_JOYSTICK_1 = 0;
    public static final int GLFW_JOYSTICK_2 = 1;
    public static final int GLFW_JOYSTICK_3 = 2;
    public static final int GLFW_JOYSTICK_4 = 3;
    public static final int GLFW_JOYSTICK_5 = 4;
    public static final int GLFW_JOYSTICK_6 = 5;
    public static final int GLFW_JOYSTICK_7 = 6;
    public static final int GLFW_JOYSTICK_8 = 7;
    public static final int GLFW_JOYSTICK_9 = 8;
    public static final int GLFW_JOYSTICK_10 = 9;
    public static final int GLFW_JOYSTICK_11 = 10;
    public static final int GLFW_JOYSTICK_12 = 11;
    public static final int GLFW_JOYSTICK_13 = 12;
    public static final int GLFW_JOYSTICK_14 = 13;
    public static final int GLFW_JOYSTICK_15 = 14;
    public static final int GLFW_JOYSTICK_16 = 15;
    public static final int GLFW_JOYSTICK_LAST = GLFW_JOYSTICK_16;

    public static final int GLFW_GAMEPAD_BUTTON_A = 0;
    public static final int GLFW_GAMEPAD_BUTTON_B = 1;
    public static final int GLFW_GAMEPAD_BUTTON_X = 2;
    public static final int GLFW_GAMEPAD_BUTTON_Y = 3;
    public static final int GLFW_GAMEPAD_BUTTON_LEFT_BUMPER = 4;
    public static final int GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER = 5;
    public static final int GLFW_GAMEPAD_BUTTON_BACK = 6;
    public static final int GLFW_GAMEPAD_BUTTON_START = 7;
    public static final int GLFW_GAMEPAD_BUTTON_GUIDE = 8;
    public static final int GLFW_GAMEPAD_BUTTON_LEFT_THUMB = 9;
    public static final int GLFW_GAMEPAD_BUTTON_RIGHT_THUMB = 10;
    public static final int GLFW_GAMEPAD_BUTTON_DPAD_UP = 11;
    public static final int GLFW_GAMEPAD_BUTTON_DPAD_RIGHT = 12;
    public static final int GLFW_GAMEPAD_BUTTON_DPAD_DOWN = 13;
    public static final int GLFW_GAMEPAD_BUTTON_DPAD_LEFT = 14;
    public static final int GLFW_GAMEPAD_BUTTON_LAST = GLFW_GAMEPAD_BUTTON_DPAD_LEFT;

    public static final int GLFW_GAMEPAD_BUTTON_CROSS = GLFW_GAMEPAD_BUTTON_A;
    public static final int GLFW_GAMEPAD_BUTTON_CIRCLE = GLFW_GAMEPAD_BUTTON_B;
    public static final int GLFW_GAMEPAD_BUTTON_SQUARE = GLFW_GAMEPAD_BUTTON_X;
    public static final int GLFW_GAMEPAD_BUTTON_TRIANGLE = GLFW_GAMEPAD_BUTTON_Y;

    public static final int GLFW_GAMEPAD_AXIS_LEFT_X = 0;
    public static final int GLFW_GAMEPAD_AXIS_LEFT_Y = 1;
    public static final int GLFW_GAMEPAD_AXIS_RIGHT_X = 2;
    public static final int GLFW_GAMEPAD_AXIS_RIGHT_Y = 3;
    public static final int GLFW_GAMEPAD_AXIS_LEFT_TRIGGER = 4;
    public static final int GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER = 5;
    public static final int GLFW_GAMEPAD_AXIS_LAST = GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

    public static final int GLFW_NO_ERROR = 0;
    public static final int GLFW_NOT_INITIALIZED = 0x00010001;
    public static final int GLFW_NO_CURRENT_CONTEXT = 0x00010002;
    public static final int GLFW_INVALID_ENUM = 0x00010003;
    public static final int GLFW_INVALID_VALUE = 0x00010004;
    public static final int GLFW_OUT_OF_MEMORY = 0x00010005;
    public static final int GLFW_API_UNAVAILABLE = 0x00010006;
    public static final int GLFW_VERSION_UNAVAILABLE = 0x00010007;
    public static final int GLFW_PLATFORM_ERROR = 0x00010008;
    public static final int GLFW_FORMAT_UNAVAILABLE = 0x00010009;
    public static final int GLFW_NO_WINDOW_CONTEXT = 0x0001000A;
    public static final int GLFW_CURSOR_UNAVAILABLE = 0x0001000B;
    public static final int GLFW_FEATURE_UNAVAILABLE = 0x0001000C;
    public static final int GLFW_FEATURE_UNIMPLEMENTED = 0x0001000D;
    public static final int GLFW_PLATFORM_UNAVAILABLE = 0x0001000E;

    public static final int GLFW_FOCUSED = 0x00020001;
    public static final int GLFW_ICONIFIED = 0x00020002;
    public static final int GLFW_RESIZABLE = 0x00020003;
    public static final int GLFW_VISIBLE = 0x00020004;
    public static final int GLFW_DECORATED = 0x00020005;
    public static final int GLFW_AUTO_ICONIFY = 0x00020006;
    public static final int GLFW_FLOATING = 0x00020007;
    public static final int GLFW_MAXIMIZED = 0x00020008;
    public static final int GLFW_CENTER_CURSOR = 0x00020009;
    public static final int GLFW_TRANSPARENT_FRAMEBUFFER = 0x0002000A;
    public static final int GLFW_HOVERED = 0x0002000B;
    public static final int GLFW_FOCUS_ON_SHOW = 0x0002000C;
    public static final int GLFW_MOUSE_PASSTHROUGH = 0x0002000D;
    public static final int GLFW_POSITION_X = 0x0002000E;
    public static final int GLFW_POSITION_Y = 0x0002000F;

    public static final int GLFW_RED_BITS = 0x00021001;
    public static final int GLFW_GREEN_BITS = 0x00021002;
    public static final int GLFW_BLUE_BITS = 0x00021003;
    public static final int GLFW_ALPHA_BITS = 0x00021004;
    public static final int GLFW_DEPTH_BITS = 0x00021005;
    public static final int GLFW_STENCIL_BITS = 0x00021006;
    public static final int GLFW_ACCUM_RED_BITS = 0x00021007;
    public static final int GLFW_ACCUM_GREEN_BITS = 0x00021008;
    public static final int GLFW_ACCUM_BLUE_BITS = 0x00021009;
    public static final int GLFW_ACCUM_ALPHA_BITS = 0x0002100A;
    public static final int GLFW_AUX_BUFFERS = 0x0002100B;
    public static final int GLFW_STEREO = 0x0002100C;
    public static final int GLFW_SAMPLES = 0x0002100D;
    public static final int GLFW_SRGB_CAPABLE = 0x0002100E;
    public static final int GLFW_REFRESH_RATE = 0x0002100F;
    public static final int GLFW_DOUBLEBUFFER = 0x00021010;

    public static final int GLFW_CLIENT_API = 0x00022001;
    public static final int GLFW_CONTEXT_VERSION_MAJOR = 0x00022002;
    public static final int GLFW_CONTEXT_VERSION_MINOR = 0x00022003;
    public static final int GLFW_CONTEXT_REVISION = 0x00022004;
    public static final int GLFW_CONTEXT_ROBUSTNESS = 0x00022005;
    public static final int GLFW_OPENGL_FORWARD_COMPAT = 0x00022006;
    public static final int GLFW_CONTEXT_DEBUG = 0x00022007;
    public static final int GLFW_OPENGL_DEBUG_CONTEXT = GLFW_CONTEXT_DEBUG;
    public static final int GLFW_OPENGL_PROFILE = 0x00022008;
    public static final int GLFW_CONTEXT_RELEASE_BEHAVIOR = 0x00022009;
    public static final int GLFW_CONTEXT_NO_ERROR = 0x0002200A;
    public static final int GLFW_CONTEXT_CREATION_API = 0x0002200B;
    public static final int GLFW_SCALE_TO_MONITOR = 0x0002200C;
    public static final int GLFW_SCALE_FRAMEBUFFER = 0x0002200D;
    public static final int GLFW_COCOA_RETINA_FRAMEBUFFER = 0x00023001;
    public static final int GLFW_COCOA_FRAME_NAME = 0x00023002;
    public static final int GLFW_COCOA_GRAPHICS_SWITCHING = 0x00023003;
    public static final int GLFW_X11_CLASS_NAME = 0x00024001;
    public static final int GLFW_X11_INSTANCE_NAME = 0x00024002;
    public static final int GLFW_WIN32_KEYBOARD_MENU = 0x00025001;
    public static final int GLFW_WIN32_SHOWDEFAULT = 0x00025002;
    public static final int GLFW_WAYLAND_APP_ID = 0x00026001;

    public static final int GLFW_NO_API = 0;
    public static final int GLFW_OPENGL_API = 0x00030001;
    public static final int GLFW_OPENGL_ES_API = 0x00030002;

    public static final int GLFW_NO_ROBUSTNESS = 0;
    public static final int GLFW_NO_RESET_NOTIFICATION = 0x00031001;
    public static final int GLFW_LOSE_CONTEXT_ON_RESET = 0x00031002;

    public static final int GLFW_OPENGL_ANY_PROFILE = 0;
    public static final int GLFW_OPENGL_CORE_PROFILE = 0x00032001;
    public static final int GLFW_OPENGL_COMPAT_PROFILE = 0x00032002;

    public static final int GLFW_CURSOR = 0x00033001;
    public static final int GLFW_STICKY_KEYS = 0x00033002;
    public static final int GLFW_STICKY_MOUSE_BUTTONS = 0x00033003;
    public static final int GLFW_LOCK_KEY_MODS = 0x00033004;
    public static final int GLFW_RAW_MOUSE_MOTION = 0x00033005;

    public static final int GLFW_CURSOR_NORMAL = 0x00034001;
    public static final int GLFW_CURSOR_HIDDEN = 0x00034002;
    public static final int GLFW_CURSOR_DISABLED = 0x00034003;
    public static final int GLFW_CURSOR_CAPTURED = 0x00034004;

    public static final int GLFW_ANY_RELEASE_BEHAVIOR = 0;
    public static final int GLFW_RELEASE_BEHAVIOR_FLUSH = 0x00035001;
    public static final int GLFW_RELEASE_BEHAVIOR_NONE = 0x00035002;

    public static final int GLFW_NATIVE_CONTEXT_API = 0x00036001;
    public static final int GLFW_EGL_CONTEXT_API = 0x00036002;
    public static final int GLFW_OSMESA_CONTEXT_API = 0x00036003;

    public static final int GLFW_ANGLE_PLATFORM_TYPE_NONE = 0x00037001;
    public static final int GLFW_ANGLE_PLATFORM_TYPE_OPENGL = 0x00037002;
    public static final int GLFW_ANGLE_PLATFORM_TYPE_OPENGLES = 0x00037003;
    public static final int GLFW_ANGLE_PLATFORM_TYPE_D3D9 = 0x00037004;
    public static final int GLFW_ANGLE_PLATFORM_TYPE_D3D11 = 0x00037005;
    public static final int GLFW_ANGLE_PLATFORM_TYPE_VULKAN = 0x00037007;
    public static final int GLFW_ANGLE_PLATFORM_TYPE_METAL = 0x00037008;

    public static final int GLFW_WAYLAND_PREFER_LIBDECOR = 0x00038001;
    public static final int GLFW_WAYLAND_DISABLE_LIBDECOR = 0x00038002;

    public static final int GLFW_ANY_POSITION = 0x80000000;

    public static final int GLFW_ARROW_CURSOR = 0x00036001;
    public static final int GLFW_IBEAM_CURSOR = 0x00036002;
    public static final int GLFW_CROSSHAIR_CURSOR = 0x00036003;
    public static final int GLFW_POINTING_HAND_CURSOR = 0x00036004;
    public static final int GLFW_RESIZE_EW_CURSOR = 0x00036005;
    public static final int GLFW_RESIZE_NS_CURSOR = 0x00036006;
    public static final int GLFW_RESIZE_NWSE_CURSOR = 0x00036007;
    public static final int GLFW_RESIZE_NESW_CURSOR = 0x00036008;
    public static final int GLFW_RESIZE_ALL_CURSOR = 0x00036009;
    public static final int GLFW_NOT_ALLOWED_CURSOR = 0x0003600A;
    public static final int GLFW_HRESIZE_CURSOR = GLFW_RESIZE_EW_CURSOR;
    public static final int GLFW_VRESIZE_CURSOR = GLFW_RESIZE_NS_CURSOR;
    public static final int GLFW_HAND_CURSOR = GLFW_POINTING_HAND_CURSOR;

    public static final int GLFW_CONNECTED = 0x00040001;
    public static final int GLFW_DISCONNECTED = 0x00040002;

    public static final int GLFW_JOYSTICK_HAT_BUTTONS = 0x00050001;
    public static final int GLFW_ANGLE_PLATFORM_TYPE = 0x00050002;
    public static final int GLFW_PLATFORM = 0x00050003;
    public static final int GLFW_COCOA_CHDIR_RESOURCES = 0x00051001;
    public static final int GLFW_COCOA_MENUBAR = 0x00051002;
    public static final int GLFW_X11_XCB_VULKAN_SURFACE = 0x00052001;
    public static final int GLFW_WAYLAND_LIBDECOR = 0x00053001;

    public static final int GLFW_ANY_PLATFORM = 0x00060000;
    public static final int GLFW_PLATFORM_WIN32 = 0x00060001;
    public static final int GLFW_PLATFORM_COCOA = 0x00060002;
    public static final int GLFW_PLATFORM_WAYLAND = 0x00060003;
    public static final int GLFW_PLATFORM_X11 = 0x00060004;
    public static final int GLFW_PLATFORM_NULL = 0x00060005;

    public static final int GLFW_DONT_CARE = -1;

    public static void setWindowFocusCallback(long window, GLFWWindowFocusCallback focusCallback) {
        if (window == 0) {
            throw new IllegalArgumentException("window must not be null");
        }
        glfwSetWindowFocusCallback(Address.fromLong(window), focusCallback);
    }

    @Import(name = "glfwSetWindowFocusCallback")
    private static native void glfwSetWindowFocusCallback(Address window, GLFWWindowFocusCallback focusCallback);

    public static void setWindowMaximizeCallback(long window, GLFWWindowMaximizeCallback maximizeCallback) {
        if (window == 0) {
            throw new IllegalArgumentException("window must not be null");
        }
        glfwSetWindowMaximizeCallback(Address.fromLong(window), maximizeCallback);
    }

    @Import(name = "glfwSetWindowMaximizeCallback")
    private static native void glfwSetWindowMaximizeCallback(Address window, GLFWWindowMaximizeCallback maximizeCallback);

    public static void setWindowCloseCallback(long window, GLFWWindowCloseCallback maximizeCallback) {
        if (window == 0) {
            throw new IllegalArgumentException("window must not be null");
        }
        glfwSetWindowCloseCallback(Address.fromLong(window), maximizeCallback);
    }

    public static void setDropCallback(long window, GLFWDropCallback dropCallback) {
        if (window == 0) {
            throw new IllegalArgumentException("window must not be null");
        }
        glfwSetDropCallback(Address.fromLong(window), dropCallback);
    }

    @Import(name = "glfwSetWindowCloseCallback")
    private static native void glfwSetWindowCloseCallback(Address window, GLFWWindowCloseCallback maximizeCallback);

    public static void setWindowIconifyCallback(long window, GLFWWindowIconifyCallback maximizeCallback) {
        if (window == 0) {
            throw new IllegalArgumentException("window must not be null");
        }
        glfwSetWindowIconifyCallback(Address.fromLong(window), maximizeCallback);
    }

    @Import(name = "glfwSetWindowIconifyCallback")
    private static native void glfwSetWindowIconifyCallback(Address window, GLFWWindowIconifyCallback maximizeCallback);

    public static void setWindowRefreshCallback(long window, GLFWWindowRefreshCallback maximizeCallback) {
        if (window == 0) {
            throw new IllegalArgumentException("window must not be null");
        }
        glfwSetWindowRefreshCallback(Address.fromLong(window), maximizeCallback);
    }

    @Import(name = "glfwSetWindowRefreshCallback")
    private static native void glfwSetWindowRefreshCallback(Address window, GLFWWindowRefreshCallback maximizeCallback);

    public static String getClipboardString(long window) {
        return glfwGetClipboardString(Address.fromLong(window));
    }

    @Import(name = "glfwGetClipboardString")
    private static native String glfwGetClipboardString(Address window);

    public static void setClipboardString(long window, String string) {
        glfwSetClipboardString(Address.fromLong(window), string);
    }

    @Import(name = "glfwSetClipboardString")
    private static native void glfwSetClipboardString(Address window, String string);

    @Import(name = "glfwInit")
    private static native boolean glfwInit();

    @Import(name = "glfwCreateWindow")
    private static native Address glfwCreateWindowNative(int width, int height, String title,
                                                         Address monitor, Address share);

    @Import(name = "glfwWindowShouldClose")
    private static native boolean glfwWindowShouldClose(Address window);

    @Import(name = "glfwSwapBuffers")
    private static native void glfwSwapBuffers(Address window);

    @Import(name = "glfwPollEvents")
    private static native void glfwPollEvents();

    @Import(name = "glfwTerminate")
    private static native void glfwTerminate();

    @Import(name = "glfwMakeContextCurrent")
    private static native void glfwMakeContextCurrent(Address window);

    @Import(name = "glfwGetCurrentContext")
    private static native Address glfwGetCurrentContext();

    @Import(name = "glfwSetErrorCallback")
    private static native void glfwSetErrorCallback(GLFWErrorCallback callback);

    @Import(name = "glfwGetError")
    private static native int glfwGetError(Address description);

    @Import(name = "glfwGetVersion")
    private static native void glfwGetVersion(Address major, Address minor, Address rev);

    @Import(name = "glfwGetVersionString")
    private static native String glfwGetVersionString();

    @Import(name = "glfwExtensionSupported")
    private static native boolean glfwExtensionSupported(String extension);

    @Import(name = "glfwGetPrimaryMonitor")
    private static native Address glfwGetPrimaryMonitor();

    @Import(name = "glfwGetMonitorPos")
    private static native void glfwGetMonitorPos(Address monitor, Address xpos, Address ypos);

    @Import(name = "glfwGetMonitorWorkarea")
    private static native void glfwGetMonitorWorkarea(Address monitor, Address xpos, Address ypos, Address width, Address height);

    @Import(name = "glfwGetMonitorPhysicalSize")
    private static native void glfwGetMonitorPhysicalSize(Address monitor, Address widthMM, Address heightMM);

    @Import(name = "glfwGetMonitorContentScale")
    private static native void glfwGetMonitorContentScale(Address monitor, Address xscale, Address yscale);

    @Import(name = "glfwGetMonitorName")
    private static native String glfwGetMonitorName(Address monitor);

    @Import(name = "glfwSetMonitorUserPointer")
    private static native void glfwSetMonitorUserPointer(Address monitor, Address pointer);

    @Import(name = "glfwGetMonitorUserPointer")
    private static native Address glfwGetMonitorUserPointer(Address monitor);

    @Import(name = "glfwSetMonitorCallback")
    private static native void glfwSetMonitorCallback(Address callback);

    @Import(name = "glfwGetVideoMode")
    private static native GLFWVidMode glfwGetVideoMode(Address monitor);

    @Import(name = "glfwGetVideoModes")
    private static native GLFWVidMode[] glfwGetVideoModes(Address monitor);

    @Import(name = "glfwSetGamma")
    private static native void glfwSetGamma(Address monitor, float gamma);

    @Import(name = "glfwGetGammaRamp")
    private static native Address glfwGetGammaRamp(Address monitor);

    @Import(name = "glfwSetGammaRamp")
    private static native void glfwSetGammaRamp(Address monitor, Address ramp);

    @Import(name = "glfwDefaultWindowHints")
    private static native void glfwDefaultWindowHints();

    @Import(name = "glfwWindowHint")
    private static native void glfwWindowHint(int hint, int value);

    @Import(name = "glfwWindowHintString")
    private static native void glfwWindowHintString(int hint, String value);

    @Import(name = "glfwDestroyWindow")
    private static native void glfwDestroyWindow(Address window);

    @Import(name = "glfwWindowHintString")
    private static native void glfwWindowHintString(int hint, Address value);

    @Import(name = "glfwSetWindowTitle")
    private static native void glfwSetWindowTitle(Address window, String title);

    @Import(name = "glfwSetWindowIcon")
    private static native void glfwSetWindowIcon(Address window, int count, Address images);

    @Import(name = "glfwSetWindowPos")
    private static native void glfwSetWindowPos(Address window, int xpos, int ypos);

    @Import(name = "glfwGetWindowPos")
    private static native void glfwGetWindowPos(Address window, Address xpos, Address ypos);

    @Import(name = "glfwSetWindowSizeLimits")
    private static native void glfwSetWindowSizeLimits(Address window, int minwidth, int minheight, int maxwidth, int maxheight);

    @Import(name = "glfwSetWindowAspectRatio")
    private static native void glfwSetWindowAspectRatio(Address window, int numer, int denom);

    @Import(name = "glfwSetWindowSize")
    private static native void glfwSetWindowSize(Address window, int width, int height);

    @Import(name = "glfwGetWindowSize")
    private static native void glfwGetWindowSize(Address window, Address width, Address height);

    @Import(name = "glfwSetWindowMonitor")
    private static native void glfwSetWindowMonitor(Address window, Address monitor, int xpos, int ypos, int width, int height, int refreshRate);

    @Import(name = "glfwGetWindowMonitor")
    private static native Address glfwGetWindowMonitor(Address window);

    @Import(name = "glfwGetWindowAttrib")
    private static native int glfwGetWindowAttrib(Address window, int attrib);

    @Import(name = "glfwSetWindowAttrib")
    private static native void glfwSetWindowAttrib(Address window, int attrib, int value);

    @Import(name = "glfwPollEventsTimeout")
    private static native void glfwPollEventsTimeout(double timeout);

    @Import(name = "glfwWaitEventsTimeout")
    private static native void glfwWaitEventsTimeout(double timeout);

    @Import(name = "glfwWaitEvents")
    private static native void glfwWaitEvents();

    @Import(name = "glfwPostEmptyEvent")
    private static native void glfwPostEmptyEvent();

    @Import(name = "glfwGetInputMode")
    private static native int glfwGetInputMode(Address window, int mode);

    @Import(name = "glfwSetInputMode")
    private static native void glfwSetInputMode(Address window, int mode, int value);

    @Import(name = "glfwRawMouseMotionSupported")
    private static native boolean glfwRawMouseMotionSupported();

    @Import(name = "glfwRawMouseMotionEnable")
    private static native void glfwRawMouseMotionEnable(boolean value);

    @Import(name = "glfwGetKeyName")
    private static native String glfwGetKeyName(int key, int scancode);

    @Import(name = "glfwGetKey")
    private static native int glfwGetKey(int key);

    @Import(name = "glfwGetKeyState")
    private static native int glfwGetKeyState(int key);

    @Import(name = "glfwSetKeyCallback")
    private static native void glfwSetKeyCallback(Address window, GLFWKeyCallback callback);

    @Import(name = "glfwSetCharCallback")
    private static native void glfwSetCharCallback(Address window, GLFWCharCallback callback);

    @Import(name = "glfwSetMouseButtonCallback")
    private static native void glfwSetMouseButtonCallback(Address window, GLFWMouseButtonCallback callback);

    @Import(name = "glfwSetCursorPosCallback")
    private static native void glfwSetCursorPosCallback(Address window, GLFWCursorPosCallback callback);

    @Import(name = "glfwSetCursorEnterCallback")
    private static native void glfwSetCursorEnterCallback(Address window, GLFWCursorEnterCallback callback);

    @Import(name = "glfwSetScrollCallback")
    private static native void glfwSetScrollCallback(Address window, GLFWScrollCallback callback);

    @Import(name = "glfwSetDropCallback")
    private static native void glfwSetDropCallback(Address window, GLFWDropCallback callback);

    @Import(name = "glfwSwapInterval")
    private static native void glfwSwapInterval(int interval);

    @Import(name = "glfwGetMouseButton")
    private static native int glfwGetMouseButton(Address window, int button);

    @Import(name = "glfwGetTimerValue")
    private static native long glfwGetTimerValue();

    @Import(name = "glfwGetTimerFrequency")
    private static native long glfwGetTimerFrequency();

    @Import(name = "glfwSetTime")
    private static native void glfwSetTime(double time);

    @Import(name = "glfwSetCursorPos")
    private static native void glfwSetCursorPos(Address window, double xpos, double ypos);

    @Import(name = "glfwGetCursorPos")
    private static native void glfwGetCursorPos(Address window, Address xpos, Address ypos);

    @Import(name = "glfwSetFramebufferSizeCallback")
    private static native void glfwSetFramebufferSizeCallback(Address window, GLFWFramebufferSizeCallback callback);

    @Import(name = "glfwGetFramebufferSize")
    private static native void glfwGetFramebufferSize(Address window, Address width, Address height);

    @Import(name = "glfwGetMonitors")
    private static native Address glfwGetMonitors(Address count);

    @Import(name = "glfwInitHint")
    private static native void glfwInitHint(int hint, int value);

    public static void setKeyCallback(long window, GLFWKeyCallback keyCallback) {
        glfwSetKeyCallback(Address.fromLong(window), keyCallback);
    }

    public static void setCharCallback(long window, GLFWCharCallback charCallback) {
        glfwSetCharCallback(Address.fromLong(window), charCallback);
    }

    public static void setMouseButtonCallback(long window, GLFWMouseButtonCallback mouseButtonCallback) {
        glfwSetMouseButtonCallback(Address.fromLong(window), mouseButtonCallback);
    }

    public static void setCursorPosCallback(long window, GLFWCursorPosCallback cursorPosCallback) {
        glfwSetCursorPosCallback(Address.fromLong(window), cursorPosCallback);
    }

    public static void setCursorEnterCallback(long window, GLFWCursorEnterCallback cursorEnterCallback) {
        glfwSetCursorEnterCallback(Address.fromLong(window), cursorEnterCallback);
    }

    public static void setScrollCallback(long window, GLFWScrollCallback scrollCallback) {
        glfwSetScrollCallback(Address.fromLong(window), scrollCallback);
    }

    public static int getMouseButton(long window, int glfwMouseButton1) {
        return glfwGetMouseButton(Address.fromLong(window), glfwMouseButton1);
    }

    public static void setInputMode(long window, int mode, int value) {
        glfwSetInputMode(Address.fromLong(window), mode, value);
    }

    public static int getInputMode(long window, int mode) {
        return glfwGetInputMode(Address.fromLong(window), mode);
    }

    public static void setCursorPos(long window, int x, int y) {
        glfwSetCursorPos(Address.fromLong(window), x, y);
    }

    public static void setFramebufferSizeCallback(long window, GLFWFramebufferSizeCallback resizeCallback) {
        glfwSetFramebufferSizeCallback(Address.fromLong(window), resizeCallback);
    }

    public static void getFramebufferSize(long window, int[] width, int[] height) {
        glfwGetFramebufferSize(Address.fromLong(window), Address.ofData(width), Address.ofData(height));
    }

    public static void getWindowSize(long window, int[] width, int[] height) {
        glfwGetWindowSize(Address.fromLong(window), Address.ofData(width), Address.ofData(height));
    }

    public static long[] getMonitors() {
        int[] cnt = new int[1];
        Address addresses = glfwGetMonitors(Address.ofData(cnt));
        long[] monitors = new long[cnt[0]];
        for (int i = 0; i < monitors.length; i++) {
            monitors[i] = addresses.getAddress().toLong();
        }

        return monitors;
    }

    public static void initHint(int hint, int value) {
        glfwInitHint(hint, value);
    }

    public static void setDebugCallback(TeaCallback o) {
        Gdx.app.error("GLFW", "Setting debug callback is not supported!");
    }

    public static void maximizeWindow(long window) {
        glfwMaximizeWindow(Address.fromLong(window));
    }

    @Import(name = "glfwMaximizeWindow")
    private static native void glfwMaximizeWindow(Address address);

    public static void iconifyWindow(long window) {
        glfwIconifyWindow(Address.fromLong(window));
    }

    @Import(name = "glfwIconifyWindow")
    private static native void glfwIconifyWindow(Address address);

    public static void swapInterval(int interval) {
        glfwSwapInterval(interval);
    }

    public static long createCursor(TeaGLFWImage glfwImage, int xHotspot, int yHotspot) {
        return glfwCreateImage(glfwImage, xHotspot, yHotspot).toLong();
    }

    @Import(name = "glfwCreateImage")
    private static native Address glfwCreateImage(TeaGLFWImage glfwImage, int xHotspot, int yHotspot);

    public static void destroyCursor(long cursor) {
        glfwDestroyCursor(cursor);
    }

    @Import(name = "glfwDestroyCursor")
    private static native void glfwDestroyCursor(long cursor);

    public static long createStandardCursor(int cursor) {
        return glfwCreateStandardCursor(cursor).toLong();
    }

    @Import(name = "glfwCreateStandardCursor")
    private static native Address glfwCreateStandardCursor(int cursor);

    public static void setCursor(long window, long cursor) {
        glfwSetCursor(window, cursor);
    }

    @Import(name = "glfwSetCursor")
    private static native void glfwSetCursor(long window, long cursor);

    public static void setWindowShouldClose(long window, boolean b) {
        glfwSetWindowShouldClose(Address.fromLong(window), b);
    }

    @Import(name = "glfwSetWindowShouldClose")
    private static native void glfwSetWindowShouldClose(Address address, boolean b);

    public static void setWindowSize(long window, int width, int height) {
        glfwSetWindowSize(Address.fromLong(window), width, height);
    }

    public static void setWindowMonitor(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
        glfwSetWindowMonitor(Address.fromLong(window), Address.fromLong(monitor), xpos, ypos, width, height, refreshRate);
    }

    public static void setWindowAttrib(long window, int attrib, int value) {
        glfwSetWindowAttrib(Address.fromLong(window), attrib, value);
    }

    public static long getWindowMonitor(long window) {
        return glfwGetWindowMonitor(Address.fromLong(window)).toLong();
    }

    public static void showWindow(long window) {
        glfwShowWindow(Address.fromLong(window));
    }
    
    @Import(name = "glfwShowWindow")
    private static native void glfwShowWindow(Address address);
    
    public static void hideWindow(long window) {
        glfwHideWindow(Address.fromLong(window));
    }
    
    @Import(name = "glfwHideWindow")
    private static native void glfwHideWindow(Address address);

    public static void restoreWindow(long window) {
        glfwRestoreWindow(Address.fromLong(window));
    }
    
    @Import(name = "glfwRestoreWindow")
    private static native void glfwRestoreWindow(Address address);

    public static void focusWindow(long window) {
        glfwFocusWindow(Address.fromLong(window));
    }

    @Import(name = "glfwFocusWindow")
    private static native void glfwFocusWindow(Address address);

    public static void requestWindowAttention(long window) {
        glfwRequestWindowAttention(Address.fromLong(window));
    }

    @Import(name = "glfwRequestWindowAttention")
    private static native void glfwRequestWindowAttention(Address address);

    public static String[] dropNames(int count, long names) {
        String[] strings = new String[count];
        Address address = Address.fromLong(names);
        for (int i = 0; i < count; i++) {
            Address strAddress = address.getAddress();
            address = address.add(Address.sizeOf());
            strings[i] = Strings.fromC(strAddress);
        }

        return strings;
    }

    public static abstract class GLFWErrorCallback extends Function {
        public static GLFWErrorCallback createPrint() {
            return Function.get(GLFWErrorCallback.class, TeaNativeCallbacks.class, "onError");
        }

        public abstract void invoke(int error, String description);
    }

    public static abstract class GLFWKeyCallback extends Function {
        public abstract void invoke(Address window, int key, int scancode, int action, int mods);
    }

    public static abstract class GLFWCharCallback extends Function {
        public abstract void invoke(Address window, int codepoint);
    }

    public static abstract class GLFWScrollCallback extends Function {
        public abstract void invoke(Address window, double scrollX, double scrollY);
    }

    public static abstract class GLFWMouseButtonCallback extends Function {
        public abstract void invoke(Address window, int button, int action, int mods);
    }

    public static abstract class GLFWCursorPosCallback extends Function {
        public abstract void invoke(Address window, double xpos, double ypos);
    }

    public static abstract class GLFWCursorEnterCallback extends Function {
        public abstract void invoke(Address window, int entered, Address cursorPos);
    }

    public static abstract class GLFWMonitorCallback extends Function {
        public abstract void invoke(Address monitor, int event);
    }

    public static abstract class GLFWWindowFocusCallback extends Function {
        public abstract void invoke(Address window, boolean focused);
    }

    public static abstract class GLFWWindowIconifyCallback extends Function {
        public abstract void invoke(Address window, boolean iconified);
    }

    public static abstract class GLFWWindowMaximizeCallback extends Function {
        public abstract void invoke(Address window, boolean maximized);
    }

    public static abstract class GLFWWindowRefreshCallback extends Function {
        public abstract void invoke(Address window);
    }

    public static abstract class GLFWWindowCloseCallback extends Function {
        public abstract void invoke(Address window);
    }

    public static abstract class GLFWWindowPosCallback extends Function {
        public abstract void invoke(Address window, int xpos, int ypos);
    }

    public static abstract class GLFWWindowSizeCallback extends Function {
        public abstract void invoke(Address window, int width, int height);
    }

    public static abstract class GLFWWindowContentScaleCallback extends Function {
        public abstract void invoke(Address window, float xscale, float yscale);
    }

    public static abstract class GLFWDropCallback extends Function {

        public abstract void invoke(Address window, int count, long names);
    }

    public static abstract class GLFWFramebufferSizeCallback extends Function {
        public abstract void invoke(Address window, int width, int height);
    }

    public static boolean init() {
        return glfwInit();
    }

    public static long createWindow(int width, int height, String title, long monitor, long share) {
        return glfwCreateWindowNative(width, height, title,
                Address.fromLong(monitor), Address.fromLong(share)).toLong();
    }

    public static boolean windowShouldClose(long window) {
        return glfwWindowShouldClose(Address.fromLong(window));
    }

    public static void swapBuffers(long window) {
        glfwSwapBuffers(Address.fromLong(window));
    }

    public static void pollEvents() {
        glfwPollEvents();
    }

    public static void terminate() {
        glfwTerminate();
    }

    public static void makeContextCurrent(long window) {
        glfwMakeContextCurrent(Address.fromLong(window));
    }

    public static long getCurrentContext() {
        return glfwGetCurrentContext().toLong();
    }

    public static void getVersion(int[] major, int[] minor, int[] rev) {
        glfwGetVersion(Address.ofData(major), Address.ofData(minor), Address.ofData(rev));
    }

    public static String getVersionString() {
        return glfwGetVersionString();
    }

    public static boolean extensionSupported(String extension) {
        return glfwExtensionSupported(extension);
    }

    public static long getPrimaryMonitor() {
        return glfwGetPrimaryMonitor().toLong();
    }

    public static void getMonitorPos(long monitor, int[] xpos, int[] ypos) {
        glfwGetMonitorPos(Address.fromLong(monitor), Address.ofData(xpos), Address.ofData(ypos));
    }

    public static void getMonitorWorkarea(long monitor, int[] xpos, int[] ypos, int[] width, int[] height) {
        glfwGetMonitorWorkarea(Address.fromLong(monitor), Address.ofData(xpos), Address.ofData(ypos), Address.ofData(width), Address.ofData(height));
    }

    public static void getMonitorPhysicalSize(long monitor, int[] widthMM, int[] heightMM) {
        glfwGetMonitorPhysicalSize(Address.fromLong(monitor), Address.ofData(widthMM), Address.ofData(heightMM));
    }

    public static void getMonitorContentScale(long monitor, int[] xscale, int[] yscale) {
        glfwGetMonitorContentScale(Address.fromLong(monitor), Address.ofData(xscale), Address.ofData(yscale));
    }

    public static String getMonitorName(long monitor) {
        return glfwGetMonitorName(Address.fromLong(monitor));
    }

    public static void setMonitorUserPointer(long monitor, long pointer) {
        glfwSetMonitorUserPointer(Address.fromLong(monitor), Address.fromLong(pointer));
    }

    public static long getMonitorUserPointer(long monitor) {
        return glfwGetMonitorUserPointer(Address.fromLong(monitor)).toLong();
    }

    public static void setMonitorCallback(long callback) {
        glfwSetMonitorCallback(Address.fromLong(callback));
    }

    public static GLFWVidMode getVideoMode(long monitor) {
        return glfwGetVideoMode(Address.fromLong(monitor));
    }

    public static class GLFWVidMode extends Structure {
        public int width;
        public int height;
        public int redBits;
        public int greenBits;
        public int blueBits;
        public int refreshRate;
    }

    public static GLFWVidMode[] getVideoModes(long monitor) {
        return glfwGetVideoModes(Address.fromLong(monitor));
    }

    public static void setGamma(long monitor, float gamma) {
        glfwSetGamma(Address.fromLong(monitor), gamma);
    }

    public static long getGammaRamp(long monitor) {
        return glfwGetGammaRamp(Address.fromLong(monitor)).toLong();
    }

    public static void setGammaRamp(long monitor, long ramp) {
        glfwSetGammaRamp(Address.fromLong(monitor), Address.fromLong(ramp));
    }

    public static void defaultWindowHints() {
        glfwDefaultWindowHints();
    }

    public static void windowHint(int hint, int value) {
        glfwWindowHint(hint, value);
    }

    public static void windowHintString(int hint, String value) {
        glfwWindowHintString(hint, value);
    }

    public static void windowHintString(int hint, long value) {
        glfwWindowHintString(hint, Address.fromLong(value));
    }

    public static void destroyWindow(long window) {
        glfwDestroyWindow(Address.fromLong(window));
    }

    public static void setWindowTitle(long window, String title) {
        glfwSetWindowTitle(Address.fromLong(window), title);
    }

    public static void setWindowIcon(long window, int count, long images) {
        glfwSetWindowIcon(Address.fromLong(window), count, Address.fromLong(images));
    }

    public static void setWindowPos(long window, int xpos, int ypos) {
        glfwSetWindowPos(Address.fromLong(window), xpos, ypos);
    }

    public static void getWindowPos(long window, int[] xpos, int[] ypos) {
        glfwGetWindowPos(Address.fromLong(window), Address.ofData(xpos), Address.ofData(ypos));
    }

    public static void setWindowSizeLimits(long window, int minwidth, int minheight, int maxwidth, int maxheight) {
        glfwSetWindowSizeLimits(Address.fromLong(window), minwidth, minheight, maxwidth, maxheight);
    }

    public static void setErrorCallback(GLFWErrorCallback glfwErrorCallback) {
        glfwSetErrorCallback(glfwErrorCallback);
    }

}