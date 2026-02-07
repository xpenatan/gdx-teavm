import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GLTFQuickStartExample;

public class TestWebLauncher {

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration();
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
//        new TeaApplication(new LoadingTest(), config);
        new WebApplication(new GLTFQuickStartExample(), config);
//        new TeaApplication(new ReflectionTest(), config);
//        new TeaApplication(new UITest(), config);

        // Audio is no longer supported in gdx-teavm. You can use this solution to keep using Howler.js
//        new TeaApplication(new GearsDemo(), config);
    }
}