import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;

public class Main {

    private static final String LWJGL_JNI_FUNCTION_COUNT = "org.lwjgl.system.JNINativeInterfaceSize";
    private static final String JAVA_24_PLUS_JNI_FUNCTION_COUNT = "233";

    public static void main(String[] args) {
        if (System.getProperty(LWJGL_JNI_FUNCTION_COUNT) == null) {
            System.setProperty(LWJGL_JNI_FUNCTION_COUNT, JAVA_24_PLUS_JNI_FUNCTION_COUNT);
        }

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);

        ApplicationListener listener = new SpriteBatchTest();
        float exitAfterSeconds = getExitAfterSeconds(args);
        if (exitAfterSeconds > 0) {
            listener = new AutoExitListener(listener, exitAfterSeconds);
        }

        new Lwjgl3Application(listener, config);
    }

    private static float getExitAfterSeconds(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--exit-after-seconds=")) {
                return Float.parseFloat(arg.substring("--exit-after-seconds=".length()));
            }
        }
        return 0;
    }

    private static class AutoExitListener implements ApplicationListener {
        private final ApplicationListener listener;
        private final float exitAfterSeconds;
        private float elapsedSeconds;

        private AutoExitListener(ApplicationListener listener, float exitAfterSeconds) {
            this.listener = listener;
            this.exitAfterSeconds = exitAfterSeconds;
        }

        @Override
        public void create() {
            listener.create();
        }

        @Override
        public void resize(int width, int height) {
            listener.resize(width, height);
        }

        @Override
        public void render() {
            listener.render();
            elapsedSeconds += Gdx.graphics.getDeltaTime();
            if (elapsedSeconds >= exitAfterSeconds) {
                // This flag is only for automated benchmark/training runs; skip normal app cleanup on native-image.
                Runtime.getRuntime().halt(0);
            }
        }

        @Override
        public void pause() {
            listener.pause();
        }

        @Override
        public void resume() {
            listener.resume();
        }

        @Override
        public void dispose() {
            listener.dispose();
        }
    }
}
