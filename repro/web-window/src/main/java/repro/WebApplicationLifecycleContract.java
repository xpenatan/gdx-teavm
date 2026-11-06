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
import com.badlogic.gdx.LifecycleListener;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import java.util.concurrent.locks.LockSupport;
import org.teavm.jso.JSBody;

/** Browser contract for serial, suspendable visibility and pagehide callbacks. */
public final class WebApplicationLifecycleContract extends ApplicationAdapter {
    private static volatile Thread renderThread;
    private static volatile int renderedFrames;
    private static int applicationPauseCount;

    private WebApplicationLifecycleContract() {
    }

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 800;
        config.height = 600;
        config.showDownloadLogs = false;
        new WebApplication(new WebApplicationLifecycleContract(), config);
    }

    @Override
    public void create() {
        renderThread = Thread.currentThread();
        Gdx.app.addLifecycleListener(new LifecycleListener() {
            @Override
            public void pause() {
                requireRenderThread("LifecycleListener.pause");
                suspendBriefly("LifecycleListener.pause");
            }

            @Override
            public void resume() {
                requireRenderThread("LifecycleListener.resume");
            }

            @Override
            public void dispose() {
                requireRenderThread("LifecycleListener.dispose");
            }
        });
        result("READY");
    }

    @Override
    public void render() {
        renderedFrames++;
    }

    @Override
    public void pause() {
        requireRenderThread("ApplicationListener.pause");
        applicationPauseCount++;
        suspendBriefly("ApplicationListener.pause");
        if(applicationPauseCount == 1) {
            result("HIDDEN");
        }
    }

    @Override
    public void resume() {
        requireRenderThread("ApplicationListener.resume");
        result("VISIBLE");
    }

    @Override
    public void dispose() {
        requireRenderThread("ApplicationListener.dispose");
        suspendBriefly("ApplicationListener.dispose");
        int frameCountAtDispose = renderedFrames;
        Thread check = new Thread(() -> {
            LockSupport.parkNanos(100_000_000L);
            try {
                require(renderedFrames == frameCountAtDispose, "render continued after pagehide disposal");
                result("PASS");
            } catch(Throwable e) {
                fail(e);
            }
        }, "lifecycle-contract-checker");
        check.setDaemon(true);
        check.start();
    }

    private static void suspendBriefly(String operation) {
        Thread waker = new Thread(() -> {
            LockSupport.parkNanos(10_000_000L);
            LockSupport.unpark(renderThread);
        }, operation + "-waker");
        waker.setDaemon(true);
        waker.start();
        LockSupport.park();
        requireRenderThread(operation + " resume");
    }

    private static void requireRenderThread(String operation) {
        require(Thread.currentThread() == renderThread, operation + " ran outside the persistent render thread");
    }

    private static void require(boolean condition, String message) {
        if(!condition) {
            result("FAIL: " + message);
            throw new AssertionError(message);
        }
    }

    private static void fail(Throwable error) {
        result("FAIL: " + error);
        throw new AssertionError(error);
    }

    @JSBody(params = "state", script = "" +
            "window.__gdxTestVisibility = state;\n" +
            "if(!window.__gdxVisibilityInstalled) {\n" +
            "  Object.defineProperty(document, 'visibilityState', {configurable:true, get:function() { return window.__gdxTestVisibility; }});\n" +
            "  window.__gdxVisibilityInstalled = true;\n" +
            "}\n" +
            "document.dispatchEvent(new Event('visibilitychange'));")
    private static native void dispatchVisibilityChange(String state);

    @JSBody(script = "window.dispatchEvent(new Event('pagehide'));")
    private static native void dispatchPageHide();

    @JSBody(params = "value", script = "document.body.setAttribute('data-result', value);")
    private static native void result(String value);
}
