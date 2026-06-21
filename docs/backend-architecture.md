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
| `TeaPSPBackend` | TeaVM C | PSP native project |

## Gradle Plugin Path

The Gradle plugin lives in `tools/gdx-teavm-plugin` and is applied with:

```kotlin
plugins {
    id("com.github.xpenatan.gdx-teavm")
}
```

The plugin:

1. Applies Java and TeaVM's Gradle plugin.
2. Creates the `gdxTeaVM` extension.
3. Adds selected backend artifacts to the Java `implementation` configuration and TeaVM's generation classpath.
4. Configures TeaVM JS, Wasm, or C tasks.
5. Passes gdx-teavm settings into TeaVM properties.
6. Hides raw TeaVM tasks from the default task groups.
7. Registers user-facing `gdx_teavm_*` tasks.

Target blocks are opt-in. If a build file declares only `wasm {}`, only Wasm gdx-teavm tasks are created. Native TeaVM C settings live inside target blocks such as `glfw {}` because logical targets can have different launchers and native options.

## Gradle Plugin Tasks

| Target block | Tasks |
| --- | --- |
| `js {}` | `gdx_teavm_web_js_build`, `gdx_teavm_web_js_run` |
| `wasm {}` | `gdx_teavm_web_wasm_build`, `gdx_teavm_web_wasm_run` |
| `glfw {}` | `gdx_teavm_glfw_generate`, `gdx_teavm_glfw_build`, `gdx_teavm_glfw_run` |

The web run tasks use `GdxTeaVMRunWebTask`, which loads `backend-web`'s `JettyServer` by reflection from the target runtime classpath. This keeps server behavior shared with `WebBackend`.

## TeaVM Runtime Plugins

Backend modules include `TeaVMPlugin` implementations registered under `META-INF/services/org.teavm.vm.spi.TeaVMPlugin`.

| Backend | TeaVM plugin | Selection |
| --- | --- | --- |
| `backend-web` | `WebPlugin` | `TeaVMJavaScriptHost` or `TeaVMWasmGCHost` exists |
| `backend-glfw` | `GLFWPlugin` | `TeaVMCHost` exists and `gdx.teavm.native.backend=glfw` |
| `backend-psp` | `PSPPlugin` | Experimental local-only manual builder path |
| `backend-ios` | `IOSPlugin` | Experimental local-only backend path |

The Gradle plugin writes the properties consumed by these runtime plugins. The runtime plugins parse those properties with `GdxTeaVMPluginConfig`.

## Web Runtime Plugin

`WebPlugin` installs:

- `WebClassTransformer`
- `JavaObjectExporterDependency`
- `TeaReflectionSupplier` reflection metadata setup
- `GdxWebTargetWrapper` when webapp generation is enabled

For JavaScript and Wasm, the same wrapper path is used so asset copying and web app generation do not diverge.

## Native Runtime Plugins

`GLFWPlugin` runs during plugin TeaVM C generation when the selected native backend is GLFW. `PSPPlugin` and `IOSPlugin` remain available only for experimental local backend paths.

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
WebPlugin / GLFWPlugin
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
| JS | `build/dist/web` | `webapp` |
| Wasm | `build/dist/wasm` | `webapp` |
| GLFW | `build/dist/glfw` | `c/src`, `c/release`, build scripts |

Builder defaults depend on the `build(new File(...))` output directory and the concrete backend. For example, `WebBackend` defaults to a `webapp` folder and native backends default to `c/src` plus `c/release`.

## Publishing

Publishing is centralized in `buildSrc/src/main/kotlin/publish.gradle.kts`.

The root build publishes library modules. It also delegates plugin marker and plugin implementation publishing to the included plugin build under `tools/gdx-teavm-plugin`.

The experimental PSP and iOS backends are local-only and are not part of the published library set.

Key root tasks:

- `prepareSnapshotDeploy`
- `prepareReleaseDeploy`
- `publishSnapshot`
- `publishRelease`

Local prepare tasks generate local Maven repository layouts in:

- `build/snapshot-deploy`
- `build/staging-deploy`
