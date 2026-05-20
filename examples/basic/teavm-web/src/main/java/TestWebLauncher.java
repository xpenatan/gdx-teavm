import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GLTFQuickStartExample;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.UITest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.VisUITest;

public class TestWebLauncher {

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration();
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
//        new WebApplication(new LoadingTest(), config);
//        new WebApplication(new GLTFQuickStartExample(), config);
//        new WebApplication(new ReflectionTest(), config);
        new WebApplication(new UITest(), config);

        // Audio is no longer supported in gdx-teavm. You can use this solution to keep using Howler.js
//        new WebApplication(new GearsDemo(), config);
    }
}