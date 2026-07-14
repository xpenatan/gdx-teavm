# gdx-teavm Usage Guide

This guide covers the two supported ways to build with `gdx-teavm`:

- The Gradle plugin, recommended for normal application projects.
- The manual builder API, useful for custom build launchers and backend development.

## Versions

The repository keeps dependency versions in `buildSrc/src/main/kotlin/LibExt.kt`.

| Component | Current version |
| --- | --- |
| libGDX | `1.14.2` |
| TeaVM | `0.15.0` |
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
maven("https://teavm.org/maven/repository/")
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

The plugin adds the required backend dependency for every declared target. For example, `js {}` or `wasm {}` adds `backend-web`, `glfw {}` adds `backend-glfw`, `ios {}` adds `backend-ios`, and `android {}` adds `backend-android`.

Those dependencies are added to both the normal Java `implementation` configuration and TeaVM's generation configuration. This means a standalone plugin module can compile launchers that import `WebApplication` or `GLFWApplication` without manually declaring the backend artifacts.

Android is different because the Android Gradle Plugin owns APK packaging, install tasks, build types, signing, manifests, resources, and native CMake execution. Apply `gdx-teavm` to a real Android application module and declare an `android {}` target there; the plugin generates the TeaVM C/CMake payload, while Android Gradle tasks build and install the APK.

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
| JS | `build/dist/js/webapp` |
| Wasm | `build/dist/wasm/webapp` |

The run tasks use `JettyServer` from `backend-web`, not a separate HTTP server implementation.

## Native Targets

Declare `glfw {}` for desktop native backend tasks. An experimental `ios {}` block is also available for WIP TeaVM C payloads. Each native target block contains its own TeaVM C settings, so plugin targets can keep different launcher classes, heap sizes, optimization levels, and backend-specific options.

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

    ios {
        mainClass.set("com.example.game.teavm.IosLauncher")
        optimization.set(OptimizationLevel.NONE)
        xcodeProjectDir.set(layout.buildDirectory.dir("dist/ios/xcode"))
    }
}
```

Generated native plugin tasks:

| Task | Description |
| --- | --- |
| `gdx_teavm_glfw_generate` | Generate the C project |
| `gdx_teavm_glfw_build` | Generate and build using `glfw.buildType` |
| `gdx_teavm_glfw_run` | Generate, build, and run using `glfw.buildType` |
| `gdx_teavm_ios_generate` | Generate the experimental iOS C/assets |
| `gdx_teavm_ios_prepare_angle` | Download and extract the MetalANGLEKit frameworks used by `ios.graphicsApi=angle` |
| `gdx_teavm_ios_init_xcode` | Create the experimental iOS Xcode project if missing |
| `gdx_teavm_ios_regenerate_xcode` | Regenerate the experimental iOS Xcode project, overwriting manual project edits |
| `gdx_teavm_ios_open_xcode` | Create the experimental iOS Xcode project if missing and open it in Xcode |
| `gdx_teavm_ios_build_simulator` | Generate and build the experimental iOS app for the simulator |
| `gdx_teavm_ios_run_simulator` | Generate, build, install, and launch the experimental iOS app on a simulator |

Default output:

| Target | Output |
| --- | --- |
| GLFW | `build/dist/glfw` |
| iOS | `build/dist/ios` |

## Android Target

Android builds use a real Android application module, not a generated Android project. The Android module applies both `com.android.application` and `com.github.xpenatan.gdx-teavm`, owns normal Android settings, and points Android's external native build at the generated TeaVM CMake project.

The TeaVM launcher lives in `src/native/java` so it is used for TeaVM C generation. The gdx-teavm plugin registers this folder with Android's main Java source model so Android Studio imports it as source code, mirrors the `teavm` dependencies onto Android's compile-only IDE classpath, and excludes the folder from Android's APK Java compile. This keeps imports and completion working after a Gradle sync without packaging the launcher into the APK.

The Android runtime bridge is provided by `backend-android`. The plugin adds generated Java sources for `com.github.xpenatan.gdx.teavm.android.TeaAndroidActivity` and `TeaAndroidView` to the Android app compile, so projects can use a minimal Activity subclass for the default full-screen app case.

```kotlin
plugins {
    id("com.android.application")
    id("com.github.xpenatan.gdx-teavm")
}

val generatedAndroidCMakeFile = layout.buildDirectory.file("generated/gdx-teavm/android/CMakeLists.txt")
val androidCxxDir = rootProject.layout.buildDirectory.dir("android-cxx/my-game-android")

