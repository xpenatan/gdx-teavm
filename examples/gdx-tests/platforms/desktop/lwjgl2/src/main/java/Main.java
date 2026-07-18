import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.xpenatan.imgui.example.tests.wrapper.TeaVMTestWrapper;

public class Main {
    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1444;
        config.height = 800;
        config.title = "gdx-tests";
        config.gles30ContextMajorVersion = 4;
        config.gles30ContextMinorVersion = 3;
        config.useGL30 = true;
        new LwjglApplication(new TeaVMTestWrapper(), config);
    }
}