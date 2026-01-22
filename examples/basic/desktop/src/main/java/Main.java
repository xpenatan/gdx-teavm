import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.Basic3DTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest2;

public class Main {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);
//        new LwjglApplication(new GearsDemo());
//        new LwjglApplication(new Basic3DTest());
//        new LwjglApplication(new SpriteBatchTest());
        new Lwjgl3Application(new SpriteBatchTest2(), config);
//        new LwjglApplication(new TeaVMInputTest());
//        new LwjglApplication(new ReflectionTest());
//        new LwjglApplication(new ReadPixelsTest());
//        new LwjglApplication(new FilesTest());
//        new LwjglApplication(new LoadingTest());
//        new LwjglApplication(new GLTFQuickStartExample());
    }
}
