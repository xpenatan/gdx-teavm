# gdx-teavm iOS Spike

This module is a local experiment for the iOS runtime classes in `backend-ios`.
The iOS backend is not exposed by the Gradle plugin and is not part of the
published artifact set.

Use the module as a compile check for the current launcher/runtime wiring:

```bash
./gradlew :examples:basic:ios:compileJava
```

The Swift/Xcode template resources remain in
`backends/backend-ios/src/main/resources/templates/ios/xcode` for local
experimentation.
