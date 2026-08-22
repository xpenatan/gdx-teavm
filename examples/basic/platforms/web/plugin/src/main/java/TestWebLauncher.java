import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.ModelInstancedRenderingTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.WebGLBufferRangeTest;

public class TestWebLauncher {

    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration();
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        config.useGL30 = true;
//        new WebApplication(new ModelInstancedRenderingTest(), config);
        new WebApplication(new WebGLBufferRangeTest(), config);
    }
}
