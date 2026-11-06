/*
 *  Copyright 2026 TeaVM contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package repro;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import java.util.concurrent.locks.LockSupport;
import org.teavm.jso.JSBody;

/** Browser integration contract for DOM input queued behind a suspended render callback. */
public final class WebInputSuspensionContract extends ApplicationAdapter {
    private static volatile Thread renderThread;
    private static volatile boolean firstUpResumed;
    private static volatile boolean keyDownProcessed;
    private static volatile boolean keyUpProcessed;
    private static volatile boolean secondClickProcessed;
    private static int touchDownCount;
    private static int touchUpCount;

    private WebInputSuspensionContract() {
    }

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 800;
        config.height = 600;
        config.showDownloadLogs = false;
        new WebApplication(new WebInputSuspensionContract(), config);
    }

    @Override
    public void create() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touchDownCount++;
                if(touchDownCount == 1) {
                    require(Gdx.input.isTouched(), "touch state missing in touchDown callback");
                } else {
                    require(firstUpResumed, "queued touchDown ran before suspended touchUp resumed");
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                touchUpCount++;
                if(touchUpCount == 1) {
                    renderThread = Thread.currentThread();
                    require(!Gdx.input.isTouched(), "touch state missing release before touchUp");
                    scheduleSecondClickAndTab();
                    Thread wake = new Thread(() -> {
                        LockSupport.parkNanos(250_000_000L);
                        LockSupport.unpark(renderThread);
                    }, "input-contract-waker");
                    wake.setDaemon(true);
                    wake.start();

                    LockSupport.park();
                    require(Thread.currentThread() == renderThread, "render thread changed after input suspension");
                    require(!Gdx.input.isTouched(), "later DOM mouse state mutated before queued input dispatch");
                    require(!Gdx.input.isKeyPressed(Keys.TAB), "queued key state mutated before suspended frame resumed");
                    require(!Gdx.input.isKeyJustPressed(Keys.TAB), "queued key edge mutated before suspended frame resumed");
                    require(readSecondClickPrevented(), "mousedown default was not prevented synchronously");
                    require(readTabDownDefaultPrevented(), "Tab keydown default was not prevented synchronously");
                    require(readTabUpDefaultPrevented(), "Tab keyup default was not prevented synchronously");
                    firstUpResumed = true;
                } else {
                    require(firstUpResumed, "second touchUp ran before first touchUp completed");
                    require(Thread.currentThread() == renderThread, "input callbacks changed Java thread");
                    secondClickProcessed = true;
                    finishIfComplete();
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if(keycode == Keys.TAB) {
                    require(firstUpResumed, "queued keyDown ran before suspended touchUp resumed");
                    require(Gdx.input.isKeyPressed(Keys.TAB), "Tab key state missing in keyDown callback");
                    require(Gdx.input.isKeyJustPressed(Keys.TAB), "Tab just-pressed state missing in keyDown callback");
                    keyDownProcessed = true;
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if(keycode == Keys.TAB) {
                    require(keyDownProcessed, "queued keyUp ran before keyDown");
                    require(!Gdx.input.isKeyPressed(Keys.TAB), "Tab key state remained pressed in keyUp callback");
                    keyUpProcessed = true;
                    finishIfComplete();
                }
                return false;
            }
        });
        result("READY");
    }

    private static void require(boolean condition, String message) {
        if(!condition) {
            result("FAIL: " + message);
            throw new AssertionError(message);
        }
    }

    private static void finishIfComplete() {
        if(secondClickProcessed && keyDownProcessed && keyUpProcessed) {
            result("PASS");
        }
    }

    @JSBody(script = "" +
            "window.__secondClickPrevented = false;\n" +
            "window.__tabDownPrevented = false;\n" +
            "window.__tabUpPrevented = false;\n" +
            "window.setTimeout(function() {\n" +
            "  var canvas = document.getElementById('canvas');\n" +
            "  var rect = canvas.getBoundingClientRect();\n" +
            "  var init = {bubbles:true, cancelable:true, button:0, clientX:rect.left + 20, clientY:rect.top + 20};\n" +
            "  var down = new MouseEvent('mousedown', init);\n" +
            "  canvas.dispatchEvent(down);\n" +
            "  window.__secondClickPrevented = down.defaultPrevented;\n" +
            "  canvas.dispatchEvent(new MouseEvent('mouseup', init));\n" +
            "  var tab = new KeyboardEvent('keydown', {bubbles:true, cancelable:true, key:'Tab', code:'Tab', keyCode:9});\n" +
            "  document.dispatchEvent(tab);\n" +
            "  window.__tabDownPrevented = tab.defaultPrevented;\n" +
            "  var tabUp = new KeyboardEvent('keyup', {bubbles:true, cancelable:true, key:'Tab', code:'Tab', keyCode:9});\n" +
            "  document.dispatchEvent(tabUp);\n" +
            "  window.__tabUpPrevented = tabUp.defaultPrevented;\n" +
            "}, 25);")
    private static native void scheduleSecondClickAndTab();

    @JSBody(script = "return window.__secondClickPrevented === true;")
    private static native boolean readSecondClickPrevented();

    @JSBody(script = "return window.__tabDownPrevented === true;")
    private static native boolean readTabDownDefaultPrevented();

    @JSBody(script = "return window.__tabUpPrevented === true;")
    private static native boolean readTabUpDefaultPrevented();

    @JSBody(params = "value", script = "document.body.setAttribute('data-result', value);")
    private static native void result(String value);
}
