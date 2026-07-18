# gdx-tests example

This example integrates the libGDX test suite and is included only when `includeLibgdxSource=true` is configured with a valid `gdxSourcePath`.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL2 | `:examples:gdx-tests:platforms:desktop:lwjgl2` | `gdx_tests_run_desktop` |
| Web builder | `:examples:gdx-tests:platforms:web:builder` | `gdx_tests_build_web` |

The desktop launcher intentionally uses LWJGL2 because the test wrapper depends on its input/current-context behavior.
