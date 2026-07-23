# Android Controller Verification

## Verified setup

The controller demo was verified with a MuMu Android device at ADB target `127.0.0.1:16384`. MuMu was used because gamepad passthrough is not reliable in every Android Studio emulator setup.

The captured run shows the controller in the demo UI and a button-down event reaching libGDX:

![MuMu controller success](docs/mumu-controller-success.png)

## Reproduce

Build and install:

```bat
gradlew.bat :examples:controllers:platforms:android:installDebug
```

Launch:

```bat
adb -s 127.0.0.1:16384 shell am start -n com.github.xpenatan.gdx.teavm.examples.controllers.android/com.github.xpenatan.gdx.teavm.examples.controllers.android.MainActivity
```
