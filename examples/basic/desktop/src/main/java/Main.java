import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.EmptyApplicationTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest2;

public class Main {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);
//        new Lwjgl3Application(new GearsDemo());
//        new Lwjgl3Application(new Basic3DTest());
//        new Lwjgl3Application(new SpriteBatchTest());
//        new Lwjgl3Application(new EmptyApplicationTest(), config);
        new Lwjgl3Application(new SpriteBatchTest2(), config);
//        new Lwjgl3Application(new TeaVMInputTest());
//        new Lwjgl3Application(new ReflectionTest());
//        new Lwjgl3Application(new ReadPixelsTest());
//        new Lwjgl3Application(new FilesTest());
//        new Lwjgl3Application(new LoadingTest());
//        new Lwjgl3Application(new GLTFQuickStartExample());
    }
}
