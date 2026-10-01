# Controllers example

The portable controller demo lives in `core`, and its shared font lives in `assets`. Each platform leaf consumes those assets and selects the matching gdx-controllers backend or gdx-teavm extension.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL3 | `:examples:controllers:platforms:desktop:lwjgl3` | `controllers_desktop_run` |
| Desktop TeaVM C builder | `:examples:controllers:platforms:desktop:teavm-c:builder` | `controllers_desktop_c_debug_build` |
| Desktop TeaVM C plugin | `:examples:controllers:platforms:desktop:teavm-c:plugin` | `gdx_teavm_glfw_debug_run` |
| Web builder | `:examples:controllers:platforms:web:builder` | `controllers_web_run` |
| Web plugin | `:examples:controllers:platforms:web:plugin` | `gdx_teavm_web_js_run` |
| Android | `:examples:controllers:platforms:android` | `assembleDebug` |
| iOS | `:examples:controllers:platforms:ios` | `gdx_teavm_ios_build_simulator` |

## Desktop TeaVM C plugin

Run the debug build:

```shell
./gradlew :examples:controllers:platforms:desktop:teavm-c:plugin:gdx_teavm_glfw_debug_run
```

Run the release build:

```shell
./gradlew :examples:controllers:platforms:desktop:teavm-c:plugin:gdx_teavm_glfw_release_run
```

Use the corresponding `_build` task to compile without launching, or `_generate` to generate C sources only. Outputs are under the plugin project's `build/dist/glfw/debug` for debug and `build/dist/glfw/release` for release. Both targets use the shared controller demo and font assets with the GLFW controller extension.
