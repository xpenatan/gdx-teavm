# gdx-teavm

![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/snapshot.yml/badge.svg)
[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.xpenatan.gdx-teavm/backend-web)](https://central.sonatype.com/namespace/com.github.xpenatan.gdx-teavm)
[![Snapshot](https://img.shields.io/badge/snapshot--SNAPSHOT-red)](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/com/github/xpenatan/gdx-teavm/)

`gdx-teavm` is a TeaVM backend set for running [libGDX](https://libgdx.com/) applications outside the JVM. It can generate browser builds with JavaScript or Wasm, native C projects for GLFW, experimental iOS native payloads, and Android native app builds through an Android application module.

The project provides two build styles:

- **Gradle plugin**: the recommended workflow for application projects. It applies TeaVM, configures the selected backend, copies assets, prepares generated output, and creates `gdx_teavm_*` tasks.
- **Manual builder API**: a Java launcher API for advanced builds and custom tooling. It directly uses `TeaBuilder` with `WebBackend` or `TeaGLFWBackend`.

## Status

| gdx-teavm | libGDX | TeaVM  |
|:---------:|:------:|:------:|
| -SNAPSHOT | 1.14.2 | 0.15.0 |
|   1.6.1   | 1.14.2 | 0.15.0 |
|   1.5.5   | 1.14.0 | 0.14.0 |
|   1.5.4   | 1.14.0 | 0.13.1 |

## Modules

| Module | Purpose |
| --- | --- |
| `backend-shared` | Shared compiler, asset, resource, and reflection support |
| `backend-web` | libGDX web runtime plus JavaScript and Wasm build support |
| `backend-glfw` | TeaVM C output for desktop GLFW native builds |
| `backend-ios` | Experimental TeaVM C output and runtime support for iOS builds |
| `backend-android` | TeaVM C output and runtime support for Android app modules |
| `gdx-freetype-web` | FreeType support for TeaVM web builds |
| `gdx-freetype-c` | FreeType support for TeaVM C native builds |
| `gdx-controllers-web` | Controller support for TeaVM web builds |
| `gdx-controllers-ios` | GameController support for TeaVM C iOS builds |
| `tools/gdx-teavm-plugin` | Gradle plugin implementation |

## Documentation

- [Usage guide](docs/usage.md): setup, plugin examples, builder examples, and tasks.
- [Plugin property reference](docs/plugin-properties.md): every `gdxTeaVM` property, grouped by shared and target-specific settings.
- [Backend architecture](docs/backend-architecture.md): how the builder, Gradle plugin, TeaVM plugins, assets, and reflection work together.
- [Examples](examples/README.md): portable cores and runnable platform implementations.
- [Desktop C native build guide](examples/basic/platforms/desktop/teavm-c/builder/README.md): native toolchain requirements and manual CMake workflow.

## Setup

Release artifacts are available from Maven Central. Snapshot and plugin repository configuration is documented once in the [usage guide](docs/usage.md#repositories).

## Gradle Plugin Quick Start

Apply the plugin in the TeaVM target module:

```kotlin
plugins {
    id("com.github.xpenatan.gdx-teavm") version "<latest-release>"
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:1.14.2")
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

Use the builder API when you need full programmatic control or a custom build launcher. The [manual builder guide](docs/usage.md#manual-builder-api) contains dependencies and complete Web and GLFW examples.

## Examples In This Repository

The [examples index](examples/README.md) describes the project layout and links to the task inventory for each example.

## Support

If this project is useful to you, consider [sponsoring](https://github.com/sponsors/xpenatan) its development.

## License

`gdx-teavm` is licensed under the [Apache License 2.0](LICENSE).
