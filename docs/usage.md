# gdx-teavm Usage Guide

This guide covers the two supported ways to build with `gdx-teavm`:

- The Gradle plugin, recommended for normal application projects.
- The manual builder API, useful for custom build launchers and backend development.

## Versions

The repository keeps dependency versions in `buildSrc/src/main/kotlin/LibExt.kt`.

| Component | Current version |
| --- | --- |
| libGDX | `1.14.1` |
| TeaVM | `0.14.0` |
| gdx-teavm snapshot | `-SNAPSHOT` |

## Repositories

For releases:

```kotlin
repositories {
    mavenCentral()
}
```

For snapshots:

```kotlin
repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}
```

If Gradle cannot resolve TeaVM artifacts from Central, add the TeaVM repository:

```kotlin
maven("http://teavm.org/maven/repository/") {
    isAllowInsecureProtocol = true
}
```

For plugin resolution, put the repositories in `pluginManagement` in `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }
}
```

The gdx-teavm plugin marker is published to Maven with the library artifacts, so Gradle Plugin Portal is not required for this plugin.

## Recommended: Gradle Plugin

Apply the plugin to the module that should produce TeaVM output.

```kotlin
plugins {
    id("com.github.xpenatan.gdx-teavm") version "-SNAPSHOT"
}
```

The extension block is named `gdxTeaVM`.

```kotlin
gdxTeaVM {
    assets("assets")
    classpathAssets("com/example/game/assets")
    reflection("com.example.game.save**")

    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
    }

    wasm {
        mainClass.set("com.example.game.teavm.WebLauncher")
    }
}
```

### Automatic Backend Dependencies

The plugin adds the required backend dependency for every declared target. For example, `js {}` or `wasm {}` adds `backend-web`, `glfw {}` adds `backend-glfw`, and `psp {}` adds `backend-psp`.

Those dependencies are added to both the normal Java `implementation` configuration and TeaVM's generation configuration. This means a standalone plugin module can compile launchers that import `WebApplication`, `GLFWApplication`, or `PSPApplication` without manually declaring the backend artifacts.

## Web Targets

Use `js {}` for JavaScript and `wasm {}` for Wasm.

```kotlin
import org.teavm.gradle.api.OptimizationLevel

gdxTeaVM {
    assets("assets")

    js {
        mainClass.set("com.example.game.teavm.WebLauncher")
        htmlTitle.set("My Game")
        htmlWidth.set(1280)
        htmlHeight.set(720)
        serverPort.set(8080)
        targetFileName.set("app.js")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(true)
    }

    wasm {
        mainClass.set("com.example.game.teavm.WebLauncher")
        htmlTitle.set("My Game")
        htmlWidth.set(1280)
        htmlHeight.set(720)
        serverPort.set(8080)
        targetFileName.set("app.wasm")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(true)
        copyRuntime.set(true)
        modularRuntime.set(false)
    }
}
```

Generated tasks:

| Task | Description |
| --- | --- |
| `gdx_teavm_web_js_build` | Build the JavaScript web app |
| `gdx_teavm_web_js_run` | Build and serve the JavaScript web app |
| `gdx_teavm_web_wasm_build` | Build the Wasm web app |
| `gdx_teavm_web_wasm_run` | Build and serve the Wasm web app |

Default output:

| Target | Output |
| --- | --- |
| JS | `build/dist/web/webapp` |
| Wasm | `build/dist/wasm/webapp` |

The run tasks use `JettyServer` from `backend-web`, not a separate HTTP server implementation.

## Native Targets

Declare `glfw {}` or `psp {}` for native backend tasks. Each native target block contains its own TeaVM C settings, so GLFW and PSP can keep different launcher classes, heap sizes, optimization levels, and backend-specific options.

```kotlin
import org.teavm.gradle.api.OptimizationLevel

gdxTeaVM {
    assets("assets")

    glfw {
        mainClass.set("com.example.game.teavm.GlfwLauncher")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        buildType.set("Debug")
        consoleLog.set(false)
    }

    psp {
        mainClass.set("com.example.game.teavm.PspLauncher")
        optimization.set(OptimizationLevel.NONE)
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        debugMemory.set(false)
    }
}
```

Generated GLFW tasks:

| Task | Description |
| --- | --- |
| `gdx_teavm_glfw_generate` | Generate the C project |
| `gdx_teavm_glfw_build` | Generate and build using `glfw.buildType` |
| `gdx_teavm_glfw_run` | Generate, build, and run using `glfw.buildType` |

Generated PSP tasks:

| Task | Description |
| --- | --- |
| `gdx_teavm_psp_generate` | Generate the PSP C project |
| `gdx_teavm_psp_build` | Generate and run the PSP build script |

Default output:

| Target | Output |
| --- | --- |
| GLFW | `build/dist/glfw` |
| PSP | `build/dist/psp` |

## Plugin Properties

See the [plugin property reference](plugin-properties.md) for every `gdxTeaVM` property, grouped by shared settings and by target backend.

## Manual Builder API

The builder API is based on `TeaBuilder` and a concrete backend.

### Web Builder

```kotlin
dependencies {
    implementation("com.github.xpenatan.gdx-teavm:backend-web:-SNAPSHOT")
    implementation(project(":core"))
}
```

