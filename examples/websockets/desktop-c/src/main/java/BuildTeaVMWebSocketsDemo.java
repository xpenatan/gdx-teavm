import com.github.czyzby.websocket.GLFWWebSocketsBuild;
import java.io.IOException;

public class BuildTeaVMWebSocketsDemo {

    public static void main(String[] args) throws IOException {
        GLFWWebSocketsBuild.build("websockets", WebSocketsCLauncher.class, args);
    }
}
