# AGENTS Guide for `gdx-teavm`

## Mandatory notes
- Keep a `CURRENT_CHAT_CONTEXT` file with the current chat flow so work can be recovered after session failures.
- `CURRENT_CHAT_CONTEXT` is for last-chat context only; do not keep long-term history there.
- At session start, always check whether `CURRENT_CHAT_CONTEXT` exists; if it does, load it to recover current flow before proceeding.
- Always validate that a class/member/method already exists before using it; never guess API names or signatures.
- Never write or modify any file without explicit user permission (planning/review steps are allowed without writing).
- After making changes, run a simple Gradle build to confirm code compiles (prefer the smallest affected module/task, or `./gradlew build` when unclear).

## Developer workflows to use
- Inspect project graph:
  - `./gradlew projects`
- Build everything:
  - `./gradlew build`
- Run web example build launcher:
  - `./gradlew :examples:basic:teavm-web:basic_run_web`
- Run desktop example:
  - `./gradlew :examples:basic:desktop:basic_run_desktop`
- Generate C output for GLFW/PSP:
  - `./gradlew :examples:basic:teavm-glfw:basic_build_teavm_glfw`
  - `./gradlew :examples:basic:teavm-psp:basic_build_teavm_psp`
- Publishing entry points are root tasks from `buildSrc/src/main/kotlin/publish.gradle.kts`: `publishSnapshot`, `publishRelease`.

## Project shape (what matters first)
- Multi-module Gradle build; active modules are under `backends/`, `extensions/`, and `examples/` (see `settings.gradle.kts`).
- Core pipeline lives in `backends/backend-shared`: `TeaCompiler` (fluent config) delegates to a concrete `TeaBackend` (`build(...) -> backend.compile(...)`).
- Backends specialize output target + packaging:
  - Web: `backends/backend-web/.../WebBackend.java` (JS or Wasm-GC output, optional Jetty start).
  - Native C (GLFW): `backends/backend-glfw/.../TeaGLFWBackend.java` (generates C, `CMakeLists.txt`, `app_debug.bat`, `app_release.bat`).
  - Native C (PSP): `backends/backend-psp/.../TeaPSPBackend.java` (generates C + `build.bat`/`build.sh`, can auto-run build script).
- Typical consumer flow is in example launchers like `examples/basic/teavm-web/src/main/java/BuildTeaVMTestDemo.java`.

## Editing guidelines for agents in this repo
- When adding backend behavior, implement in `TeaBackend` only if shared by all targets; otherwise keep logic in `WebBackend` / `TeaGLFWBackend` / `TeaPSPBackend`.
- If you add new packaged resources, update the module’s `META-INF/gdx-teavm.properties`; otherwise assets may not be copied into output.
- Preserve fluent API style in compiler config classes (`TeaCompiler` setters return `this`).
- Prefer proving changes through existing Gradle JavaExec tasks in `examples/*/teavm-*/build.gradle.kts` instead of ad-hoc commands.

## Build/config conventions specific to this repo
- Versions and switches are centralized in `buildSrc/src/main/kotlin/LibExt.kt` (`gdxVersion`, `teaVMVersion`, release/snapshot behavior).
- Root `build.gradle.kts` forces Java 11 target for subprojects, even though some example docs mention Java 21 for local toolchains.
- `gradle.properties` flags (`includeLibgdxSource`, `includeTeaVMSource`) enable composite builds with local source substitution in `settings.gradle.kts`.
- `backend-web` intentionally compiles from both `emu` + `src/main/java` (`sourceSets["main"].java.setSrcDirs(...)`). Same pattern in `backend-glfw` and `backend-psp`.

## Resource and asset loading model (important)
- Asset copying is centralized in `TeaBackend.copyAssets(...)` and always emits an `assets/assets.txt` manifest.
- Libraries can auto-contribute resources via `META-INF/gdx-teavm.properties` parsed by `TeaVMResourceProperties`:
  - `resources=` include paths, `ignore-resources=` exclusions, `classpath-resources=` classpath bundles.
- Scripts (`.js`, `.wasm`) and `/external_cpp/` files are partitioned in `TeaBackend.partitionResources(...)` and then handled per-backend.
- Concrete examples: `backends/backend-shared/src/main/resources/META-INF/gdx-teavm.properties`, `backends/backend-web/src/main/resources/META-INF/gdx-teavm.properties`.



## Conventions discovered from existing docs
- Root `README.md` positions this repo as libGDX + TeaVM backend with optional Emscripten/JNI glue.
- `examples/basic/teavm-glfw/README.md` is the authoritative native build doc (CMake + Visual Studio workflow after code generation).


