import com.github.xpenatan.gdx.teavm.backends.web.TeaWebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.TeaApplicationConfiguration;
import com.github.xpenatan.imgui.example.tests.wrapper.TeaVMTestWrapper;

public class GdxTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = false;
        config.useGL30 = true;

        config.preloadListener = assetLoader -> {
            assetLoader.loadScript("freetype.js");
        };

        new TeaWebApplication(new TeaVMTestWrapper(), config);
    }
}
