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

Target blocks are opt-in. If a build file declares only `wasm {}`, only Wasm gdx-teavm tasks are created. `webDefaults {}` and `nativeDefaults {}` provide optional Gradle conventions without declaring targets. Unnamed target blocks retain the original tasks, while overloads such as `wasm("release") {}` create independently configured named targets. Native TeaVM C settings live inside target blocks such as `glfw {}` because logical targets can have different launchers and native options. Android projects currently support only the unnamed `android {}` target.

## Gradle Task Integration

Each declared target registers only its own public `gdx_teavm_*` tasks. Named targets insert their normalized name immediately before the lifecycle action, such as `gdx_teavm_web_js_release_build` or `gdx_teavm_glfw_release_run`. The [usage guide](usage.md#shared-defaults-and-named-targets) is the user-facing task reference; this section covers the implementation behind web run tasks.

Unnamed JS, Wasm, and native targets keep delegating to TeaVM's standard singleton generation tasks for compatibility. Named JS, Wasm, GLFW, and iOS targets register independent TeaVM generation tasks with isolated output directories. The plugin still hides those low-level generation helpers so users are guided toward the public lifecycle tasks.

Web run tasks use `GdxTeaVMRunWebTask` and `backend-web`'s `JettyServer` unless `devServer.enabled` selects the TeaVM server. In development-server mode, the public run task starts TeaVM compilation, an entry-page adapter, and an optional file watcher. The watcher invokes only the target project's `classes` task, then requests an incremental build from the existing TeaVM process. Local project output directories precede external JARs so the compiler sees the updated classes.

The entry adapter serves the generated page as HTML at `/`. With `autoReload` enabled, it also injects a JS/Wasm client for TeaVM's build-status WebSocket. Cancelling the run task stops both servers and releases the public port.

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

For legacy unnamed native targets, the backend is selected from requested Gradle task names in `GdxTeaVMExtension.selectedNativeBackendName(...)` and applied to TeaVM's singleton C task. Only one unnamed native backend can use that singleton in a Gradle invocation. Named GLFW and iOS targets use their own `GenerateCTask` instances, so their configuration and output do not overwrite another native variant. Android C generation remains integrated with its Android application module and Android Gradle Plugin lifecycle.

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

`TeaReflectionSupplier` maintains the shared class registry and dependency support. `TeaReflectionPolicy` extends TeaVM's `SimpleReflectionPolicy` and is registered through `META-INF/services/org.teavm.extension.spi.reflection.ReflectionPolicy`. It consults the registry to enable class lookup by name and reflection for all fields, methods, and constructors, including non-public members. This replaces the deprecated `ReflectionSupplier` SPI without narrowing reflection defaults or changing builder/plugin configuration.

## Native Toolchain Policy

The GLFW backend passes arbitrary CMake cache entries from `glfw.cmakeDefinitions` to its generated Windows and Unix configure scripts. Consumers can therefore use standard CMake settings such as `CMAKE_MSVC_RUNTIME_LIBRARY` without a backend-specific enum. Windows keeps its historical MT default when the setting is absent, but an explicit standard CMake selection is never overwritten.

Windows resources remain prebuilt and compact: GLFW and GLEW provide matching MT and MD static archives, and CMake selects the pair that matches the application target. On Linux and macOS, CMake downloads hash-verified GLFW 3.4 and GLEW 2.3.0 release sources into the native build directory and links them statically; their source trees are not stored in the backend JAR. `GDX_TEAVM_GLFW_USE_SYSTEM_LIBS=ON` restores system-package discovery when a consumer prefers it. Android and iOS use their dedicated native backends and toolchains; MSVC runtime flags are not portable to those targets.

## JSO Overlay Notes

TeaVM Wasm strict mode performs runtime checks for non-transparent `@JSClass` types. If a Java class is only a facade over a JavaScript object and no real JavaScript global constructor exists for it, mark it transparent:

```java
@JSClass(transparent = true)
public abstract class HTMLDocumentExt extends HTMLDocument {
}
```

This is required for facade/helper types such as DOM extension wrappers and `WebGL20.CustomIntMap`. Do not use it for real JavaScript classes that should map to real constructors.

## Output Layout

Plugin defaults are maintained in the [property reference](plugin-properties.md#common-teavm-target-properties). Unnamed plugin output uses `build/dist/<platform>`; named targets use `build/dist/<platform>/<normalized-name>`. Builder output is rooted at the directory passed to `build(File)` and organized by the concrete backend.
