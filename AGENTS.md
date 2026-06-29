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
  - `./gradlew :backends:backend-glfw:compileJava :backends:backend-ios:compileJava`

## Example Tasks
- Manual builder web example:
  - `./gradlew :examples:basic:web:basic_web_run`
- Manual builder desktop C example:
  - `./gradlew :examples:basic:desktop-c:basic_desktop_c_generate`
  - `./gradlew :examples:basic:desktop-c:basic_desktop_c_debug_build`
  - `./gradlew :examples:basic:desktop-c:basic_desktop_c_debug_run`
- Gradle plugin basic example:
  - `./gradlew :examples:basic:plugin:gdx_teavm_web_js_run`
  - `./gradlew :examples:basic:plugin:gdx_teavm_web_wasm_run`
  - `./gradlew :examples:basic:plugin:gdx_teavm_glfw_generate`
  - `./gradlew :examples:basic:plugin:gdx_teavm_glfw_build`
  - `./gradlew :examples:basic:plugin:gdx_teavm_glfw_run`
- Gradle plugin FreeType web example:
  - `./gradlew :examples:freetype:plugin:gdx_teavm_web_js_run`
  - `./gradlew :examples:freetype:plugin:gdx_teavm_web_wasm_run`
- Gradle plugin gdx-controllers web example:
  - `./gradlew :examples:controllers:plugin:gdx_teavm_web_js_run`
  - `./gradlew :examples:controllers:plugin:gdx_teavm_web_wasm_run`
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
- `TeaBuilder` is the fluent public entry point. It stores configuration in `TeaBuilderData`.
- `TeaBuilder.build(File output)` delegates to a concrete `TeaBackend`.
- `TeaBackend.compile(...)` performs shared setup:
  - classpath collection
  - TeaVM tool configuration
  - reflection metadata setup
  - asset planning and copying
  - backend-specific build hooks
- Put behavior in `TeaBackend` only when it is shared by all targets. Keep target-specific behavior in:
  - `WebBackend`
  - `TeaGLFWBackend`

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
## Gradle Plugin
- Plugin source lives in `tools/gdx-teavm-plugin`.
- Plugin id: `com.github.xpenatan.gdx-teavm`.
- Extension block: `gdxTeaVM { ... }`.
- The plugin applies Java and TeaVM's Gradle plugin internally.
- Target blocks are declarative. Tasks are created only for declared blocks:
  - `js { ... }`
  - `wasm { ... }`
  - `glfw { ... }`
  - `ios { ... }`
- Plugin-generated tasks use group `gdx-teavm`.
- TeaVM's own low-level tasks still exist internally, but the plugin clears their task group so normal users are guided toward the `gdx_teavm_*` tasks.
- The plugin forces TeaVM generation tasks to run each invocation so assets and generated web/native wrappers are refreshed.
- The plugin adds the selected backend artifact to both Java `implementation` and TeaVM's generation classpath:
  - web targets add `backend-web`
  - GLFW adds `backend-glfw`
  - iOS adds `backend-ios`
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
- iOS native:
  - `gdx_teavm_ios_generate`
  - `gdx_teavm_ios_prepare_angle`
  - `gdx_teavm_ios_init_xcode`
  - `gdx_teavm_ios_regenerate_xcode`
  - `gdx_teavm_ios_open_xcode`
  - `gdx_teavm_ios_build_simulator`
  - `gdx_teavm_ios_run_simulator`

## Plugin Configuration Model
- Shared plugin properties are defined in `GdxTeaVMExtension`.
- Only backend-agnostic settings belong in the root `gdxTeaVM { ... }` block, such as assets and reflection.
- Per-target TeaVM properties are defined in `GdxTeaVMTargetExtension` and subclasses:
  - `GdxTeaVMWebExtension`
  - `GdxTeaVMJsExtension`
  - `GdxTeaVMWasmExtension`
  - `GdxTeaVMGlfwExtension`
  - `GdxTeaVMIosExtension`
