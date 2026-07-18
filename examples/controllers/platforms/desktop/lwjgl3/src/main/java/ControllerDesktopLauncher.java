import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.controllers.ControllerDemo;

public class ControllerDesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("gdx-controllers desktop");
        config.setWindowedMode(800, 480);
        config.useVsync(true);
        new Lwjgl3Application(new ControllerDemo(), config);
    }
}
