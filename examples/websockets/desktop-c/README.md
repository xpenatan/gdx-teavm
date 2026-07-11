# WebSockets TeaVM C Desktop Example

This example runs the shared `examples:websockets:core` demo through the TeaVM C GLFW backend.

The websocket TeaVM desktop-c backend is provided by `gdx-websockets:teavm-desktop-c`; this example only shows how to consume it from a `gdx-teavm` desktop-c build.

## Build and Run

Generate C sources:

```shell
gradlew :examples:websockets:desktop-c:websockets_desktop_c_generate
```

Build a native executable:

```shell
gradlew :examples:websockets:desktop-c:websockets_desktop_c_debug_build
gradlew :examples:websockets:desktop-c:websockets_desktop_c_release_build
```

Run it through Gradle:

```shell
gradlew :examples:websockets:desktop-c:websockets_desktop_c_debug_run
gradlew :examples:websockets:desktop-c:websockets_desktop_c_release_run
gradlew :examples:websockets:desktop-c:websockets_desktop_c_release_console_run
```

The example writes generated files under `examples/websockets/desktop-c/build/dist`.

## Linux `wss` Support

Some Linux distributions ship a system `libcurl.so.4` that exposes the WebSocket API symbols but does not actually enable the `ws` and `wss` protocols. In that case the example can start, but connecting to a `wss://` endpoint fails with:

```text
Protocol "wss" not supported or disabled in libcurl
```

The `gdx-websockets:teavm-desktop-c` build helper supports packaging a caller-provided `libcurl.so.4` next to the native executable. The Linux runtime loader prefers that local copy before falling back to the system library.

### Build a `ws`/`wss`-Capable `libcurl.so.4`

The `gdx-websockets:teavm-desktop-c` module owns the helper scripts and documentation for building a `ws`/`wss`-capable `libcurl` runtime. See `gdx-websockets/teavm-desktop-c/README.md`.

### Use the Custom Runtime During Build/Run

Pass the generated library path to Gradle:

```shell
gradlew \
  -PgdxTeaVMLinuxCurlPath=/absolute/path/to/libcurl.so.4 \
  :examples:websockets:desktop-c:websockets_desktop_c_debug_run
```

The build copies that runtime into:

```text
examples/websockets/desktop-c/build/dist/c/release/libcurl.so.4
```

and the Linux native executable loads it from the executable directory.

### Running on Another Linux Machine

After building, copy the whole `examples/websockets/desktop-c/build/dist/c/release/` directory to the target Linux machine. Keep these files together:

- `websockets_debug` or `websockets_release`
- `libcurl.so.4`
- the `assets/` directory

If you want to override the runtime path manually instead of keeping the library next to the executable, set:

```shell
GDX_TEAVM_LIBCURL_PATH=/absolute/path/to/libcurl.so.4 ./websockets_release
```

## macOS `wss` Support

The TeaVM GLFW websocket backend now compiles and links on macOS, but the system `libcurl.4.dylib` on some macOS installs can still fail at runtime with:

```text
Protocol "wss" not supported
```

The `gdx-websockets:teavm-desktop-c` build helper can package a caller-provided `libcurl.4.dylib` next to the native executable. The build uses this order:

1. `-PgdxTeaVMMacCurlPath=...`
2. `GDX_TEAVM_MAC_CURL_PATH`

### Build a `ws`/`wss`-Capable `libcurl.4.dylib`

The `gdx-websockets:teavm-desktop-c` module owns the helper scripts and documentation for building a `ws`/`wss`-capable macOS `libcurl` runtime. See `gdx-websockets/teavm-desktop-c/README.md`.

### Package a Custom macOS `libcurl`

Pass a known-good `libcurl.4.dylib` to Gradle:

```shell
gradlew \
  -PgdxTeaVMMacCurlPath="$(brew --prefix curl)/lib/libcurl.4.dylib" \
  :examples:websockets:desktop-c:websockets_desktop_c_debug_run
```

The build copies that runtime into:

```text
examples/websockets/desktop-c/build/dist/c/release/libcurl.4.dylib
```

### Runtime Override

The native macOS executable also honors:

```shell
GDX_TEAVM_LIBCURL_PATH=/absolute/path/to/libcurl.4.dylib ./websockets_debug
```
