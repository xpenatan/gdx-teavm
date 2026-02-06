import com.github.xpenatan.gdx.teavm.backends.web.TeaWebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GLTFQuickStartExample;

public class TestWebLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration();
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
//        new TeaApplication(new LoadingTest(), config);
        new TeaWebApplication(new GLTFQuickStartExample(), config);
//        new TeaApplication(new ReflectionTest(), config);
//        new TeaApplication(new UITest(), config);

        // Audio is no longer supported in gdx-teavm. You can use this solution to keep using Howler.js
//        new TeaApplication(new GearsDemo(), config);
    }
}