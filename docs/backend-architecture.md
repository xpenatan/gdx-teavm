# Backend Architecture

This document is for contributors and automation agents that need to understand how the backend build system fits together.

## High-Level Flow

`gdx-teavm` has two entry points that share backend logic:

- Manual builder API: Java code creates `TeaBuilder` and a concrete backend.
- Gradle plugin: Gradle configures TeaVM tasks and passes gdx-teavm properties into TeaVM runtime plugins.

Both paths use the same backend modules, asset planner, resource metadata, and reflection support.

## Manual Builder Path

The manual builder starts with:

```java
new TeaBuilder(new WebBackend())
        .addAssets(new AssetFileHandle("assets"))
        .setMainClass("com.example.WebLauncher")
        .build(new File("build/dist"));
```

The flow is:

1. `TeaBuilder` stores configuration in `TeaBuilderData`.
2. `TeaBuilder.build(...)` calls `TeaBackend.compile(...)`.
3. `TeaBackend` configures TeaVM, reflection, source providers, assets, and resources.
4. The concrete backend customizes the target and packaging.

Concrete builder backends:

| Backend | Target | Main output |
| --- | --- | --- |
| `WebBackend` | JavaScript or Wasm | Web app folder |
| `TeaGLFWBackend` | TeaVM C | GLFW CMake project |

## Gradle Plugin Path

The Gradle plugin lives in `tools/gdx-teavm-plugin` and is applied with:

```kotlin
plugins {
    id("com.github.xpenatan.gdx-teavm")
}
```

For regular Java projects, the plugin:

1. Applies Java and TeaVM's Gradle plugin.
2. Creates the `gdxTeaVM` extension.
3. Adds selected backend artifacts to the Java `implementation` configuration and TeaVM's generation classpath.
4. Configures TeaVM JS, Wasm, or C tasks.
5. Passes gdx-teavm settings into TeaVM properties.
6. Hides raw TeaVM tasks from the default task groups.
7. Registers user-facing `gdx_teavm_*` tasks.

Android projects use a dedicated path instead: the Android Gradle Plugin remains responsible for the application build, while gdx-teavm creates its TeaVM configuration, adds `backend-android`, registers the runtime bridge sources, and generates the native C/CMake payload.

Target blocks are opt-in. If a build file declares only `wasm {}`, only Wasm gdx-teavm tasks are created. Native TeaVM C settings live inside target blocks such as `glfw {}` because logical targets can have different launchers and native options. Android projects currently support only the `android {}` target.

## Gradle Plugin Tasks

| Target block | Tasks |
| --- | --- |
| `js {}` | `gdx_teavm_web_js_build`, `gdx_teavm_web_js_run` |
| `wasm {}` | `gdx_teavm_web_wasm_build`, `gdx_teavm_web_wasm_run` |
| `glfw {}` | `gdx_teavm_glfw_generate`, `gdx_teavm_glfw_build`, `gdx_teavm_glfw_run` |
| `ios {}` | `gdx_teavm_ios_generate`, `gdx_teavm_ios_prepare_angle`, `gdx_teavm_ios_init_xcode`, `gdx_teavm_ios_regenerate_xcode`, `gdx_teavm_ios_open_xcode`, `gdx_teavm_ios_build_simulator`, `gdx_teavm_ios_run_simulator` |
| `android {}` | `gdx_teavm_android_generate` |

Web run tasks use `GdxTeaVMRunWebTask` by default, loading `backend-web`'s `JettyServer` by reflection from the target runtime classpath. When the target's `devServer.enabled` property is true, the same public run task delegates compilation to TeaVM's `javaScriptDevServer` or `wasmGCDevServer` task. With `autoBuild` enabled, the run task registers a Gradle `DeploymentHandle`; Gradle detects that reloadable deployment, automatically enters continuous-build mode for the same invocation, and reruns Java compilation and TeaVM generation when task inputs change. With `autoBuild` disabled, the task blocks directly to keep both servers alive without registering a reloadable deployment. Local project class directories precede external JARs on the development classpath so TeaVM observes recompiled classes, and the plugin invalidates TeaVM's compiler cache before each rebuild. Cancelling the overall Gradle invocation stops both the TeaVM server and entry adapter in either mode.

The entry adapter supplies the generated page at `/` with an HTML content type, working around TeaVM serving generated `.html` files as plain text. When `autoReload` is enabled, it also installs a shared JS/Wasm client for TeaVM's build-status WebSocket. The plugin applies the same backend-filtered classpath and gdx-teavm properties to normal generation and development-server compilation.

## TeaVM Runtime Plugins

Backend modules include `TeaVMPlugin` implementations registered under `META-INF/services/org.teavm.vm.spi.TeaVMPlugin`.

