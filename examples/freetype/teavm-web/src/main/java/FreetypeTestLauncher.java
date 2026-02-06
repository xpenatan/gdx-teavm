import com.github.xpenatan.gdx.teavm.backends.web.TeaWebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.freetype.FreetypeDemo;

public class FreetypeTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;

        config.preloadListener = assetLoader -> {
            assetLoader.loadScript("freetype.js");
        };

        new TeaWebApplication(new FreetypeDemo(), config);
    }
}