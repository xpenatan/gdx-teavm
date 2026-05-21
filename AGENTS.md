# AGENTS Guide for `gdx-teavm`

## Mandatory Notes
- Keep a `CURRENT_CHAT_CONTEXT` file with the current chat flow so work can be recovered after session failures.
- `CURRENT_CHAT_CONTEXT` is for last-chat context only; do not keep long-term history there.
- At session start, always check whether `CURRENT_CHAT_CONTEXT` exists; if it does, load it before proceeding.
- Always validate that a class, member, method, task, or Gradle property already exists before using it. Never guess API names or signatures.
- Never write or modify any file without explicit user permission. Planning and review steps are allowed without writing.
- After making changes, run a simple Gradle build to confirm code still compiles. Prefer the smallest affected module/task, or `./gradlew build` when unclear.

## Common Workflows
- Inspect project graph:
  - `./gradlew projects`
- Build everything:
  - `./gradlew build`
- Compile the Gradle plugin:
  - `./gradlew :gdx-teavm-plugin:compileKotlin`
- Compile the shared/web backends:
  - `./gradlew :backends:backend-shared:compileJava :backends:backend-web:compileJava`
- Compile native backends:
  - `./gradlew :backends:backend-glfw:compileJava :backends:backend-psp:compileJava`

## Example Tasks
- Manual builder web example:
  - `./gradlew :examples:basic:web:basic_run_web`
- Manual builder GLFW example:
  - `./gradlew :examples:basic:glfw:basic_generate_teavm_glfw`
  - `./gradlew :examples:basic:glfw:basic_build_teavm_glfw_debug`
  - `./gradlew :examples:basic:glfw:basic_run_teavm_glfw_debug`
- Manual builder PSP example:
  - `./gradlew :examples:basic:psp:basic_build_teavm_psp`
- Gradle plugin basic example:
  - `./gradlew :examples:basic:plugin:gdx_teavm_web_js_run`
  - `./gradlew :examples:basic:plugin:gdx_teavm_web_wasm_run`
  - `./gradlew :examples:basic:plugin:gdx_teavm_glfw_generate`
  - `./gradlew :examples:basic:plugin:gdx_teavm_glfw_build`
  - `./gradlew :examples:basic:plugin:gdx_teavm_glfw_run`
  - `./gradlew :examples:basic:plugin:gdx_teavm_psp_generate`
  - `./gradlew :examples:basic:plugin:gdx_teavm_psp_build`
- Gradle plugin FreeType web example:
  - `./gradlew :examples:freetype:web:gdx_teavm_web_js_run`
  - `./gradlew :examples:freetype:web:gdx_teavm_web_wasm_run`
- Publishing entry points are root tasks from `buildSrc/src/main/kotlin/publish.gradle.kts`:
  - `publishSnapshot`
  - `publishRelease`
  - `prepareSnapshotDeploy`
  - `prepareReleaseDeploy`

## Project Shape
- This is a multi-module Gradle build. Active modules live under `backends/`, `extensions/`, `tools/gdx-teavm-plugin`, and `examples/`.
- `settings.gradle.kts` includes the local Gradle plugin build with `includeBuild("tools/gdx-teavm-plugin")`; the included build appears as project path `:gdx-teavm-plugin` in Gradle output.
- Version numbers and build switches are centralized in `buildSrc/src/main/kotlin/LibExt.kt`.
- Root `build.gradle.kts` applies shared Java 11 settings and Maven repositories to subprojects.
- `gradle.properties` can enable composite builds for local libGDX or TeaVM source:
  - `includeLibgdxSource`
  - `includeTeaVMSource`
  - `gdxSourcePath`
  - `teavmPath`

## Core Compiler Pipeline
- The manual builder API lives in `backends/backend-shared`.
- `TeaCompiler` is the fluent public entry point. It stores configuration in `TeaCompilerData`.
- `TeaCompiler.build(File output)` delegates to a concrete `TeaBackend`.
- `TeaBackend.compile(...)` performs shared setup:
  - classpath collection
  - TeaVM tool configuration
  - reflection metadata setup
  - asset planning and copying
  - backend-specific build hooks
- Put behavior in `TeaBackend` only when it is shared by all targets. Keep target-specific behavior in:
  - `WebBackend`
  - `TeaGLFWBackend`
  - `TeaPSPBackend`

