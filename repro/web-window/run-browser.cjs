const fs = require("node:fs");
const http = require("node:http");
const path = require("node:path");
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || "playwright");

const webRoot = path.resolve(__dirname, "build/dist/js/webapp");
const mimeTypes = {
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".png": "image/png",
};

async function main() {
  const mode = process.argv[2] || "frame";
  const inputContract = mode === "input";
  const fileContract = mode === "file";
  const lifecycleContract = mode === "lifecycle";
  const server = http.createServer((request, response) => {
    const pathname = decodeURIComponent(new URL(request.url, "http://localhost").pathname);
    const relativePath = pathname === "/" ? "index.html" : pathname.slice(1);
    const file = path.resolve(webRoot, relativePath);
    if (!file.startsWith(webRoot + path.sep) && file !== path.join(webRoot, "index.html")) {
      response.writeHead(403).end();
      return;
    }
    fs.readFile(file, (error, contents) => {
      if (error) {
        response.writeHead(404).end();
        return;
      }
      response.writeHead(200, { "Content-Type": mimeTypes[path.extname(file)] || "application/octet-stream" });
      response.end(contents);
    });
  });

  await new Promise((resolve) => server.listen(18101, "127.0.0.1", resolve));
  const launchOptions = {
    headless: true,
    args: ["--no-sandbox", "--use-gl=angle", "--use-angle=swiftshader"],
  };
  if (process.env.CHROME_PATH) launchOptions.executablePath = process.env.CHROME_PATH;
  const browser = await chromium.launch(launchOptions);
  try {
    const page = await browser.newPage();
    page.on("pageerror", (error) => console.error("pageerror:", error));
    page.on("console", (message) => {
      if (message.type() === "error") console.error("console:", message.text());
    });
    if(fileContract) {
      await page.goto("http://127.0.0.1:18101/?phase=write", { waitUntil: "load" });
      await page.waitForFunction(() => {
        const value = document.body.getAttribute("data-result");
        return value?.startsWith("WRITE:") || value?.startsWith("FAIL:");
      }, null, { timeout: 60000 });
      const writeResult = await page.locator("body").getAttribute("data-result");
      if (!writeResult.startsWith("WRITE:")) throw new Error(`Local file write phase failed: ${writeResult}`);
      const expectedTimestamp = Number(writeResult.slice("WRITE:".length));
      if (!Number.isSafeInteger(expectedTimestamp) || expectedTimestamp <= 0) {
        throw new Error(`Invalid local-file timestamp: ${writeResult}`);
      }

      await page.waitForFunction(async (expected) => new Promise((resolve) => {
        const open = indexedDB.open("gdx-teavm-last-modified-contract-v1");
        open.onerror = () => resolve(false);
        open.onsuccess = () => {
          const db = open.result;
          if (!db.objectStoreNames.contains("FILE_DATA")) {
            db.close();
            resolve(false);
            return;
          }
          const transaction = db.transaction("FILE_DATA", "readonly");
          const request = transaction.objectStore("FILE_DATA").get("last-modified-contract.txt");
          request.onsuccess = () => {
            const record = request.result;
            const matches = !!record && record.date instanceof Date && record.date.getTime() === expected;
            db.close();
            resolve(matches);
          };
          request.onerror = () => {
            db.close();
            resolve(false);
          };
        };
      }), expectedTimestamp, { timeout: 10000 });

      await page.goto(`http://127.0.0.1:18101/?phase=read&expected=${expectedTimestamp}`, { waitUntil: "load" });
      await page.waitForFunction(() => {
        const value = document.body.getAttribute("data-result");
        return value === "PASS" || value?.startsWith("FAIL:");
      }, null, { timeout: 60000 });
    } else {
      await page.goto("http://127.0.0.1:18101/", { waitUntil: "load" });
    }

    if(inputContract) {
      await page.waitForFunction(() => document.body.getAttribute("data-result") === "READY", null, { timeout: 60000 });
      await page.locator("#canvas").click({ position: { x: 20, y: 20 } });
      await page.waitForFunction(() => {
        const value = document.body.getAttribute("data-result");
        return value === "PASS" || value?.startsWith("FAIL:");
      }, null, { timeout: 15000 });
    } else if(lifecycleContract) {
      await page.waitForFunction(() => document.body.getAttribute("data-result") === "READY", null, { timeout: 60000 });
      await page.evaluate(() => {
        window.__gdxTestVisibility = "hidden";
        Object.defineProperty(document, "visibilityState", {
          configurable: true,
          get: () => window.__gdxTestVisibility,
        });
        document.dispatchEvent(new Event("visibilitychange"));
      });
      await page.waitForFunction(() => {
        const value = document.body.getAttribute("data-result");
        return value === "HIDDEN" || value?.startsWith("FAIL:");
      }, null, { timeout: 15000 });
      if (await page.locator("body").getAttribute("data-result") !== "HIDDEN") {
        throw new Error(`Hidden lifecycle callback failed: ${await page.locator("body").getAttribute("data-result")}`);
      }

      await page.evaluate(() => {
        window.__gdxTestVisibility = "visible";
        document.dispatchEvent(new Event("visibilitychange"));
      });
      await page.waitForFunction(() => {
        const value = document.body.getAttribute("data-result");
        return value === "VISIBLE" || value?.startsWith("FAIL:");
      }, null, { timeout: 15000 });
      if (await page.locator("body").getAttribute("data-result") !== "VISIBLE") {
        throw new Error(`Visible lifecycle callback failed: ${await page.locator("body").getAttribute("data-result")}`);
      }

      await page.evaluate(() => window.dispatchEvent(new Event("pagehide")));
      await page.waitForFunction(() => {
        const value = document.body.getAttribute("data-result");
        return value === "PASS" || value?.startsWith("FAIL:");
      }, null, { timeout: 15000 });
    } else {
      await page.waitForFunction(() => document.body.getAttribute("data-result") === "PASS" ||
        document.body.getAttribute("data-result")?.startsWith("FAIL:"), null, { timeout: 15000 });
    }
    const result = await page.locator("body").getAttribute("data-result");
    if (result !== "PASS") throw new Error(`WebWindow contract failed: ${result}`);
    const contractName = inputContract ? "web-input-suspension" :
      fileContract ? "web-file-last-modified" :
      lifecycleContract ? "web-application-lifecycle" : "web-window-frame";
    console.log(`${contractName}-contract=PASS`);
  } finally {
    await browser.close();
    await new Promise((resolve, reject) => server.close((error) => error ? reject(error) : resolve()));
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
