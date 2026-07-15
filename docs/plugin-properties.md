# Gradle Plugin Property Reference

This page lists the properties available in the `gdxTeaVM` Gradle extension.

The plugin creates tasks only for target blocks that are declared. Declaring `js {}` or `wasm {}` adds `backend-web`; declaring `glfw {}` adds `backend-glfw`; declaring experimental `ios {}` adds `backend-ios`; declaring `android {}` adds `backend-android`. Regular Java targets add their backend to both `implementation` and TeaVM's generation classpath. Android modules use the dedicated TeaVM configuration and generated runtime bridge sources described in the usage guide.

## Minimal Shape

```kotlin
plugins {
    id("com.github.xpenatan.gdx-teavm") version "-SNAPSHOT"
}

gdxTeaVM {
    assets("assets")

    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
    }
}
```

## Shared Properties

These properties live directly inside `gdxTeaVM { ... }`.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `assets` | `ConfigurableFileCollection` | empty | Local files or directories copied as libGDX internal assets. Directories are copied recursively. |
| `classpathAssets` | `ListProperty<String>` | empty | Classpath resource roots copied as libGDX classpath assets. |
| `reflection` | `ListProperty<String>` | empty | Reflection class names or package patterns to preserve. |
| `reflectionEnabled` | `Property<Boolean>` | `true` | Enables gdx-teavm reflection metadata generation. |
| `reflectionDefaults` | `Property<Boolean>` | `true` | Adds default reflection configuration required by common libGDX runtime types. |
| `reflectionScan` | `Property<Boolean>` | `true` | Scans reachable classes and configured packages for reflection metadata. |
| `reflectionDebug` | `Property<Boolean>` | `false` | Prints extra reflection metadata diagnostics during TeaVM generation. |

Helper methods:

| Method | Purpose |
| --- | --- |
| `assets(vararg paths)` | Adds local asset files or directories to `assets`. |
| `classpathAssets(vararg paths)` | Adds classpath resource roots to `classpathAssets`. |
| `reflection(vararg patterns)` | Adds reflection class names or package patterns to `reflection`. |

Example:

```kotlin
gdxTeaVM {
    assets("assets", "../shared-assets")
    classpathAssets("com/example/game/shaders")
    reflection("com.example.game.save**")
}
```

## Target Declaration Methods

| Block | Backend | Tasks created |
| --- | --- | --- |
| `js { ... }` | `backend-web` | `gdx_teavm_web_js_build`, `gdx_teavm_web_js_run` |
| `wasm { ... }` | `backend-web` | `gdx_teavm_web_wasm_build`, `gdx_teavm_web_wasm_run` |
| `glfw { ... }` | `backend-glfw` | `gdx_teavm_glfw_generate`, `gdx_teavm_glfw_build`, `gdx_teavm_glfw_run` |
| `ios { ... }` | `backend-ios` | `gdx_teavm_ios_generate`, `gdx_teavm_ios_prepare_angle`, `gdx_teavm_ios_init_xcode`, `gdx_teavm_ios_regenerate_xcode`, `gdx_teavm_ios_open_xcode`, `gdx_teavm_ios_build_simulator`, `gdx_teavm_ios_run_simulator` |
| `android { ... }` | `backend-android` | `gdx_teavm_android_generate` |

## Common TeaVM Target Properties

The `js {}` and `wasm {}` blocks expose these TeaVM Gradle properties directly. Native target blocks expose the matching TeaVM C settings and apply only the selected backend to TeaVM's C task.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `outputDir` | `DirectoryProperty` | target-specific | Root directory for generated files. |
| `mainClass` | `Property<String>` | none | Fully qualified launcher class used as TeaVM main class. This must be set for every target you build. |
| `relativePathInOutputDir` | `Property<String>` | web `webapp`, native `c/src` | Path inside `outputDir` where TeaVM writes generated files. |
| `optimization` | `Property<OptimizationLevel>` | JS `BALANCED`, Wasm/native `AGGRESSIVE` | TeaVM optimization level. |
| `debugInformation` | `Property<Boolean>` | `false` | Includes TeaVM debug information when supported by the target. |
| `fastGlobalAnalysis` | `Property<Boolean>` | `false` | Enables faster TeaVM global analysis, trading precision for speed. |
| `outOfProcess` | `Property<Boolean>` | `false` | Runs TeaVM compilation out of the Gradle process when supported by TeaVM. |
| `processMemory` | `Property<Int>` | `512` | Memory limit in megabytes for out-of-process TeaVM compilation. |
| `preservedClasses` | `ListProperty<String>` | empty | Classes TeaVM should preserve from aggressive removal or renaming. |

Target-specific default output:

