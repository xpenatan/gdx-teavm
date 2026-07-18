# FreeType TeaVM C Desktop Example

This example runs the shared `examples:freetype:core` demo through the TeaVM C GLFW backend.

FreeType support comes from the `extensions:c:gdx-freetype-c` module. When that dependency is present, the generated CMake project fetches and links FreeType for the native bridge.

Generate C sources:

```shell
gradlew :examples:freetype:platforms:desktop:teavm-c:builder:freetype_desktop_c_generate
```

Build a native executable:

```shell
gradlew :examples:freetype:platforms:desktop:teavm-c:builder:freetype_desktop_c_debug_build
gradlew :examples:freetype:platforms:desktop:teavm-c:builder:freetype_desktop_c_release_build
```

Run it through Gradle:

```shell
gradlew :examples:freetype:platforms:desktop:teavm-c:builder:freetype_desktop_c_debug_run
gradlew :examples:freetype:platforms:desktop:teavm-c:builder:freetype_desktop_c_release_run
gradlew :examples:freetype:platforms:desktop:teavm-c:builder:freetype_desktop_c_release_console_run
```

The example reuses assets from `examples/freetype/assets` and writes generated files under `examples/freetype/platforms/desktop/teavm-c/builder/build/dist`.
