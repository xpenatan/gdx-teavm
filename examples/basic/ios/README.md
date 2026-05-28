# gdx-teavm iOS Spike

This module is a smoke test for running TeaVM C output from a Swift iOS app. It includes the current iOS GL bridge and can generate either a MetalANGLEKit-backed ANGLE Xcode project or the older native OpenGL ES / GLKit project.

Initialize the Xcode project:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_init_xcode
```

Generate the TeaVM C payload:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_generate
```

Generated output:

- `build/dist/ios/c/src/app_include.c`
- `build/dist/ios/c/src/ios_bridge.h`
- `build/dist/ios/c/src/ios_bridge.c`
- `build/dist/ios/CMakeLists.txt`
- `build/dist/ios/metadata.properties`
- `build/dist/ios/xcode/GdxTeaVMIOSSpike.xcodeproj`

The Xcode project location is controlled by `ios.xcodeProjectDir`. Set it to a directory outside `build`, such as `layout.projectDirectory.dir("xcode")`, when the Xcode project should be committed to git.

The generated graphics API is controlled by `ios.graphicsApi` or Gradle property `gdx.teavm.ios.graphicsApi`. The default is `angle`, which downloads libGDX's pinned MetalANGLEKit bundle and renders GLES through Metal. Set it to `gles` to generate the native OpenGL ES / GLKit project instead.

Open in Xcode:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_open_xcode
```

Then:

1. Select an iOS simulator.
2. Run the `GdxTeaVMIOSSpike` target.

Command-line simulator test:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_run_simulator
```

The run task ensures the Xcode project exists, generates TeaVM C/assets, builds the Debug simulator app, opens Simulator.app, installs the app, and launches it. The default simulator is `iPhone 12 Pro`.

For a physical device, set your Apple development team in Xcode's Signing & Capabilities tab.

Expected success signal is the selected libGDX test rendering in the simulator. The current launcher is useful for validating the Swift -> C -> TeaVM Java path and the selected iOS graphics API.
