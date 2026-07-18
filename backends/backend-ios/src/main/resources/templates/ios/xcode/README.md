# Generated iOS Project

Create this project with:

```bash
./gradlew ${IOS_GRADLE_PROJECT_PATH}:gdx_teavm_ios_init_xcode
```

Generate or refresh the TeaVM C/assets with:

```bash
./gradlew ${IOS_GRADLE_PROJECT_PATH}:gdx_teavm_ios_generate
```

Then open it with:

```bash
./gradlew ${IOS_GRADLE_PROJECT_PATH}:gdx_teavm_ios_open_xcode
```

Run the `GdxTeaVMIOSSpike` target on an iOS simulator first. For a physical device, set your Apple development team in Xcode's Signing & Capabilities tab.

The generated project builds the TeaVM CMake target first, then links its bundled static archive into the app. Native C extensions contributed by dependencies are included in that archive.

The default generated graphics API is ANGLE through MetalANGLEKit. Set `ios.graphicsApi` or Gradle property `gdx.teavm.ios.graphicsApi` to `gles` and run `gdx_teavm_ios_regenerate_xcode` to generate the native OpenGL ES / GLKit project instead.
