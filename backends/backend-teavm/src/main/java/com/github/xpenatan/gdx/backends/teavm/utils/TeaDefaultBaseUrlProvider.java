package com.github.xpenatan.gdx.backends.teavm.utils;

import com.github.xpenatan.gdx.backends.teavm.dom.impl.TeaWindow;
import org.teavm.jso.browser.Location;

public class TeaDefaultBaseUrlProvider implements TeaBaseUrlProvider{
    @Override
    public String getBaseUrl() {
        TeaWindow currentWindow = TeaWindow.get();
        Location location = currentWindow.getLocation();
        String hostPageBaseURL = location.getFullURL();

        if(hostPageBaseURL.contains(".html")) {
            // TODO Find a solution to remove html path
            hostPageBaseURL = hostPageBaseURL.replace("index.html", "");
            hostPageBaseURL = hostPageBaseURL.replace("index-wasm.html", "");
            hostPageBaseURL = hostPageBaseURL.replace("index-debug.html", "");
        }
        int indexQM = hostPageBaseURL.indexOf('?');
        if (indexQM >= 0) {
            hostPageBaseURL = hostPageBaseURL.substring(0, indexQM);
        }
        return hostPageBaseURL;
    }
}
