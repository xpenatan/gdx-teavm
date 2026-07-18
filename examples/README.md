# Examples

Each example keeps portable application code in `core` and runnable implementations in `platforms`.
Only leaf projects under `platforms` contain launchers and runnable Gradle tasks; the intermediate directories organize runtime families and implementations.

```text
<example>/
|-- assets/                         # Optional shared runtime assets
|-- core/                           # Portable application code
`-- platforms/
    |-- desktop/
    |   |-- lwjgl3/
    |   |-- graalvm/
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

## Available examples

| Example | Purpose |
| --- | --- |
| [Basic](basic/README.md) | Core libGDX rendering, UI, assets, reflection, web, and native targets |
| [FreeType](freetype/README.md) | FreeType extension across web and native targets |
| [Controllers](controllers/README.md) | Controller extension across desktop, web, Android, and iOS |
| [gdx-tests](gdx-tests/README.md) | libGDX test suite integration; enabled with `includeLibgdxSource` |
