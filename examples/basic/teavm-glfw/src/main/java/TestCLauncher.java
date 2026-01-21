import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GearsDemo;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWApplication;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.SpriteBatchTest;

public class TestCLauncher {

    public static void main(String[] args) {
        System.setProperty("os.name", "Windows"); // TODO figure out how to obtain OS name
//        new GLFWApplication(new TestCApplication());
//        new GLFWApplication(new GearsDemo());
        new GLFWApplication(new SpriteBatchTest());
//        new GLFWApplication(new ReadPixelsTest());
//        new GLFWApplication(new Basic3DTest());
//        new GLFWApplication(new HelloTriangle());
    }

}