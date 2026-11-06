# WebWindow RAF contract

This isolated browser contract checks that a suspended RAF task keeps its Java thread identity and that a later RAF callback waits in the single render queue until the suspended task completes.

Build it from the repository root with the TeaVM fork checkout:

```powershell
gradle --no-daemon -PwebWindowRepro=true -PincludeTeaVMSource=true -PteavmPath=C:/gits/teavm-unciv :repro:web-window:gdx_teavm_web_js_build
```

Run the generated app in headless Chrome with Playwright installed:

```powershell
$env:PLAYWRIGHT_MODULE = 'path/to/node_modules/playwright'
$env:CHROME_PATH = 'path/to/chrome.exe'
node repro/web-window/run-browser.cjs
```
