import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.freetype.FreetypeDemo;

public class FreetypeCLauncher {

    public static void main(String[] args) {
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.setTitle("gdx-freetype desktop-c");
        config.useVsync(false);
        config.setForegroundFPS(0);

        new GLFWApplication(new FreetypeDemo(), config);
    }
}
