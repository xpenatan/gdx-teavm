import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.controllers.ControllerDemo;

public class ControllerCLauncher {

    public static void main(String[] args) {
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("gdx-controllers desktop-c");
        config.setWindowedMode(800, 480);
        config.useVsync(true);
        config.setForegroundFPS(60);

        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
        new GLFWApplication(new ControllerDemo(), config);
    }
}
