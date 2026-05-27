# Generated iOS Smoke Project

Open this project after running:

```bash
./gradlew :examples:basic:ios:gdx_teavm_ios_generate
```

Then open:

```bash
examples/basic/ios/build/dist/ios/xcode/GdxTeaVMIOSSpike.xcodeproj
```

Run the `GdxTeaVMIOSSpike` target on an iOS simulator first. For a physical device, set your Apple development team in Xcode's Signing & Capabilities tab.

This is a smoke project. It proves Swift can start TeaVM C and drive resize, render, pause, resume, dispose, and touch callbacks. It does not include GL/Metal, audio, packaged app assets, or a production bundle setup yet.