## Backends
- `backends/backend-web`
  - Runtime classes: `WebApplication`, `WebApplicationConfiguration`, WebGL/audio/filesystem implementations.
  - Builder backend: `WebBackend`.
  - Targets JavaScript or Wasm based on `WebBackend.setWebAssembly(boolean)`.
  - Generates `index.html`, `WEB-INF/web.xml`, web assets, preload manifest, scripts, and optional Jetty serving.
- `backends/backend-glfw`
  - Runtime classes: `GLFWApplication`, `GLFWApplicationConfiguration`.
  - Builder backend: `TeaGLFWBackend`.
  - Targets TeaVM C output and writes a native CMake project with debug/release build scripts.
  - Output is typically under `build/dist/glfw/c` in plugin mode or `build/dist/c` in builder mode.
- `backends/backend-psp`
  - Runtime classes: `PSPApplication`, PSP native APIs, PSP glue.
  - Builder backend: `TeaPSPBackend`.
  - Targets TeaVM C output and writes PSP build scripts.
  - PSP defaults are smaller heap/direct-buffer sizes than desktop native targets.

## Gradle Plugin
- Plugin source lives in `tools/gdx-teavm-plugin`.
- Plugin id: `com.github.xpenatan.gdx-teavm`.
- Extension block: `gdxTeaVM { ... }`.
- The plugin applies Java and TeaVM's Gradle plugin internally.
- Target blocks are declarative. Tasks are created only for declared blocks:
  - `js { ... }`
  - `wasm { ... }`
  - `glfw { ... }`
  - `psp { ... }`
- Plugin-generated tasks use group `gdx-teavm`.
- TeaVM's own low-level tasks still exist internally, but the plugin clears their task group so normal users are guided toward the `gdx_teavm_*` tasks.
- The plugin forces TeaVM generation tasks to run each invocation so assets and generated web/native wrappers are refreshed.
- The plugin adds the selected backend artifact to both Java `implementation` and TeaVM's generation classpath:
  - web targets add `backend-web`
  - GLFW adds `backend-glfw`
  - PSP adds `backend-psp`
- In the repository build, backend dependencies resolve to local projects. In a published build, they resolve from Maven using the generated plugin version in `GdxTeaVMPluginInfo`.

## Plugin Tasks
- Web JavaScript:
  - `gdx_teavm_web_js_build`
  - `gdx_teavm_web_js_run`
- Web Wasm:
  - `gdx_teavm_web_wasm_build`
  - `gdx_teavm_web_wasm_run`
- GLFW native:
  - `gdx_teavm_glfw_generate`
  - `gdx_teavm_glfw_build`
  - `gdx_teavm_glfw_run`
- PSP native:
  - `gdx_teavm_psp_generate`
  - `gdx_teavm_psp_build`

## Plugin Configuration Model
- Shared plugin properties are defined in `GdxTeaVMExtension`.
- Only backend-agnostic settings belong in the root `gdxTeaVM { ... }` block, such as assets and reflection.
- Per-target TeaVM properties are defined in `GdxTeaVMTargetExtension` and subclasses:
  - `GdxTeaVMWebExtension`
  - `GdxTeaVMJsExtension`
  - `GdxTeaVMWasmExtension`
  - `GdxTeaVMGlfwExtension`
  - `GdxTeaVMPspExtension`
- Web-only settings such as `htmlTitle`, `htmlWidth`, `htmlHeight`, `entryPointName`, `mainClassArgs`, `logoPath`, `copyLoadingAsset`, `webappEnabled`, and `serverPort` belong in `js {}` or `wasm {}`, not in the root extension.
- GLFW build mode is selected with `glfw.buildType` (`Debug` or `Release`); plugin tasks are not split by build type.
- Web targets usually share the same launcher class.
- GLFW and PSP usually need native-specific launcher classes because they start different backend application classes.
- Default output directories:
  - JS: `build/dist/web`
  - Wasm: `build/dist/wasm`
  - GLFW: `build/dist/glfw`
  - PSP: `build/dist/psp`
- Default generated app subdirectories:
  - Web targets: `webapp`
  - Native targets: `c/src`

