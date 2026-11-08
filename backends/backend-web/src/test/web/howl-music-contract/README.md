# HowlMusic browser contract

This fixture compiles the production `HowlMusic` adapter through TeaVM and runs
it in Chromium with Howler. It checks pending playback while the audio context
is suspended, actual playback and position advance after a trusted click,
pause/stop/dispose before unlock, a malformed-file load error, recovery from
Howler's recognized autoplay `playerror`, and a terminal `playerror` that must
not resume after unlock.

From the repository root, build the named fixture target:

```powershell
./gradlew :examples:basic:platforms:web:plugin:gdx_teavm_web_js_music_contract_build
```

The target output is:

```text
examples/basic/platforms/web/plugin/build/dist/js/music_contract/webapp
```

Install Playwright in a local build directory or point `PLAYWRIGHT_MODULE` to
an existing Playwright package. Then run the browser contract:

```powershell
$env:PLAYWRIGHT_MODULE = "playwright"
$env:HOWL_MUSIC_WEBAPP = "examples/basic/platforms/web/plugin/build/dist/js/music_contract/webapp"
node backends/backend-web/src/test/web/howl-music-contract/run.cjs
```

If Playwright cannot locate Chromium, set `PLAYWRIGHT_CHROMIUM_EXECUTABLE` to a
local Chromium or Chrome executable. The runner serves the generated site on a
temporary localhost port and closes the server and browser when the checks end.
