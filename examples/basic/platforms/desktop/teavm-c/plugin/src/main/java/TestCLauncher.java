import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.UITest;

public class TestCLauncher {

    public static void main(String[] args) {
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);
        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
        new GLFWApplication(new UITest(), config);
    }
}
