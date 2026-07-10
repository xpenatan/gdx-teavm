import com.github.czyzby.websocket.TeaWebSockets;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.websockets.WebSocketDemo;

public class WebSocketsWebLauncher {

    public static void main(String[] args) {
        TeaWebSockets.initiate();

        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;

        new WebApplication(new WebSocketDemo(), config);
    }
}
