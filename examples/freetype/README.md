# FreeType example

The portable FreeType demo lives in `core`. Shared font assets live in `assets` and are consumed directly by each runnable platform leaf.

| Platform | Gradle project | Representative task |
| --- | --- | --- |
| Desktop LWJGL3 | `:examples:freetype:platforms:desktop:lwjgl3` | `freetype_desktop_run` |
| Desktop TeaVM C builder | `:examples:freetype:platforms:desktop:teavm-c:builder` | `freetype_desktop_c_debug_build` |
| Web builder | `:examples:freetype:platforms:web:builder` | `freetype_web_run` |
| Web plugin | `:examples:freetype:platforms:web:plugin` | `gdx_teavm_web_js_run` |
| Android | `:examples:freetype:platforms:android` | `assembleDebug` |
| iOS | `:examples:freetype:platforms:ios` | `gdx_teavm_ios_build_simulator` |
