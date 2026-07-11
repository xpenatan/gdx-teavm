# gdx-teavm WebSockets iOS

This module is an experimental iOS websocket example using `gdx-websockets:teavm-ios`.

Generate the TeaVM C payload:

```bash
./gradlew :examples:websockets:ios:gdx_teavm_ios_generate
```

Initialize the Xcode project:

```bash
./gradlew :examples:websockets:ios:gdx_teavm_ios_init_xcode
```

Open the generated Xcode project:

```bash
./gradlew :examples:websockets:ios:gdx_teavm_ios_open_xcode
```

Command-line simulator test on macOS:

```bash
./gradlew :examples:websockets:ios:gdx_teavm_ios_run_simulator
```

Generated output is written under `build/dist/ios`. The default graphics API is `angle`, which downloads the pinned MetalANGLEKit framework bundle. Set `ios.graphicsApi` or Gradle property `gdx.teavm.ios.graphicsApi=gles` to use the older OpenGL ES / GLKit template.
