# Basic example

Portable test applications live in `core`, shared runtime assets live in `assets`, and each runnable launcher lives under `platforms`.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL3 | `:examples:basic:platforms:desktop:lwjgl3` | `basic_desktop_run` |
| Desktop GraalVM Native Image | `:examples:basic:platforms:desktop:graalvm` | `basic_desktop_graalvm_run` |
| Desktop TeaVM C builder | `:examples:basic:platforms:desktop:teavm-c:builder` | `basic_desktop_c_debug_build` |
| Desktop TeaVM C plugin | `:examples:basic:platforms:desktop:teavm-c:plugin` | `gdx_teavm_glfw_build` |
| Web builder | `:examples:basic:platforms:web:builder` | `basic_web_run` |
| Web plugin | `:examples:basic:platforms:web:plugin` | `gdx_teavm_web_js_run` |
| Android | `:examples:basic:platforms:android` | `assembleDebug` |
| iOS | `:examples:basic:platforms:ios` | `gdx_teavm_ios_build_simulator` |

See the [manual TeaVM C guide](platforms/desktop/teavm-c/builder/README.md) for native toolchain requirements.

## GraalVM speed comparison

The GraalVM launcher runs the same `SpriteBatchTest` as the regular LWJGL3 and TeaVM C launchers. Each launcher disables VSync and removes the foreground FPS cap, while `SpriteBatchTest` prints a one-second FPS sample to the console.

Point `JAVA_HOME` and `GRAALVM_HOME` at a GraalVM JDK that includes Native Image, then build or run the optimized native executable:

```bash
./gradlew :examples:basic:platforms:desktop:graalvm:basic_desktop_graalvm_build
./gradlew :examples:basic:platforms:desktop:graalvm:basic_desktop_graalvm_run
```

The executable and copied assets are written to `platforms/desktop/graalvm/build/native/nativeCompile`. For a JVM-side launcher check, or to compare the GraalVM JIT when Gradle itself runs on GraalVM, use `basic_desktop_graalvm_jvm_run`.

For a quick side-by-side run of the same workload, compare the one-second FPS samples printed by these tasks after allowing each runtime to warm up:

```bash
./gradlew :examples:basic:platforms:desktop:lwjgl3:basic_desktop_run
./gradlew :examples:basic:platforms:desktop:graalvm:basic_desktop_graalvm_run
./gradlew :examples:basic:platforms:desktop:teavm-c:plugin:gdx_teavm_glfw_release_aggressive_run
```

Native Image builds for the current operating system only. This comparison build also uses `-march=native`, so its executable targets the build machine's CPU rather than a broadly compatible distribution target. On Windows, run Gradle from a Visual Studio x64 Native Tools environment if Native Image cannot find the C++ toolchain.

The basic web plugin build also demonstrates shared `webDefaults {}` plus named JS and Wasm release variants. Their build tasks are `gdx_teavm_web_js_release_build` and `gdx_teavm_web_wasm_release_build`. The TeaVM C plugin build demonstrates `nativeDefaults {}` and a named GLFW release variant with `gdx_teavm_glfw_release_generate`, `gdx_teavm_glfw_release_build`, and `gdx_teavm_glfw_release_run`.