## TeaVM Runtime Plugins
- Backend runtime plugins implement `org.teavm.vm.spi.TeaVMPlugin` and are registered through `META-INF/services/org.teavm.vm.spi.TeaVMPlugin`.
- `WebPlugin` supports JavaScript and Wasm by checking:
  - `TeaVMJavaScriptHost`
  - `TeaVMWasmGCHost`
- `WebPlugin` installs:
  - `WebClassTransformer`
  - `JavaObjectExporterDependency`
  - reflection support
  - `GdxWebTargetWrapper` when webapp generation is enabled
- `GLFWPlugin` and `PSPPlugin` support TeaVM C output by checking `TeaVMCHost`.
- Native plugins use `gdx.teavm.native.backend` to decide whether the current C generation is for GLFW or PSP.
- Plugin properties are transported through `TeaVMHost.getProperties()` and parsed by `GdxTeaVMPluginConfig`.

## Assets And Resources
- Asset copying is centralized in `AssetsCopy`.
- Do not create a second asset-copy implementation unless there is a strong reason.
- Builder and plugin paths both use the same planning/copying logic.
- Output always includes a preload manifest named `assets/preload.txt` by default (`TeaAssets.ASSETS_FILE_NAME`).
- Asset entries preserve libGDX file type:
  - disk assets are normally `Internal`
  - classpath resources are `Classpath`
- `AssetFileHandle` is used by the builder API.
- The Gradle plugin exposes:
  - `assets(...)` or `assets.from(...)`
  - `classpathAssets(...)`
- Libraries can auto-contribute resources through `META-INF/gdx-teavm.properties`, parsed by `TeaVMResourceProperties`:
  - `resources=`
  - `ignore-resources=`
  - `classpath-resources=`
- Scripts (`.js`, `.wasm`) and `/external_cpp/` resources are partitioned by `TeaBackend.partitionResources(...)` and copied by the relevant backend.
- Packaged resource metadata exists in:
  - `backends/backend-shared/src/main/resources/META-INF/gdx-teavm.properties`
  - `backends/backend-web/src/main/resources/META-INF/gdx-teavm.properties`
  - `backends/backend-glfw/src/main/resources/META-INF/gdx-teavm.properties`
  - `backends/backend-psp/src/main/resources/META-INF/gdx-teavm.properties`

## Reflection
- libGDX reflection emulation is backed by generated TeaVM metadata.
- Builder API:
  - `TeaCompiler.addReflectionClass(Class<?>)`
  - `TeaCompiler.addReflectionClass(String)`
  - `TeaCompiler.setReflectionListener(DefaultReflectionListener)`
- Gradle plugin API:
  - `reflection("com.example.Type")`
  - `reflection("com.example.package**")`
  - `reflectionEnabled`
  - `reflectionDefaults`
  - `reflectionScan`
  - `reflectionDebug`
- Runtime emulation classes read reflection metadata through `TeaReflectionSupplier`.
- Plugin generation installs reflection support through `TeaVMPluginReflectionSupport`.

## JSO And Wasm Strict Mode
- TeaVM Wasm strict mode emits runtime checks for non-transparent `@JSClass` overlays.
- If a class is only a Java facade over an existing JavaScript object and does not exist as a real JavaScript global constructor, annotate it with:
  - `@JSClass(transparent = true)`
- Existing examples include DOM/WebGL facades and `WebGL20.CustomIntMap`.
- Do not annotate real JavaScript classes such as browser constructors or library constructors as transparent without checking their TeaVM usage.

## Development Notes
- Source sets for `backend-web`, `backend-glfw`, and `backend-psp` intentionally compile from both `emu` and `src/main/java`.
- If adding packaged resources, update the module's `META-INF/gdx-teavm.properties` so assets are discoverable.
- Preserve fluent API style in compiler and backend config classes; setters return `this`.
- Prefer proving changes through existing Gradle tasks instead of ad-hoc commands.
- On Windows, Gradle daemons can hold `backend-web` JARs open. If `:backends:backend-web:clean` fails on a locked JAR, run `./gradlew --stop` and retry.

## Documentation Entry Points
- Root README: `README.md`
- Usage guide: `docs/usage.md`
- Plugin property reference: `docs/plugin-properties.md`
- Backend architecture guide: `docs/backend-architecture.md`
- GLFW native build notes: `examples/basic/glfw/README.md`
