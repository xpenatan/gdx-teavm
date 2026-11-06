# WebWindow RAF contract

This isolated browser project contains focused contracts for backend callbacks and local-file storage. The frame contract checks render-thread identity across suspension and serial RAF execution. The input contract starts a small `WebApplication`, sends a canvas click, then suspends in `touchUp` while another click and Tab down/up arrive. It checks queued state changes and callbacks stay ordered on the render thread while browser defaults are prevented synchronously. The file contract writes a local file, verifies the IndexedDB record stores its timestamp, reloads the page, and checks `lastModified()` returns the persisted timestamp rather than zero. The lifecycle contract dispatches visibility and pagehide events, suspends inside listener callbacks, checks render-thread identity, and verifies frames stop after pagehide disposal.

Build it from the repository root with a local TeaVM source checkout. Set the
checkout path once in PowerShell:

```powershell
$teavmPath = Read-Host 'Path to the TeaVM source checkout'
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true "-PteavmPath=$teavmPath" :repro:web-window:gdx_teavm_web_js_build
```

Run the default frame contract in headless Chrome with Playwright installed:

```powershell
$env:PLAYWRIGHT_MODULE = 'path/to/node_modules/playwright'
$env:CHROME_PATH = 'path/to/chrome.exe'
node repro/web-window/run-browser.cjs
```

Build the input contract by selecting its entry class, then run the runner in `input` mode:

```powershell
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true "-PteavmPath=$teavmPath" '-PwebWindowEntry=repro.WebInputSuspensionContract' :repro:web-window:gdx_teavm_web_js_build
node repro/web-window/run-browser.cjs input
```

Build the persistent local-file timestamp contract by selecting its entry class,
then run the runner in `file` mode. The runner writes in one app instance, waits
for the actual IndexedDB transaction data, reloads the same browser context, and
compares the restored timestamp with the write-time value:

```powershell
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true "-PteavmPath=$teavmPath" '-PwebWindowEntry=repro.WebFileLastModifiedContract' :repro:web-window:gdx_teavm_web_js_build
node repro/web-window/run-browser.cjs file
```

Build and run the visibility/pagehide callback contract:

```powershell
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true "-PteavmPath=$teavmPath" '-PwebWindowEntry=repro.WebApplicationLifecycleContract' :repro:web-window:gdx_teavm_web_js_build
node repro/web-window/run-browser.cjs lifecycle
```

Pagehide disposal is queued to the render executor so Java callbacks can suspend
and remain serialized. Browsers do not wait for asynchronous work before leaving
a page; this contract proves queued callback order in a live page, not guaranteed
completion after an actual navigation away.