| Target | `outputDir` | `relativePathInOutputDir` | `targetFileName` |
| --- | --- | --- | --- |
| JS | `build/dist/js` | `webapp` | `app.js` |
| Wasm | `build/dist/wasm` | `webapp` | `app.wasm` |
| GLFW | `build/dist/glfw` | `c/src` | `app` |
| iOS | `build/dist/ios` | `c/src` | `app` |
| Android | `build/generated/gdx-teavm/android` | `c/src` | `app` |

## Web Targets

Web targets use `backend-web` and can be built as JavaScript or Wasm. JS and Wasm usually share the same launcher class because both start `WebApplication`.

```kotlin
gdxTeaVM {
    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
    }

    wasm {
        mainClass.set("com.example.game.teavm.WebLauncher")
    }
}
```

### Web Common Properties

These properties exist inside `js {}` and `wasm {}` only.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `webappEnabled` | `Property<Boolean>` | `true` | Enables generated gdx-teavm web app files around web TeaVM output. |
| `entryPointName` | `Property<String>` | `main` | Entry point function name called by the generated web app. |
| `mainClassArgs` | `Property<String>` | empty string | Arguments passed by generated web app to the TeaVM entry point. Use JavaScript array element syntax without surrounding brackets. |
| `htmlTitle` | `Property<String>` | `gdx-teavm` | Browser document title used by generated `index.html`. |
| `htmlWidth` | `Property<Int>` | `800` | Initial canvas width written to generated `index.html`. |
| `htmlHeight` | `Property<Int>` | `600` | Initial canvas height written to generated `index.html`. |
| `logoPath` | `Property<String>` | `startup-logo.png` | Classpath resource path for the loading logo copied into web app assets. |
| `copyLoadingAsset` | `Property<Boolean>` | `true` | Copies `logoPath` into the generated web app when true. |
| `serverPort` | `Property<Int>` | Gradle property `teavmPluginPort`, otherwise `8080` | Port used by this target's plugin web run task. |

Example:

```kotlin
gdxTeaVM {
    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
        htmlTitle.set("Example Game")
        htmlWidth.set(1280)
        htmlHeight.set(720)
        serverPort.set(9090)
    }
}
```

### JavaScript Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `targetFileName` | `Property<String>` | `app.js` | Name of the generated JavaScript output file. |
| `obfuscated` | `Property<Boolean>` | `true` | Minifies and renames generated JavaScript output. |
| `strict` | `Property<Boolean>` | `false` | Enables TeaVM strict JavaScript generation checks. |

Example:

```kotlin
gdxTeaVM {
    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
        strict.set(false)
    }
}
```

### Wasm Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `targetFileName` | `Property<String>` | `app.wasm` | Name of the generated Wasm output file. |
| `obfuscated` | `Property<Boolean>` | `true` | Minifies and renames generated Wasm runtime support output. |
| `strict` | `Property<Boolean>` | `false` | Enables TeaVM strict Wasm generation checks. |
| `copyRuntime` | `Property<Boolean>` | `true` | Copies TeaVM's Wasm runtime JavaScript next to the generated `.wasm` file. |
| `modularRuntime` | `Property<Boolean>` | `false` | Copies TeaVM's ES module Wasm runtime instead of the global script runtime. The generated gdx-teavm web app expects the default global runtime. |

Example:

```kotlin
gdxTeaVM {
    wasm {
        mainClass.set("com.example.game.teavm.WebLauncher")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(false)
    }
}
```

## Native Targets

Native targets use TeaVM C output. Declare `glfw {}` for the desktop backend, experimental `ios {}` for WIP native payloads, or `android {}` in an Android application module. Native targets normally need their own launcher classes because each starts a different backend application type.

### Native Target Properties

These properties exist in native target blocks.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `mainClass` | `Property<String>` | none | Native launcher class used as the TeaVM C main class. |
| `outputDir` | `DirectoryProperty` | target-specific | Root directory for generated files for this native backend. |
| `relativePathInOutputDir` | `Property<String>` | `c/src` | Path inside `outputDir` where TeaVM writes C source files. |
| `targetFileName` | `Property<String>` | `app` | Name of the generated native target. |
| `releasePath` | `DirectoryProperty` | `[outputDir]/c/release` | Directory where native runtime assets and build output support files are prepared. |
| `optimization` | `Property<OptimizationLevel>` | `AGGRESSIVE` | TeaVM C optimization level. |
| `debugInformation` | `Property<Boolean>` | `false` | Includes TeaVM C debug information when supported. |
| `fastGlobalAnalysis` | `Property<Boolean>` | `false` | Enables faster TeaVM global analysis, trading precision for speed. |
| `outOfProcess` | `Property<Boolean>` | `false` | Runs TeaVM C compilation out of the Gradle process when supported by TeaVM. |
| `processMemory` | `Property<Int>` | `512` | Memory limit in megabytes for out-of-process TeaVM compilation. |
| `preservedClasses` | `ListProperty<String>` | empty | Classes TeaVM should preserve from aggressive removal or renaming. |
| `minHeapSizeMb` | `Property<Int>` | `4` | Initial native heap size in megabytes. |
| `maxHeapSizeMb` | `Property<Int>` | `128` | Maximum native heap size in megabytes. |
| `heapDump` | `Property<Boolean>` | `false` | Enables TeaVM heap dump support for native output when supported. |
| `shortFileNames` | `Property<Boolean>` | `true` | Asks TeaVM to generate shorter C file names, useful for native toolchains with path limits. |
| `obfuscated` | `Property<Boolean>` | `true` | Obfuscates generated native C symbols. |

