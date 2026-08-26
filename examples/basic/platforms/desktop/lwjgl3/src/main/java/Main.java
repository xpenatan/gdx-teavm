import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;

public class Main {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);
        new Lwjgl3Application(new SpriteBatchTest(), config);
    }
}
