# Basic example

Portable test applications live in `core`, shared runtime assets live in `assets`, and each runnable launcher lives under `platforms`.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL3 | `:examples:basic:platforms:desktop:lwjgl3` | `basic_desktop_run` |
| Desktop TeaVM C builder | `:examples:basic:platforms:desktop:teavm-c:builder` | `basic_desktop_c_debug_build` |
| Desktop TeaVM C plugin | `:examples:basic:platforms:desktop:teavm-c:plugin` | `gdx_teavm_glfw_build` |
| Web builder | `:examples:basic:platforms:web:builder` | `basic_web_run` |
| Web plugin | `:examples:basic:platforms:web:plugin` | `gdx_teavm_web_js_run` |
| Android | `:examples:basic:platforms:android` | `assembleDebug` |
| iOS | `:examples:basic:platforms:ios` | `gdx_teavm_ios_build_simulator` |

See the [manual TeaVM C guide](platforms/desktop/teavm-c/builder/README.md) for native toolchain requirements.

The basic web plugin build also demonstrates shared `webDefaults {}` plus named JS and Wasm release variants. Their build tasks are `gdx_teavm_web_js_release_build` and `gdx_teavm_web_wasm_release_build`. The TeaVM C plugin build demonstrates `nativeDefaults {}` and a named GLFW release variant with `gdx_teavm_glfw_release_generate`, `gdx_teavm_glfw_release_build`, and `gdx_teavm_glfw_release_run`.
