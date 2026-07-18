import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.imgui.example.tests.wrapper.TeaVMTestWrapper;

public class GdxTestLauncher {

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = false;
        config.useGL30 = true;

        config.preloadListener = assetLoader -> {
            assetLoader.loadScript("freetype.js");
        };

        new WebApplication(new TeaVMTestWrapper(), config);
    }
}
