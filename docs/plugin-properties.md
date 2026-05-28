# Gradle Plugin Property Reference

This page lists the properties available in the `gdxTeaVM` Gradle extension.

The plugin creates tasks only for target blocks that are declared. Declaring `js {}` or `wasm {}` adds `backend-web`; declaring `glfw {}` adds `backend-glfw`; declaring `psp {}` adds `backend-psp`. Backend dependencies are added to both Java `implementation` and TeaVM's generation classpath.

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
| `psp { ... }` | `backend-psp` | `gdx_teavm_psp_generate`, `gdx_teavm_psp_build` |
| `ios { ... }` | `backend-ios` | `gdx_teavm_ios_generate`, `gdx_teavm_ios_prepare_angle`, `gdx_teavm_ios_init_xcode`, `gdx_teavm_ios_open_xcode`, `gdx_teavm_ios_build_simulator`, `gdx_teavm_ios_run_simulator`, `gdx_teavm_ios_regenerate_xcode` |

## Common TeaVM Target Properties

The `js {}` and `wasm {}` blocks expose these TeaVM Gradle properties directly. The native `glfw {}` and `psp {}` blocks expose the matching TeaVM C settings and apply only the selected backend to TeaVM's C task.

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
| JS | `build/dist/web` | `webapp` | `app.js` |
| Wasm | `build/dist/wasm` | `webapp` | `app.wasm` |
| GLFW | `build/dist/glfw` | `c/src` | `app` |
| PSP | `build/dist/psp` | `c/src` | `app` |
| iOS | `build/dist/ios` | `c/src` | `app` |

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

Native targets use TeaVM C output. Declare `glfw {}`, `psp {}`, or `ios {}` to create backend tasks and configure that target's TeaVM C settings. Native targets normally need their own launcher classes because each starts a different backend application type.

### Native Target Properties

These properties exist in `glfw {}` and `psp {}`.

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

Plugin GLFW build and run tasks use the `buildType` configured in `glfw {}`.

| Task | Build type |
| --- | --- |
| `gdx_teavm_glfw_build` | `glfw.buildType` |
| `gdx_teavm_glfw_run` | `glfw.buildType` |

### PSP Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `debugMemory` | `Property<Boolean>` | `false` | Enables PSP memory debug support in generated native glue code. |
| `autoExecuteBuild` | `Property<Boolean>` | `false` | Lets the backend execute the generated PSP build script after generating sources. |

Example:

```kotlin
gdxTeaVM {
    psp {
        mainClass.set("com.example.game.teavm.PspLauncher")
        optimization.set(OptimizationLevel.NONE)
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        debugMemory.set(true)
    }
}
```

### iOS Properties

The iOS Xcode project can be initialized separately from TeaVM C generation. This lets a project create and commit Xcode files once, preserve signing, teams, capabilities, icons, and other manual project settings, then regenerate only TeaVM C/assets during day-to-day Java changes.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `xcodeProjectName` | `Property<String>` | `GdxTeaVMIOSSpike` | Generated Xcode project and app target name. |
| `xcodeProjectDir` | `DirectoryProperty` | `[outputDir]/xcode` | Directory containing the `.xcodeproj` and Swift sources. Set this outside `build` when the Xcode project should be committed. |
| `graphicsApi` | `Property<String>` | `angle` | Graphics implementation used by the generated Xcode project. Use `angle` for MetalANGLEKit-backed GLES over Metal, or `gles` for Apple's native OpenGL ES / GLKit path. Can also be set with Gradle property `gdx.teavm.ios.graphicsApi`. |
| `xcodeScheme` | `Property<String>` | `GdxTeaVMIOSSpike` | Xcode scheme used by simulator build tasks. |
| `xcodeConfiguration` | `Property<String>` | `Debug` | Xcode build configuration used by simulator build tasks. |
| `simulatorDevice` | `Property<String>` | `iPhone 12 Pro` | Simulator name or UDID used by `gdx_teavm_ios_run_simulator`. |
| `bundleIdentifier` | `Property<String>` | `com.github.xpenatan.gdxteavm.ios.spike` | Bundle identifier used by simulator install/launch. |
| `xcodeDerivedDataPath` | `DirectoryProperty` | `build/xcode-derived/ios` | Derived data directory used by Xcode build tasks. |
| `openSimulator` | `Property<Boolean>` | `true` | Opens Simulator.app before install/launch when true. |
| `overwriteXcodeProject` | `Property<Boolean>` | `false` | Rewrites the generated Xcode project during Xcode initialization. Keep false for normal development. |

Example:

```kotlin
gdxTeaVM {
    ios {
        mainClass.set("com.example.game.teavm.IosLauncher")
        graphicsApi.set("angle")
        xcodeProjectDir.set(layout.projectDirectory.dir("xcode"))
        bundleIdentifier.set("com.example.game")
        simulatorDevice.set("iPhone 15")
    }
}
```

iOS workflow:

| Task | Purpose |
| --- | --- |
| `gdx_teavm_ios_prepare_angle` | Downloads and extracts the pinned MetalANGLEKit framework bundle when `ios.graphicsApi` is `angle`. |
| `gdx_teavm_ios_init_xcode` | Creates the Xcode project if missing and preserves an existing project. Does not run TeaVM C generation. |
| `gdx_teavm_ios_generate` | Regenerates TeaVM C/assets only. |
| `gdx_teavm_ios_open_xcode` | Ensures the Xcode project exists, then opens it. |
| `gdx_teavm_ios_build_simulator` | Ensures the Xcode project exists, generates C/assets, then builds the project for the simulator. |
| `gdx_teavm_ios_run_simulator` | Generates C/assets, builds, installs, and launches on the simulator. |
| `gdx_teavm_ios_regenerate_xcode` | Rewrites the Xcode project from the template. This can overwrite manual Xcode edits. |

## Complete Multi-Target Example

```kotlin
import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm") version "-SNAPSHOT"
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:1.14.1")
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

    psp {
        mainClass.set("com.example.game.teavm.PspLauncher")
        optimization.set(OptimizationLevel.NONE)
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        obfuscated.set(false)
        debugMemory.set(false)
    }
}
```
