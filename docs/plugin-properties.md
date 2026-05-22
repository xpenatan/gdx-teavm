# Gradle Plugin Property Reference

This page lists the properties available in the `gdxTeaVM` Gradle extension.

The plugin creates tasks only for target blocks that are declared. Declaring `js {}` or `wasm {}` adds `backend-web`; declaring `glfw {}` adds `backend-glfw`; declaring `psp {}` adds `backend-psp`. These backend dependencies are added to both Java `implementation` and TeaVM's generation classpath.

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

## Common Target Properties

Every target block has these properties.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `outputDir` | `DirectoryProperty` | target-specific | Root directory for generated files for this target. |
| `mainClass` | `Property<String>` | none | Fully qualified launcher class used as TeaVM main class. This must be set for every target you build. |
| `relativePathInOutputDir` | `Property<String>` | web `webapp`, native `c/src` | Path inside `outputDir` where TeaVM writes generated target files. |
| `targetFileName` | `Property<String>` | target-specific | Name of the main generated output file, such as `app.js`, `app.wasm`, or `app`. |
| `optimization` | `Property<OptimizationLevel>` | JS `BALANCED`, Wasm/GLFW/PSP `AGGRESSIVE` | TeaVM optimization level. |
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

Native targets use TeaVM C output. GLFW and PSP normally need their own launcher classes because each starts a different backend application type.

### Native Common Properties

These properties are shared by `glfw {}` and `psp {}`.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `minHeapSizeMb` | `Property<Int>` | `4` | Initial native heap size in megabytes. |
| `maxHeapSizeMb` | `Property<Int>` | `128` | Maximum native heap size in megabytes. |
| `heapDump` | `Property<Boolean>` | `false` | Enables TeaVM heap dump support for native output when supported. |
| `shortFileNames` | `Property<Boolean>` | `true` | Asks TeaVM to generate shorter C file names, useful for native toolchains with path limits. |
| `obfuscated` | `Property<Boolean>` | `true` | Obfuscates generated native C symbols. |
| `releasePath` | `DirectoryProperty` | `[outputDir]/c/release` | Directory where native runtime assets and build output support files are prepared. |

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
        buildType.set("Release")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
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
        optimization.set(OptimizationLevel.AGGRESSIVE)
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        debugMemory.set(true)
    }
}
```

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
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        consoleLog.set(false)
    }

    psp {
        mainClass.set("com.example.game.teavm.PspLauncher")
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        debugMemory.set(false)
    }
}
```
