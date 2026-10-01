# Examples

Each example keeps portable application code in `core` and runnable implementations in `platforms`.
Only leaf projects under `platforms` contain launchers and runnable Gradle tasks; the intermediate directories organize runtime families and implementations.

```text
<example>/
|-- assets/                         # Optional shared runtime assets
|-- core/                           # Portable application code
`-- platforms/
    |-- desktop/
    |   |-- lwjgl3/                 # JVM/LWJGL3
    |   |-- graalvm/                # GraalVM Native Image/LWJGL3
    |   `-- teavm-c/
    |       |-- builder/            # Manual TeaBuilder API
    |       `-- plugin/             # gdx-teavm Gradle plugin
    |-- web/
    |   |-- builder/
    |   `-- plugin/
    |-- android/
    `-- ios/
```

A leaf is omitted when an example does not support that runtime or build style.
Platform leaves depend directly on their example's `core`; they do not depend on sibling platform projects.
Reusable example-only instrumentation lives in the sibling `shared` project and is consumed by the example core projects.

## CI runtime proofs

The `Runtime Examples` workflow publishes one `runtime-examples-proof` artifact per run. Its directories are organized by platform and example, each proof includes the raw `fps.log`, and `fps-summary.md` reports the sample count, average, minimum, maximum, and latest FPS captured from each runtime's libGDX `FPSLogger` output.

## Available examples

| Example | Purpose |
| --- | --- |
| [Basic](basic/README.md) | Core libGDX rendering, UI, assets, reflection, web, and native targets |
| [FreeType](freetype/README.md) | FreeType extension across web and native targets |
| [Controllers](controllers/README.md) | Controller extension across desktop, web, Android, and iOS |
| [gdx-tests](gdx-tests/README.md) | libGDX test suite integration; enabled with `includeLibgdxSource` |

## TeaVM reflection configuration

TeaVM platform projects keep their application-owned policy beside the launcher: `src/main/java/example/policy` for web, desktop C, and iOS, or `src/native/java/example/policy` for Android. Plugin projects use `@Autoregistered` with the automatically supplied processor; manual builders register their policy in `src/main/resources/META-INF/services`. There are no separate policy modules or explicit processor dependencies. Native controller launchers also preserve the manager class they load by name.

Basic platform policies reuse the former library defaults with `GdxReflectionHelper.applyDefaults(this::selectClasses)` and add application-specific rules. The helper is opt-in and does not register or preserve classes automatically.

See [reflection setup](../docs/usage.md#reflection) for registration, preservation, and pinned TeaVM limitations.
