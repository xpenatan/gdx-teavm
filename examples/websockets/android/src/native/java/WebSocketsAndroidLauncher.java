import com.github.czyzby.websocket.AndroidWebSockets;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidApplication;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.websockets.WebSocketDemo;

public class WebSocketsAndroidLauncher {
    public static void main(String[] args) {
        AndroidWebSockets.initiate();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        new AndroidApplication(new WebSocketDemo(), config);
    }
}
