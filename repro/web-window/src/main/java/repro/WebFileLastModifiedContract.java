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
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import org.teavm.jso.JSBody;

/** Browser contract for local-file last-modified metadata across an IndexedDB reload. */
public final class WebFileLastModifiedContract extends ApplicationAdapter {
    private static final String FILE_PATH = "last-modified-contract.txt";
    private static final String DATABASE_NAME = "gdx-teavm-last-modified-contract-v1";

    private WebFileLastModifiedContract() {
    }

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 800;
        config.height = 600;
        config.showDownloadLogs = false;
        config.localStoragePrefix = DATABASE_NAME;
        new WebApplication(new WebFileLastModifiedContract(), config);
    }

    @Override
    public void create() {
        try {
            String phase = getQueryParameter("phase");
            FileHandle file = Gdx.files.local(FILE_PATH);
            require(Gdx.files.local("missing-last-modified-contract.txt").lastModified() == 0,
                    "missing local file must report zero");
            require(Gdx.files.internal("gdx.wasm.js").lastModified() == 0,
                    "internal asset timestamp should remain zero");

            if("write".equals(phase)) {
                file.writeString("metadata must survive an IndexedDB reload", false);
                long lastModified = file.lastModified();
                require(lastModified > 0, "new local file did not report its write time");
                result("WRITE:" + lastModified);
                return;
            }

            require("read".equals(phase), "query parameter must select write or read phase");
            require(file.exists(), "local file did not survive the page reload");
            long actual = file.lastModified();
            long expected = Long.parseLong(getQueryParameter("expected"));
            require(actual > 0, "restored local file did not report its persisted write time");
            require(actual == expected, "restored timestamp changed: expected " + expected + ", got " + actual);
            result("PASS");
        } catch(Throwable e) {
            result("FAIL: " + e.getMessage());
            throw new AssertionError(e);
        }
    }

    private static void require(boolean condition, String message) {
        if(!condition) throw new AssertionError(message);
    }

    @JSBody(params = "name", script = "return new URLSearchParams(window.location.search).get(name);")
    private static native String getQueryParameter(String name);

    @JSBody(params = "value", script = "document.body.setAttribute('data-result', value);")
    private static native void result(String value);
}
