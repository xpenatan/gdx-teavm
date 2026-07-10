import com.github.czyzby.websocket.GLFWWebSockets;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.websockets.WebSocketDemo;

public class WebSocketsCLauncher {

    public static void main(String[] args) {
        GLFWWebSockets.initiate();

        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("gdx-teavm websockets desktop-c");
        config.setWindowedMode(800, 480);
        config.useVsync(true);
        config.setForegroundFPS(60);

        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
        new GLFWApplication(new WebSocketDemo(), config);
    }
}
