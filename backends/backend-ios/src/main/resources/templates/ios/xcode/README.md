# Generated iOS Smoke Project

Create this project with:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_init_xcode
```

Generate or refresh the TeaVM C/assets with:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_generate
```

Then open:

```bash
examples/basic/ios/build/dist/ios/xcode/GdxTeaVMIOSSpike.xcodeproj
```

Run the `GdxTeaVMIOSSpike` target on an iOS simulator first. For a physical device, set your Apple development team in Xcode's Signing & Capabilities tab.

This is a smoke project. It proves Swift can start TeaVM C and drive resize, render, pause, resume, dispose, touch callbacks, packaged assets, and the selected graphics API.

The default generated graphics API is ANGLE through MetalANGLEKit. Set `ios.graphicsApi` or Gradle property `gdx.teavm.ios.graphicsApi` to `gles` and run `gdx_teavm_ios_regenerate_xcode` to generate the native OpenGL ES / GLKit project instead.
