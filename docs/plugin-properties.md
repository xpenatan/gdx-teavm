# Gradle Plugin Property Reference

This page is the authoritative list of properties available in the `gdxTeaVM` Gradle extension. See the [usage guide](usage.md#recommended-gradle-plugin) for setup and complete configurations.

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

## Target Declaration Methods

| Block | Backend |
| --- | --- |
| `js { ... }` | `backend-web` |
| `wasm { ... }` | `backend-web` |
| `glfw { ... }` | `backend-glfw` |
| `ios { ... }` | `backend-ios` |
| `android { ... }` | `backend-android` |

Only declared targets create tasks or add backend dependencies. Task behavior is covered in the [usage guide](usage.md#web-targets).

## Common TeaVM Target Properties

The `js {}` and `wasm {}` blocks expose these TeaVM Gradle properties directly. Native target blocks expose the matching TeaVM C settings and apply only the selected backend to TeaVM's C task.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `outputDir` | `DirectoryProperty` | target-specific | Root directory for generated files. |
| `mainClass` | `Property<String>` | none | Fully qualified launcher class used as TeaVM main class. This must be set for every target you build. |
| `relativePathInOutputDir` | `Property<String>` | web `webapp`, native `c/src` | Path inside `outputDir` where TeaVM writes generated files. |
| `optimization` | `Property<OptimizationLevel>` | `BALANCED` | TeaVM optimization level. |
| `debugInformation` | `Property<Boolean>` | `false` | Includes TeaVM debug information when supported by the target. |
| `fastGlobalAnalysis` | `Property<Boolean>` | `false` | Enables faster TeaVM global analysis, trading precision for speed. |
| `outOfProcess` | `Property<Boolean>` | Web `true`, native `false` | Runs TeaVM compilation out of the Gradle process when supported by TeaVM. |
| `processMemory` | `Property<Int>` | Web `1024`, native `512` | Memory limit in megabytes for out-of-process TeaVM compilation. |
| `preservedClasses` | `ListProperty<String>` | empty | Classes TeaVM should preserve from aggressive removal or renaming. |

`outputDir` defaults to `build/dist/js`, `build/dist/wasm`, `build/dist/glfw`, `build/dist/ios`, or `build/generated/gdx-teavm/android` for the corresponding target.

## Web Targets

Web targets use `backend-web` and can be built as JavaScript or Wasm. JS and Wasm usually share the same launcher class because both start `WebApplication`.

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
| `logoPath` | `Property<String>` | `startup-logo.png` | Default startup-logo asset path compiled into `WebPreloadApplicationListener`; when `copyLoadingAsset` is true, the same path is copied from the build classpath. |
| `copyLoadingAsset` | `Property<Boolean>` | `true` | Copies `logoPath` into the generated web app when true. |
| `serverPort` | `Property<Int>` | Gradle property `teavmPluginPort`, otherwise `8080` | Port used by this target's Jetty or TeaVM development server. |
| `devServer` | `GdxTeaVMDevServerExtension` | disabled | Configures TeaVM's persistent development server for this target's existing run task. |

### Development Server Properties

The same `devServer {}` block is available inside `js {}` and `wasm {}` and exposes only options supported by both targets.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `enabled` | `Property<Boolean>` | `false` | Makes the target's existing run task use TeaVM's persistent development server. |
| `autoBuild` | `Property<Boolean>` | `true` | Rebuilds when project sources, resources, assets, or static files change. Set to `false` for a serve-only session. |
| `autoReload` | `Property<Boolean>` | `false` | Reloads connected pages after successful rebuilds. |
| `processMemory` | `Property<Int>` | Target `processMemory`, normally `1024` | Maximum heap size in megabytes for the development-server process. |
| `staticDirs` | `ConfigurableFileCollection` | empty | Additional directories served as static files. |
| `staticServePath` | `Property<String>` | unset | URL path prefix for `staticDirs`. |
| `resourceRoots` | `ListProperty<String>` | empty | Classpath resource roots served as static resources. |
| `resourceServePath` | `Property<String>` | unset | URL path prefix for `resourceRoots`. |

The development server uses the containing target's `serverPort`. TeaVM's JavaScript-only indicator and stack-deobfuscation switches are intentionally not part of this common JS/Wasm API.

### JavaScript Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `targetFileName` | `Property<String>` | `app.js` | Name of the generated JavaScript output file. |
| `obfuscated` | `Property<Boolean>` | `true` | Minifies and renames generated JavaScript output. |
| `strict` | `Property<Boolean>` | `false` | Enables TeaVM strict JavaScript generation checks. |
| `sourceMap` | `Property<Boolean>` | `false` | Generates browser source maps. |
| `sourceFilePolicy` | `Property<SourceFilePolicy>` | `LINK_LOCAL_FILES` | Controls how Java sources referenced by source maps are exposed. |

### Wasm Properties

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `targetFileName` | `Property<String>` | `app.wasm` | Name of the generated Wasm output file. |
| `obfuscated` | `Property<Boolean>` | `true` | Minifies and renames generated Wasm runtime support output. |
| `strict` | `Property<Boolean>` | `false` | Enables TeaVM strict Wasm generation checks. |
| `copyRuntime` | `Property<Boolean>` | `true` | Copies TeaVM's Wasm runtime JavaScript next to the generated `.wasm` file. |
| `modularRuntime` | `Property<Boolean>` | `false` | Copies TeaVM's ES module Wasm runtime instead of the global script runtime. The generated gdx-teavm web app expects the default global runtime. |
| `sourceMap` | `Property<Boolean>` | `false` | Generates browser source maps. |
| `sourceFilePolicy` | `Property<SourceFilePolicy>` | `LINK_LOCAL_FILES` | Controls how Java sources referenced by source maps are exposed. |

## Native Targets

Native targets use TeaVM C output and add the following properties to the common target settings.

### Native Target Properties

These properties exist in native target blocks.

| Property | Type | Default | Purpose |
| --- | --- | --- | --- |
| `targetFileName` | `Property<String>` | `app` | Name of the generated native target. |
| `releasePath` | `DirectoryProperty` | `[outputDir]/c/release` | Directory where native runtime assets and build output support files are prepared. |
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
| `consoleLog` | `Property<Boolean>` | `false` | Opens or attaches native console logging for GLFW run tasks when supported by the platform. On Windows, the opened console remains visible after the application exits until a key is pressed. |
| `cmakeDefinitions` | `MapProperty<String, String>` | empty | Advanced ordered CMake cache definitions passed to the generated GLFW configure scripts. Use `cmakeDefinition(name, value)` to add one definition. |

Platform-specific CMake behavior is documented under [Native Toolchain Policy](backend-architecture.md#native-toolchain-policy).

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
