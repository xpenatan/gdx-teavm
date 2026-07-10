import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;
import com.github.xpenatan.gdx.teavm.examples.websockets.WebSocketDemo;

public class WebSocketsDesktopLauncher {

    public static void main(String[] args) {
        CommonWebSockets.initiate();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("gdx-teavm websockets desktop");
        config.setWindowedMode(800, 480);
        config.useVsync(true);
        new Lwjgl3Application(new WebSocketDemo(), config);
    }
}
