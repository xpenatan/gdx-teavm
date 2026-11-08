const fs = require('node:fs');
const http = require('node:http');
const path = require('node:path');

const webappDir = path.resolve(process.env.HOWL_MUSIC_WEBAPP || path.join(
  process.cwd(),
  'examples/basic/platforms/web/plugin/build/dist/js/music_contract/webapp'
));
const playwrightModule = process.env.PLAYWRIGHT_MODULE || 'playwright';
const { chromium } = require(playwrightModule);

function contentType(file) {
  switch (path.extname(file).toLowerCase()) {
    case '.html': return 'text/html; charset=utf-8';
    case '.js': return 'text/javascript; charset=utf-8';
    case '.css': return 'text/css; charset=utf-8';
    case '.json': return 'application/json; charset=utf-8';
    case '.png': return 'image/png';
    case '.jpg': case '.jpeg': return 'image/jpeg';
    case '.svg': return 'image/svg+xml';
    case '.woff': return 'font/woff';
    case '.woff2': return 'font/woff2';
    default: return 'application/octet-stream';
  }
}

function startServer() {
  const root = path.resolve(webappDir);
  if (!fs.existsSync(path.join(root, 'index.html')) || !fs.existsSync(path.join(root, 'app.js')) ||
      !fs.existsSync(path.join(root, 'scripts/howler.js'))) {
    throw new Error(`Not a generated music-contract webapp directory: ${root}`);
  }

  const server = http.createServer((request, response) => {
    let pathname;
    try {
      pathname = decodeURIComponent(new URL(request.url, 'http://localhost').pathname);
    } catch (_) {
      response.writeHead(400).end();
      return;
    }
    const relative = pathname === '/' ? 'index.html' : pathname.replace(/^\/+/, '');
    const file = path.resolve(root, relative);
    if (file !== root && !file.startsWith(root + path.sep)) {
      response.writeHead(403).end();
      return;
    }
    if (!fs.existsSync(file) || !fs.statSync(file).isFile()) {
      response.writeHead(404).end();
      return;
    }
    response.writeHead(200, { 'Content-Type': contentType(file) });
    fs.createReadStream(file).pipe(response);
  });

  return new Promise((resolve, reject) => {
    server.once('error', reject);
    server.listen(0, '127.0.0.1', () => {
      const address = server.address();
      resolve({ server, url: `http://127.0.0.1:${address.port}/` });
    });
  });
}

async function main() {
  const { server, url } = await startServer();
  let browser;
  try {
    const launchOptions = {
      headless: true,
      args: ['--no-sandbox'],
    };
    if (process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE) {
      launchOptions.executablePath = process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE;
    }
    browser = await chromium.launch(launchOptions);
    const context = await browser.newContext({ viewport: { width: 640, height: 360 } });
    const page = await context.newPage();
    const pageErrors = [];
    page.on('pageerror', error => pageErrors.push(error.stack || error.message));
    await page.addInitScript(() => {
      window.__allowAudioResume = false;
      document.addEventListener('click', () => { window.__allowAudioResume = true; }, true);
      for (const name of ['AudioContext', 'webkitAudioContext']) {
        const Native = window[name];
        if (!Native || Native.__gated) continue;
        const nativeResume = Native.prototype.resume;
        const Gated = function (...args) {
          const context = new Native(...args);
          context.suspend();
          return context;
        };
        Gated.__gated = true;
        Gated.prototype = Native.prototype;
        window[name] = Gated;
        Native.prototype.resume = function (...args) {
          if (!window.__allowAudioResume) return new Promise(() => {});
          return nativeResume.apply(this, args);
        };
      }
    });

    await page.goto(url, { waitUntil: 'load', timeout: 30000 });
    await page.waitForFunction(() => window.__howlMusicContract?.phase === 'afterLoadError', null, { timeout: 15000 });
    const before = await page.evaluate(() => ({
      ...window.__howlMusicContract,
      context: window.Howler?.ctx?.state,
      activeHowlPlaying: window.Howler?._howls[0]?.playing(),
      retryHowlPlaying: window.Howler?._howls[4]?.playing(),
      terminalHowlPlaying: window.Howler?._howls[5]?.playing(),
    }));

    await page.evaluate(() => {
      const howls = window.Howler._howls;
      const retry = howls[4];
      retry._emit('playerror', retry._sounds[0]._id,
        'Playback was unable to start. This is most commonly an issue on mobile devices and Chrome where playback was not within a user interaction.');

      const terminal = howls[5];
      const terminalId = terminal._sounds[0]._id;
      terminal.off('resume');
      const terminalSound = terminal._soundById(terminalId);
      terminalSound._paused = true;
      terminalSound._ended = true;
      terminal._emit('playerror', terminalId, 'Terminal playback failure');
    });

    await page.mouse.click(320, 180);
    await page.waitForFunction(() => window.__howlMusicContract?.phase === 'afterUnlock', null, { timeout: 15000 });
    const after = await page.evaluate(() => ({
      ...window.__howlMusicContract,
      context: window.Howler?.ctx?.state,
      activeHowlPlaying: window.Howler?._howls[0]?.playing(),
      retryHowlPlaying: window.Howler?._howls[4]?.playing(),
      terminalHowlPlaying: window.Howler?._howls[5]?.playing(),
    }));

    const result = { before, after, pageErrors };
    console.log(JSON.stringify(result, null, 2));
    if (before.context !== 'suspended' || !before.active || before.activeHowlPlaying ||
        before.paused || before.stopped || before.disposed || before.invalid || !before.retry ||
        before.retryHowlPlaying || before.terminalHowlPlaying) {
      throw new Error('Pre-unlock pending/cancellation/error contract failed');
    }
    if (after.context !== 'running' || !after.active || !after.activeHowlPlaying ||
        after.paused || after.stopped || after.disposed || after.invalid || !after.retry ||
        !after.retryHowlPlaying || after.terminal || after.terminalHowlPlaying ||
        after.position <= 0.1 || after.retryPosition <= 0.1 || pageErrors.length) {
      throw new Error('Post-unlock playback/retry/cancellation contract failed');
    }
  } finally {
    await browser?.close();
    server.close();
  }
}

main().catch(error => {
  console.error(error.stack || error);
  process.exitCode = 1;
});