- Web-only settings such as `htmlTitle`, `htmlWidth`, `htmlHeight`, `entryPointName`, `mainClassArgs`, `logoPath`, `copyLoadingAsset`, `webappEnabled`, and `serverPort` belong in `js {}` or `wasm {}`, not in the root extension.
- GLFW build mode is selected with `glfw.buildType` (`Debug` or `Release`); plugin tasks are not split by build type.
- Web targets usually share the same launcher class.
- Native targets usually need native-specific launcher classes because they start different backend application classes.
- iOS is an experimental native plugin target with TeaVM C/assets generation plus WIP Xcode and simulator tasks.
- Default output directories:
  - JS: `build/dist/web`
  - Wasm: `build/dist/wasm`
  - GLFW: `build/dist/glfw`
  - iOS: `build/dist/ios`
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
- `GLFWPlugin`, `AndroidPlugin`, and `IOSPlugin` support TeaVM C output by checking `TeaVMCHost`.
- Native plugins use `gdx.teavm.native.backend` to decide which native backend is selected.
- Plugin properties are transported through `TeaVMHost.getProperties()` and parsed by `GdxTeaVMPluginConfig`.

## Assets And Resources
- Asset copying is centralized in `AssetsCopy`.
- Do not create a second asset-copy implementation unless there is a strong reason.
- Builder and plugin paths both use the same planning/copying logic.
- Web targets compile preload metadata into TeaVM output through `TeaAssetManifest`; native targets copy assets directly and do not create a preload manifest file.
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

## Reflection
- libGDX reflection emulation is backed by generated TeaVM metadata.
- Builder API:
  - `TeaBuilder.addReflectionClass(Class<?>)`
  - `TeaBuilder.addReflectionClass(String)`
  - `TeaBuilder.setReflectionListener(DefaultReflectionListener)`
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
- Source sets for `backend-web` and `backend-glfw` intentionally compile from both `emu` and `src/main/java`.
- If adding packaged resources, update the module's `META-INF/gdx-teavm.properties` so assets are discoverable.
- Preserve fluent API style in compiler and backend config classes; setters return `this`.
- Prefer proving changes through existing Gradle tasks instead of ad-hoc commands.
- On Windows, Gradle daemons can hold `backend-web` JARs open. If `:backends:backend-web:clean` fails on a locked JAR, run `./gradlew --stop` and retry.

## TeaVM C Performance Work
- For GLFW TeaVM C performance work, first identify a hot generated-C or Java method through benchmark/profile evidence.
- If that hot path repeatedly pays TeaVM null checks, bounds checks, type checks, primitive array access, or tiny Java calls inside a per-frame, per-sprite, or per-vertex loop, prefer an internal C helper exposed through `@Import` and installed by a transformer/substitution.
- Keep public libGDX APIs unchanged. Do not require users to switch to custom public classes for performance work.
- Do not optimize benchmark-only classes as the real solution. Benchmark-only C paths are acceptable only when explicitly marked as comparison code.
- Do not put all optimized logic directly into TeaVM C bridge files. TeaVM generated C types, fields, and method names are hard to read, so bridge code should stay small.
- Put readable, reusable CPU-heavy native kernels in `backends/backend-shared/src/main/resources/external_cpp/teavm_optimizations/pure`.
- Pure C files may have `.c` and `.h` files with clean function names and normal C signatures.
- For measured per-frame, per-sprite, or per-vertex hot kernels, prioritize pure header-inline helpers (`static inline`, `__forceinline`, or compiler-specific `always_inline`) so TeaVM bridge callers can inline the work into the hot loop.
- Use a separate pure `.c` function only for cold/medium paths, large code where size/readability matters more than call overhead, or when benchmark evidence shows no regression from the function boundary.
- Pure C files must not include TeaVM generated headers, access TeaVM object layouts, throw TeaVM exceptions, or use TeaVM GC barriers.
- Put TeaVM-dependent wrapper code in `backends/backend-shared/src/main/resources/external_cpp/teavm_optimizations/teavm`.
- When adding a new TeaVM-dependent optimization bridge, remember that native targets default to TeaVM `shortFileNames=true`. Any C includes, generated-header checks, and CMake source gating must support both long paths such as `c/src/classes/com/example/Foo.h` and shortened paths such as `c/src/c/e/Foo.h`; prefer detecting generated classes through `c/src/all.txt` when possible.
- TeaVM C bridge files should only unpack TeaVM objects/arrays, validate inputs, handle exceptions/barriers/flush calls, and call pure C kernels.
- When optimizing a hot method, move only the expensive computation into pure C. Keep simple getters, setters, and lightweight state changes in Java unless profiling proves otherwise.
- Preserve semantics with cold Java fallback/error paths where needed, and validate with the smallest Gradle compile task plus bounded native benchmark runs that force-close stuck apps.

## Documentation Entry Points
- Root README: `README.md`
- Usage guide: `docs/usage.md`
- Plugin property reference: `docs/plugin-properties.md`
- Backend architecture guide: `docs/backend-architecture.md`
- Desktop C native build notes: `examples/basic/desktop-c/README.md`
