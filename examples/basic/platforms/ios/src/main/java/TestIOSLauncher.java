import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.GearsDemo;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplication;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSFiles;

public class TestIOSLauncher {
    public static void main(String[] args) {
        if(args.length > 0) {
            IOSFiles.setLocalPath(args[0]);
        }
        ShaderProgram.pedantic = false;
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        new IOSApplication(new GearsDemo(), config);
    }
}
