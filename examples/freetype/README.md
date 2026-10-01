# FreeType example

The portable FreeType demo lives in `core`. Shared font assets live in `assets` and are consumed directly by each runnable platform leaf.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL3 | `:examples:freetype:platforms:desktop:lwjgl3` | `freetype_desktop_run` |
| Desktop TeaVM C builder | `:examples:freetype:platforms:desktop:teavm-c:builder` | `freetype_desktop_c_debug_build` |
| Desktop TeaVM C plugin | `:examples:freetype:platforms:desktop:teavm-c:plugin` | `gdx_teavm_glfw_debug_run` |
| Web builder | `:examples:freetype:platforms:web:builder` | `freetype_web_run` |
| Web plugin | `:examples:freetype:platforms:web:plugin` | `gdx_teavm_web_js_run` |
| Android | `:examples:freetype:platforms:android` | `assembleDebug` |
| iOS | `:examples:freetype:platforms:ios` | `gdx_teavm_ios_build_simulator` |

## Desktop TeaVM C plugin

Run the debug build:

```shell
./gradlew :examples:freetype:platforms:desktop:teavm-c:plugin:gdx_teavm_glfw_debug_run
```

Run the release build:

```shell
./gradlew :examples:freetype:platforms:desktop:teavm-c:plugin:gdx_teavm_glfw_release_run
```

Use the corresponding `_build` task to compile without launching, or `_generate` to generate C sources only. Outputs are under the plugin project's `build/dist/glfw/debug` and `build/dist/glfw/release`. Both targets use the shared FreeType demo and font assets. The `extensions:c:gdx-freetype-c` dependency supplies the native bridge; the generated CMake project fetches and links FreeType.