### GLFW Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `buildType` | `Property<String>` | `Debug` | Native build type used by generated GLFW build scripts, typically `Debug` or `Release`. |
| `buildExecutable` | `Property<Boolean>` | `false` | Lets the backend invoke the generated GLFW build script when true. |
| `runExecutable` | `Property<Boolean>` | `false` | Lets the backend run the generated GLFW executable after building when true. |
| `consoleLog` | `Property<Boolean>` | `false` | Opens or attaches native console logging for GLFW run tasks when supported by the platform. |
| `cmakeDefinitions` | `MapProperty<String, String>` | empty | Advanced ordered CMake cache definitions passed to the generated GLFW configure scripts. Use `cmakeDefinition(name, value)` to add one definition. |

Example:

```kotlin
gdxTeaVM {
    glfw {
        mainClass.set("com.example.game.teavm.GlfwLauncher")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        buildType.set("Release")
        consoleLog.set(true)
    }
}
```

`cmakeDefinitions` is a low-level integration hook for arbitrary CMake cache entries. Libraries should expose typed settings for user-facing choices instead of requiring consumers to know internal cache keys or values.

Plugin GLFW build and run tasks use the `buildType` configured in `glfw {}`.

| Task | Build type |
| --- | --- |
| `gdx_teavm_glfw_build` | `glfw.buildType` |
| `gdx_teavm_glfw_run` | `glfw.buildType` |

### iOS Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `xcodeProjectName` | `Property<String>` | `GdxTeaVMIOSSpike` | Generated Xcode project name. |
| `xcodeProjectDir` | `DirectoryProperty` | `[outputDir]/xcode` | Directory containing the generated Xcode project and Swift sources. |
| `graphicsApi` | `Property<String>` | Gradle property `gdx.teavm.ios.graphicsApi`, otherwise `angle` | Generated Xcode graphics bridge. Supported values are `angle` and `gles`. |
| `xcodeScheme` | `Property<String>` | `GdxTeaVMIOSSpike` | Xcode scheme used by simulator build tasks. |
| `xcodeConfiguration` | `Property<String>` | `Debug` | Xcode build configuration used by simulator build tasks. |
| `simulatorDevice` | `Property<String>` | `iPhone 12 Pro` | Simulator device name or UDID used by `gdx_teavm_ios_run_simulator`. |
| `bundleIdentifier` | `Property<String>` | `com.github.xpenatan.gdxteavm.ios.spike` | App bundle identifier used by `gdx_teavm_ios_run_simulator`. |
| `xcodeDerivedDataPath` | `DirectoryProperty` | `build/xcode-derived/ios` | Derived data directory used by simulator build and run tasks. |
| `openSimulator` | `Property<Boolean>` | `true` | Opens Simulator.app when running the simulator task. |
| `overwriteXcodeProject` | `Property<Boolean>` | Gradle property `gdx.teavm.ios.xcode.overwrite`, otherwise `false` | Rewrites the generated Xcode project during Xcode initialization when true. |

iOS Xcode tasks are experimental and require macOS. The default `angle` graphics API downloads a pinned MetalANGLEKit framework bundle; set `graphicsApi` to `gles` to generate the older OpenGL ES / GLKit project.

## Complete Multi-Target Example

```kotlin
import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm") version "-SNAPSHOT"
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:1.14.2")
    implementation(project(":core"))
}

gdxTeaVM {
    assets("assets")
    reflection("com.example.game.save**")

    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
        htmlTitle.set("Example Game")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }

    wasm {
        mainClass.set("com.example.game.teavm.WebLauncher")
        htmlTitle.set("Example Game")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(false)
    }

    glfw {
        mainClass.set("com.example.game.teavm.GlfwLauncher")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        obfuscated.set(false)
        consoleLog.set(false)
    }

}
```
