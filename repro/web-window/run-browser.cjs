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
  const inputContract = process.argv[2] === "input";
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
  const browser = await chromium.launch({
    executablePath: process.env.CHROME_PATH || "C:/Program Files/Google/Chrome/Application/chrome.exe",
    headless: true,
    args: ["--no-sandbox", "--use-gl=angle", "--use-angle=swiftshader"],
  });
  try {
    const page = await browser.newPage();
    page.on("pageerror", (error) => console.error("pageerror:", error));
    page.on("console", (message) => {
      if (message.type() === "error") console.error("console:", message.text());
    });
    await page.goto("http://127.0.0.1:18101/", { waitUntil: "load" });
    if(inputContract) {
      await page.waitForFunction(() => document.body.getAttribute("data-result") === "READY", null, { timeout: 60000 });
      await page.locator("#canvas").click({ position: { x: 20, y: 20 } });
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
    console.log(`${inputContract ? "web-input-suspension" : "web-window-frame"}-contract=PASS`);
  } finally {
    await browser.close();
    await new Promise((resolve, reject) => server.close((error) => error ? reject(error) : resolve()));
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
