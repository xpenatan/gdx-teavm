# gdx-teavm

![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/snapshot.yml/badge.svg)
[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.xpenatan.gdx-teavm/backend-web)](https://central.sonatype.com/namespace/com.github.xpenatan.gdx-teavm)
[![Snapshot](https://img.shields.io/badge/snapshot--SNAPSHOT-red)](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/com/github/xpenatan/gdx-teavm/)

`gdx-teavm` is a TeaVM backend set for running [libGDX](https://libgdx.com/) applications outside the JVM. It can generate browser builds with JavaScript or Wasm, native C projects for GLFW and PSP, and Android native app builds through an Android application module.

The project provides two build styles:

- **Gradle plugin**: the recommended workflow for application projects. It applies TeaVM, configures the selected backend, copies assets, prepares generated output, and creates `gdx_teavm_*` tasks.
- **Manual builder API**: a Java launcher API for advanced builds and custom tooling. It directly uses `TeaBuilder` with `WebBackend`, `TeaGLFWBackend`, or `TeaPSPBackend`.

## Status

| gdx-teavm | libGDX | TeaVM  |
|:---------:|:------:|:------:|
| -SNAPSHOT | 1.14.1 | 0.14.1 |
| 1.5.5 | 1.14.0 | 0.14.0 |
| 1.5.4 | 1.14.0 | 0.13.1 |

## Modules

| Module | Purpose |
| --- | --- |
| `backend-shared` | Shared compiler, asset, resource, and reflection support |
| `backend-web` | libGDX web runtime plus JavaScript and Wasm build support |
| `backend-glfw` | TeaVM C output for desktop GLFW native builds |
| `backend-psp` | TeaVM C output for PSP builds |
| `backend-android` | TeaVM C output and runtime support for Android app modules |
| `gdx-freetype-web` | FreeType support for TeaVM web builds |
| `gdx-controllers-web` | Controller support for TeaVM web builds |
| `tools/gdx-teavm-plugin` | Gradle plugin implementation |

## Documentation

- [Usage guide](docs/usage.md): setup, plugin examples, builder examples, tasks, and publishing notes.
- [Plugin property reference](docs/plugin-properties.md): every `gdxTeaVM` property, grouped by shared, web, JS, Wasm, GLFW, and PSP settings.
- [Backend architecture](docs/backend-architecture.md): how the builder, Gradle plugin, TeaVM plugins, assets, and reflection work together.
- [GLFW native build guide](examples/basic/glfw/README.md): native toolchain requirements and manual CMake workflow.

## Repositories

Release artifacts are published to Maven Central. Snapshots are published to Central Portal snapshots.

```kotlin
repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")

    // TeaVM artifacts may be needed while using TeaVM snapshots or non-central builds.
    maven("http://teavm.org/maven/repository/") {
        isAllowInsecureProtocol = true
    }
}
```

When using the Gradle plugin from Maven, add the same repositories to `pluginManagement` in `settings.gradle.kts`.

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        maven("https://central.sonatype.com/repository/maven-snapshots/")
    }
}
```

The gdx-teavm plugin marker is published to Maven with the rest of the artifacts, so Gradle Plugin Portal is not required for this plugin.

## Gradle Plugin Quick Start

Apply the plugin in the TeaVM target module:

```kotlin
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
    }

    wasm {
        mainClass.set("com.example.game.teavm.WebLauncher")
    }
}
```

Run:

```shell
./gradlew gdx_teavm_web_js_run
./gradlew gdx_teavm_web_wasm_run
```

The web run tasks build the app, copy assets, generate the web app files, and serve the output with the backend Jetty server. The plugin adds the required gdx-teavm backend dependencies automatically for each declared target.

For native targets and every available property, see the [usage guide](docs/usage.md) and [plugin property reference](docs/plugin-properties.md). The plugin creates tasks only for the target blocks you declare.

## Manual Builder Quick Start

Use the builder API when you need full programmatic control or a custom build launcher. See the [usage guide](docs/usage.md) for complete builder examples.

```kotlin
dependencies {
    implementation("com.github.xpenatan.gdx-teavm:backend-web:-SNAPSHOT")
    implementation(project(":core"))
}
```

Run the Java launcher from Gradle or your IDE. Builder projects add the concrete backend dependency they use, such as `backend-web`, `backend-glfw`, or `backend-psp`.

## Examples In This Repository

```shell
# Plugin workflow
./gradlew :examples:basic:plugin:gdx_teavm_web_js_run
./gradlew :examples:basic:plugin:gdx_teavm_web_wasm_run
./gradlew :examples:basic:plugin:gdx_teavm_glfw_generate
./gradlew :examples:basic:plugin:gdx_teavm_psp_generate

# Android workflow
./gradlew :examples:basic:android:assembleDebug
./gradlew :examples:basic:android:installDebug

# FreeType plugin workflow
./gradlew :examples:freetype:web:gdx_teavm_web_js_run
./gradlew :examples:freetype:web:gdx_teavm_web_wasm_run

# Manual builder workflow
./gradlew :examples:basic:web:basic_run_web
./gradlew :examples:basic:glfw:basic_build_teavm_glfw_debug
./gradlew :examples:basic:psp:basic_build_teavm_psp
```

## Support

If this project is useful to you, consider [sponsoring](https://github.com/sponsors/xpenatan) its development.

## License

`gdx-teavm` is licensed under the [Apache License 2.0](LICENSE).
