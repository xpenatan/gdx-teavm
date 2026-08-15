# Generated iOS Project

Create this project with:

```bash
./gradlew ${IOS_GRADLE_PROJECT_PATH}:${IOS_GRADLE_TASK_PREFIX}_init_xcode
```

Generate or refresh the TeaVM C/assets with:

```bash
./gradlew ${IOS_GRADLE_PROJECT_PATH}:${IOS_GRADLE_TASK_PREFIX}_generate
```

Then open it with:

```bash
./gradlew ${IOS_GRADLE_PROJECT_PATH}:${IOS_GRADLE_TASK_PREFIX}_open_xcode
```

Run the `GdxTeaVMIOSSpike` target on an iOS simulator first. For a physical device, set your Apple development team in Xcode's Signing & Capabilities tab.

The generated project builds the TeaVM CMake target first, then links its bundled static archive into the app. Native C extensions contributed by dependencies are included in that archive.

The default generated graphics API is ANGLE through MetalANGLEKit. Set the target's `graphicsApi` or Gradle property `gdx.teavm.ios.graphicsApi` to `gles` and run `${IOS_GRADLE_TASK_PREFIX}_regenerate_xcode` to generate the native OpenGL ES / GLKit project instead.
