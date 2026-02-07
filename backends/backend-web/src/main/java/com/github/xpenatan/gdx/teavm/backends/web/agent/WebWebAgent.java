package com.github.xpenatan.gdx.teavm.backends.web.agent;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

/**
 * @author xpenatan
 */
public class WebWebAgent {

    @JSBody(script =
            "var userAgent = navigator.userAgent.toLowerCase();"
                    + "return {"
                    + "firefox : userAgent.indexOf('firefox') != -1,"
                    + "chrome : userAgent.indexOf('chrome') != -1,"
                    + "safari : userAgent.indexOf('safari') != -1,"
                    + "opera : userAgent.indexOf('opera') != -1,"
                    + "IE : userAgent.indexOf('msie') != -1,"
                    + "macOS : userAgent.indexOf('mac') != -1,"
                    + "linux : userAgent.indexOf('linux') != -1,"
                    + "windows : userAgent.indexOf('win') != -1,"
                    + "userAgent : userAgent"
                    + "};")
    private static native JSObject createAgent();

    public static WebAgentInfo computeAgentInfo() {
        JSObject jsObj = WebWebAgent.createAgent();
        WebAgentInfo agent = (WebAgentInfo)jsObj;
        return agent;
    }
}
