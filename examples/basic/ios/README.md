# gdx-teavm iOS Spike

This module is a local experiment for the iOS runtime classes in `backend-ios`.
iOS plugin support is exposed as experimental WIP code. It can generate the TeaVM C/assets payload and create an Xcode project from the backend templates.

Generate the TeaVM C payload:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_generate
```

Initialize the Xcode project:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_init_xcode
```

Open the generated Xcode project:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_open_xcode
```

Command-line simulator test on macOS:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_run_simulator
```

Generated output is written under `build/dist/ios`. The default graphics API is `angle`, which downloads the pinned MetalANGLEKit framework bundle. Set `ios.graphicsApi` or Gradle property `gdx.teavm.ios.graphicsApi=gles` to use the older OpenGL ES / GLKit template.