android {
    namespace = "com.example.game.android"

    defaultConfig {
        applicationId = "com.example.game.android"
        minSdk = 21
        targetSdk = 36
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    externalNativeBuild {
        cmake {
            path = generatedAndroidCMakeFile.get().asFile
            buildStagingDirectory = androidCxxDir.get().asFile
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDir(file("../assets"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    add("teavm", project(":core"))
}

gdxTeaVM {
    android {
        mainClass.set("com.example.game.teavm.AndroidLauncher")
    }
}
```

Generated Android plugin task:

| Task | Description |
| --- | --- |
| `gdx_teavm_android_generate` | Generate the TeaVM C/CMake payload for the Android module |

There is no `gdx_teavm_android_run` task. Android Debug, Release, install, launch, signing, and emulator/device behavior use standard Android Gradle Plugin and ADB tooling.

Common Android tasks:

| Task | Description |
| --- | --- |
| `assembleDebug` | Generate TeaVM C, build native Debug code, and package a debug APK |
| `assembleRelease` | Generate TeaVM C, build native Release code, and package release APKs |
| `installDebug` | Build and install the debug APK on the visible device or emulator |

The Android module should point assets at the normal asset source directory. The gdx-teavm Android generation step does not copy game assets into the generated native payload.

Default Activity subclass:

```java
package com.example.game.android;

import com.github.xpenatan.gdx.teavm.android.TeaAndroidActivity;

public class MainActivity extends TeaAndroidActivity {
}
```

Manifest entry:

```xml
<activity
    android:name="com.example.game.android.MainActivity"
    android:configChanges="keyboard|keyboardHidden|navigation|orientation|screenSize"
    android:exported="true"
    android:screenOrientation="landscape">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

For embedded usage, add `TeaAndroidView` to your own Activity or layout instead of using the default full-screen Activity:

```java
TeaAndroidView gdxView = new TeaAndroidView(this);
container.addView(gdxView);
```

Forward the host Activity lifecycle to the view:

```java
@Override
protected void onPause() {
    gdxView.onPause();
    super.onPause();
}

@Override
protected void onResume() {
    super.onResume();
    gdxView.onResume();
}

@Override
protected void onDestroy() {
    gdxView.dispose();
    super.onDestroy();
}
```

Only one active `TeaAndroidView` is supported per process for now because libGDX globals such as `Gdx.app`, `Gdx.graphics`, and the TeaVM Android application instance are process-global.

### Testing The Basic Android Example

Start a visible emulator from Android Studio Device Manager, or plug in a real Android device. Then verify that ADB sees one device in the `device` state:

```shell
adb devices -l
```

Build the debug APK:

```shell
./gradlew :examples:basic:android:assembleDebug
```

Install it on the connected device or running emulator:

```shell
./gradlew :examples:basic:android:installDebug
```

Launch it from the Android launcher as `gdx-teavm basic`, or start the Activity directly with ADB:

```shell
adb shell am start -n com.github.xpenatan.gdx.teavm.examples.basic.android/com.github.xpenatan.gdx.teavm.examples.basic.android.MainActivity
```

Build the release APKs:

```shell
./gradlew :examples:basic:android:assembleRelease
```

The basic Android demo enables ABI APK splits and disables the universal APK. Debug and release outputs use one APK per ABI:

| Build type | ABI | APK |
| --- | --- | --- |
| Debug | `arm64-v8a` | `examples/basic/android/build/outputs/apk/debug/android-arm64-v8a-debug.apk` |
| Debug | `armeabi-v7a` | `examples/basic/android/build/outputs/apk/debug/android-armeabi-v7a-debug.apk` |
| Debug | `x86` | `examples/basic/android/build/outputs/apk/debug/android-x86-debug.apk` |
| Debug | `x86_64` | `examples/basic/android/build/outputs/apk/debug/android-x86_64-debug.apk` |
| Release | `arm64-v8a` | `examples/basic/android/build/outputs/apk/release/android-arm64-v8a-release.apk` |
| Release | `armeabi-v7a` | `examples/basic/android/build/outputs/apk/release/android-armeabi-v7a-release.apk` |
| Release | `x86` | `examples/basic/android/build/outputs/apk/release/android-x86-release.apk` |
| Release | `x86_64` | `examples/basic/android/build/outputs/apk/release/android-x86_64-release.apk` |

For Play Store distribution, prefer `bundleRelease` and upload the generated Android App Bundle so Play can serve the right ABI split to each device:

```shell
./gradlew :examples:basic:android:bundleRelease
```

Generated TeaVM C output is written under `examples/basic/android/build/generated/gdx-teavm/android`. Android CMake/Ninja staging is configured under the root build folder at `build/android-cxx/examples-basic-android` so AGP's native intermediates do not create an untracked `.cxx` folder in the Android module root.

The Android app module uses Java 8 source and target compatibility for APK-side Java code. This avoids Android Gradle Plugin's Java 9+ `androidJdkImage` transform, which can fail when Gradle is running on newer JDKs such as GraalVM JDK 25. The TeaVM launcher source in `src/native/java` is compiled separately for TeaVM generation.

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

### iOS Plugin Target

iOS is experimental. The Gradle plugin exposes `ios {}` for TeaVM C/assets generation, Xcode project initialization, and simulator build/run tasks. Xcode and simulator tasks require macOS.

```kotlin
gdxTeaVM {
    ios {
        mainClass.set("com.example.game.teavm.IosLauncher")
        graphicsApi.set("angle")
    }
}
```

Use `gdx_teavm_ios_init_xcode` to create the Xcode project from the templates under `backend-ios`. The default `graphicsApi` is `angle`, which downloads libGDX's pinned MetalANGLEKit bundle and renders GLES through Metal. Set `graphicsApi` to `gles` to generate the older native OpenGL ES / GLKit project.

## Assets

All build paths share the same asset planner/copy logic.

- Disk assets are copied as libGDX internal assets.
- Classpath assets are copied and marked as classpath assets in the generated preload manifest.
- Web targets compile the preload manifest into the TeaVM output through a generated runtime class instead of writing an `assets/preload.txt` file.
- Native targets copy assets directly and do not create a preload manifest file.
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
./gradlew :examples:freetype:plugin:gdx_teavm_web_js_run
./gradlew :examples:controllers:plugin:gdx_teavm_web_js_run
```

Android example:

```shell
./gradlew :examples:basic:android:assembleDebug
./gradlew :examples:basic:android:installDebug
./gradlew :examples:basic:android:assembleRelease
```

Manual builder examples:

```shell
./gradlew :examples:basic:web:basic_web_run
./gradlew :examples:freetype:web:freetype_web_run
./gradlew :examples:controllers:web:controllers_web_run
./gradlew :examples:basic:desktop-c:basic_desktop_c_generate
./gradlew :examples:basic:desktop-c:basic_desktop_c_debug_build
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