```java
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildWeb {
    public static void main(String[] args) {
        WebBackend backend = new WebBackend()
                .setHtmlTitle("My Game")
                .setHtmlWidth(1280)
                .setHtmlHeight(720)
                .setStartJettyAfterBuild(true);

        new TeaBuilder(backend)
                .addAssets(new AssetFileHandle("assets"))
                .setMainClass("com.example.game.teavm.WebLauncher")
                .setOutputName("app")
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
```

For Wasm with the builder:

```java
WebBackend backend = new WebBackend()
        .setWebAssembly(true)
        .setStartJettyAfterBuild(true);
```

### GLFW Builder

```kotlin
dependencies {
    implementation("com.github.xpenatan.gdx-teavm:backend-glfw:-SNAPSHOT")
    implementation(project(":core"))
}
```

```java
import com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildGlfw {
    public static void main(String[] args) {
        TeaGLFWBackend backend = new TeaGLFWBackend()
                .setBuildType(TeaGLFWBackend.NativeBuildType.DEBUG)
                .setBuildExecutableAfterBuild(false)
                .setRunExecutableAfterBuild(false);

        new TeaBuilder(backend)
                .addAssets(new AssetFileHandle("assets"))
                .setMainClass("com.example.game.teavm.GlfwLauncher")
                .setOptimizationLevel(TeaVMOptimizationLevel.FULL)
                .setMinHeapSize(64 * 1024 * 1024)
                .setMaxHeapSize(512 * 1024 * 1024)
                .setMinDirectBuffersSize(64 * 1024 * 1024)
                .build(new File("build/dist"));
    }
}
```

### PSP Builder

```kotlin
dependencies {
    implementation("com.github.xpenatan.gdx-teavm:backend-psp:-SNAPSHOT")
    implementation(project(":core"))
}
```

```java
import com.github.xpenatan.gdx.teavm.backends.psp.config.backend.TeaPSPBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildPsp {
    public static void main(String[] args) {
        TeaPSPBackend backend = new TeaPSPBackend();
        backend.debugMemory = true;
        backend.autoExecuteBuild = false;

        new TeaBuilder(backend)
                .addAssets(new AssetFileHandle("assets"))
                .setMainClass("com.example.game.teavm.PspLauncher")
                .setDebugInformationGenerated(true)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .build(new File("build/dist"));
    }
}
```

## Assets

All build paths share the same asset planner/copy logic.

- Disk assets are copied as libGDX internal assets.
- Classpath assets are copied and marked as classpath assets in the preload manifest.
- The generated manifest is `assets/preload.txt`.
- The manifest stores enough file type information for runtime loading to preserve `Gdx.files.internal(...)` and `Gdx.files.classpath(...)` behavior.

Builder examples:

```java
teaBuilder.addAssets(new AssetFileHandle("assets"));
teaBuilder.addAssets(new AssetFileHandle("com/example/game/skin", FileType.Classpath));
```

Plugin examples:

```kotlin
gdxTeaVM {
    assets("assets")
    classpathAssets("com/example/game/skin")
}
```

Libraries can contribute runtime files with `META-INF/gdx-teavm.properties`:

```properties
resources=runtime/file.js
classpath-resources=com/example/library/shaders
ignore-resources=unused/file.txt
```

## Reflection

TeaVM needs ahead-of-time metadata for reflection. gdx-teavm generates the metadata used by the libGDX reflection emulation classes.

Builder:

```java
new TeaBuilder(backend)
        .addReflectionClass("com.example.game.save**")
        .addReflectionClass("com.badlogic.gdx.math.Vector2");
```

Plugin:

```kotlin
gdxTeaVM {
    reflection("com.example.game.save**")
    reflectionDebug.set(false)
}
```

Use concrete class names for individual types and package patterns ending in `**` for package trees.

## Example Project Tasks

Plugin examples:

```shell
./gradlew :examples:basic:plugin:gdx_teavm_web_js_run
./gradlew :examples:basic:plugin:gdx_teavm_web_wasm_run
./gradlew :examples:basic:plugin:gdx_teavm_glfw_generate
./gradlew :examples:basic:plugin:gdx_teavm_glfw_build
./gradlew :examples:basic:plugin:gdx_teavm_psp_generate
./gradlew :examples:basic:plugin:gdx_teavm_psp_build
```

Manual builder examples:

```shell
./gradlew :examples:basic:web:basic_run_web
./gradlew :examples:basic:glfw:basic_generate_teavm_glfw
./gradlew :examples:basic:glfw:basic_build_teavm_glfw_debug
./gradlew :examples:basic:psp:basic_build_teavm_psp
```

## Snapshot Testing In A Standalone Project

Use the snapshot repository in both `pluginManagement` and regular repositories:

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }
}
```

```kotlin
plugins {
    id("com.github.xpenatan.gdx-teavm") version "-SNAPSHOT"
}
```

The plugin marker and implementation artifacts are published to Maven with the rest of the project artifacts.

## Publishing

Root publishing tasks:

```shell
./gradlew prepareSnapshotDeploy
./gradlew prepareReleaseDeploy
./gradlew publishSnapshot
./gradlew publishRelease
```

Local prepare tasks write Maven repository files under `build/snapshot-deploy` or `build/staging-deploy`.

Required environment variables for remote publishing/signing:

| Variable | Purpose |
| --- | --- |
| `CENTRAL_PORTAL_USERNAME` | Central Portal username/token username |
| `CENTRAL_PORTAL_PASSWORD` | Central Portal password/token password |
| `SIGNING_KEY` | ASCII-armored PGP private key |
| `SIGNING_PASSWORD` | PGP private key password |