| Backend | TeaVM plugin | Selection |
| --- | --- | --- |
| `backend-web` | `WebPlugin` | `TeaVMJavaScriptHost` or `TeaVMWasmGCHost` exists |
| `backend-glfw` | `GLFWPlugin` | `TeaVMCHost` exists and `gdx.teavm.native.backend=glfw` |
| `backend-ios` | `IOSPlugin` | `TeaVMCHost` exists and `gdx.teavm.native.backend=ios` |
| `backend-android` | `AndroidPlugin` | `TeaVMCHost` exists and `gdx.teavm.native.backend=android` |

The Gradle plugin writes the properties consumed by these runtime plugins. The runtime plugins parse those properties with `GdxTeaVMPluginConfig`.

## Web Runtime Plugin

`WebPlugin` installs:

- `WebClassTransformer`
- `JavaObjectExporterDependency`
- `TeaAssetManifestTransformer` for the compiled preload manifest
- `TeaWebRuntimeConfigTransformer` for web runtime defaults such as `logoPath`
- `TeaReflectionSupplier` reflection metadata setup
- `GdxWebTargetWrapper` when webapp generation is enabled

For JavaScript and Wasm, the same wrapper path is used so asset copying and web app generation do not diverge.

## Native Runtime Plugins

`GLFWPlugin`, `AndroidPlugin`, and `IOSPlugin` run during TeaVM C generation when their native backend is selected.

They install:

- reflection support
- target-specific render/build listeners
- native asset and external C/C++ resource copying

The native backend is selected from requested Gradle task names in `GdxTeaVMExtension.selectedNativeBackendName(...)`. Running multiple native plugin targets in the same Gradle invocation is rejected because TeaVM has one C task. The selected native block is applied to TeaVM's C configuration for that Gradle invocation.

## Assets

The central class is `AssetsCopy`.

Responsibilities:

- Plan disk assets and classpath assets.
- Preserve libGDX file type in the preload manifest.
- Copy files to the target output.
- For web targets, provide manifest entries through the generated TeaVM `TeaAssetManifest` class.
- For native targets, copy assets directly without a preload manifest file.
- Log copied asset source type and size.

Builder path:

```text
TeaBuilder.addAssets(...)
TeaBackend.copyAssets(...)
AssetsCopy
```

Plugin path:

```text
gdxTeaVM.assets(...)
GdxTeaVMPluginConfig
GdxTeaVMPluginAssetSupport
AssetsCopy
```

Do not duplicate asset-copy code in target wrappers. Add shared behavior to `AssetsCopy` or `GdxTeaVMPluginAssetSupport`.

## Resource Metadata

Libraries can contribute resources with `META-INF/gdx-teavm.properties`.

Supported keys:

- `resources=`
- `ignore-resources=`
- `classpath-resources=`

`TeaVMResourceProperties` parses these files. `TeaBackend.partitionResources(...)` separates web scripts, Wasm files, classpath assets, and native external C/C++ files.

## Reflection

Reflection metadata is required because TeaVM compiles ahead of time.

Builder path:

```text
TeaBuilder.addReflectionClass(...)
DefaultReflectionListener
TeaBackend.setupReflection(...)
TeaReflectionSupplier
```

Plugin path:

```text
gdxTeaVM.reflection(...)
GdxTeaVMPluginConfig
WebPlugin / native runtime plugin
TeaReflectionSupplier
```

Runtime reflection emulation in backend `emu` source sets uses `TeaReflectionSupplier`. Built-in default reflection patterns are owned by `TeaReflectionSupplier`; the Gradle plugin only passes the `reflectionDefaults` flag and user-provided `reflection(...)` patterns.

## JSO Overlay Notes

TeaVM Wasm strict mode performs runtime checks for non-transparent `@JSClass` types. If a Java class is only a facade over a JavaScript object and no real JavaScript global constructor exists for it, mark it transparent:

```java
@JSClass(transparent = true)
public abstract class HTMLDocumentExt extends HTMLDocument {
}
```

This is required for facade/helper types such as DOM extension wrappers and `WebGL20.CustomIntMap`. Do not use it for real JavaScript classes that should map to real constructors.

## Output Layout

Plugin defaults:

| Target | Root | Generated files |
| --- | --- | --- |
| JS | `build/dist/js` | `webapp` |
| Wasm | `build/dist/wasm` | `webapp` |
| GLFW | `build/dist/glfw` | `c/src`, `c/release`, build scripts |
| iOS | `build/dist/ios` | `c/src`, `c/release`, generated Xcode project |
| Android | `build/generated/gdx-teavm/android` | `c/src`, `CMakeLists.txt`, native resources |

Builder defaults depend on the `build(new File(...))` output directory and the concrete backend. `WebBackend` defaults to a `webapp` folder, while `TeaGLFWBackend` defaults to `c/src` plus `c/release`.
