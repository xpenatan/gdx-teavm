import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.EmptyApplicationTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GLTFQuickStartExample;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GearsDemo;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.ReflectionTest;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest2;

public class TestCLauncher {

    public static void main(String[] args) {
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        config.useVsync(false);
        config.setForegroundFPS(0);

        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
//        new GLFWApplication(new EmptyApplicationTest(), config);
//        new GLFWApplication(new GearsDemo());
//        new GLFWApplication(new SpriteBatchTest());
        new GLFWApplication(new SpriteBatchTest2(), config);
//        new GLFWApplication(new ReflectionTest(), config);
//        new GLFWApplication(new ReadPixelsTest());
//        new GLFWApplication(new Basic3DTest());
//        new GLFWApplication(new GLTFQuickStartExample());
//        new GLFWApplication(new HelloTriangle());
    }

}