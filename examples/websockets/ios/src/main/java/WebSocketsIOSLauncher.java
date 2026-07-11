import com.github.czyzby.websocket.IOSWebSockets;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplication;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSFiles;
import com.github.xpenatan.gdx.teavm.examples.websockets.WebSocketDemo;

public class WebSocketsIOSLauncher {
    public static void main(String[] args) {
        if(args.length > 0) {
            IOSFiles.setLocalPath(args[0]);
        }

        IOSWebSockets.initiate();

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        new IOSApplication(new WebSocketDemo(), config);
    }
}
