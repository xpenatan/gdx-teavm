# WebWindow RAF contract

This isolated browser project has two contracts. The frame contract checks render-thread identity across suspension and serial RAF execution. The input contract starts a small `WebApplication`, sends a canvas click, then suspends in `touchUp` while another click and Tab down/up arrive. It checks queued state changes and callbacks stay ordered on the render thread while browser defaults are prevented synchronously.

Build it from the repository root with the TeaVM fork checkout:

```powershell
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true -PteavmPath=C:/gits/teavm-unciv :repro:web-window:gdx_teavm_web_js_build
```

Run the default frame contract in headless Chrome with Playwright installed:

```powershell
$env:PLAYWRIGHT_MODULE = 'path/to/node_modules/playwright'
$env:CHROME_PATH = 'path/to/chrome.exe'
node repro/web-window/run-browser.cjs
```

Build the input contract by selecting its entry class, then run the runner in `input` mode:

```powershell
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true -PteavmPath=C:/gits/teavm-unciv '-PwebWindowEntry=repro.WebInputSuspensionContract' :repro:web-window:gdx_teavm_web_js_build
node repro/web-window/run-browser.cjs input
```
