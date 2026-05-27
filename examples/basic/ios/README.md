# gdx-teavm iOS Spike

This module is a first smoke test for running TeaVM C output from a Swift iOS app. It does not implement GL, audio, files in an app bundle, or iOS packaging yet.

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

Mac-side Swift test:

1. Open `build/dist/ios/xcode/GdxTeaVMIOSSpike.xcodeproj` in Xcode.
2. Select an iOS simulator.
3. Run the `GdxTeaVMIOSSpike` target.

For a physical device, set your Apple development team in Xcode's Signing & Capabilities tab.

Expected first success signal is Xcode console output from `TestIOSLauncher`: create, resize, and frame logs. This example intentionally avoids GL calls so we can prove the Swift -> C -> TeaVM Java path before adding graphics bindings.
