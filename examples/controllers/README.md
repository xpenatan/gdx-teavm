# Controllers example

The portable controller demo lives in `core`. Each platform leaf selects the matching gdx-controllers backend or gdx-teavm extension.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL3 | `:examples:controllers:platforms:desktop:lwjgl3` | `controllers_desktop_run` |
| Desktop TeaVM C builder | `:examples:controllers:platforms:desktop:teavm-c:builder` | `controllers_desktop_c_debug_build` |
| Web builder | `:examples:controllers:platforms:web:builder` | `controllers_web_run` |
| Web plugin | `:examples:controllers:platforms:web:plugin` | `gdx_teavm_web_js_run` |
| Android | `:examples:controllers:platforms:android` | `assembleDebug` |
| iOS | `:examples:controllers:platforms:ios` | `gdx_teavm_ios_build_simulator` |
