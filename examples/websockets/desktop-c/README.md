# WebSockets TeaVM C Desktop Example

This example runs the shared `examples:websockets:core` demo through the TeaVM C GLFW backend.

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

The desktop-c launcher now supports packaging a caller-provided `libcurl.so.4` next to the native executable. The Linux runtime loader prefers that local copy before falling back to the system library.

### Build a `ws`/`wss`-Capable `libcurl.so.4`

On a Linux machine with a C toolchain, OpenSSL development headers, and zlib development headers installed, run:

```shell
examples/websockets/desktop-c/build-linux-libcurl-wss.sh
```

That script downloads curl, configures it with:

```text
--with-openssl --enable-websockets --disable-static
```

and installs the runtime under:

```text
examples/websockets/desktop-c/build/libcurl-wss/install/lib/libcurl.so.4
```

### Use the Custom Runtime During Build/Run

Pass the generated library path to Gradle:

```shell
gradlew \
  -PgdxTeaVMLinuxCurlPath=$PWD/examples/websockets/desktop-c/build/libcurl-wss/install/lib/libcurl.so.4 \
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
