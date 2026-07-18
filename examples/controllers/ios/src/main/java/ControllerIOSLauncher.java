import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplication;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSFiles;
import com.github.xpenatan.gdx.teavm.examples.controllers.ControllerDemo;

public class ControllerIOSLauncher {
    public static void main(String[] args) {
        if (args.length > 0) {
            IOSFiles.setLocalPath(args[0]);
        }
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        new IOSApplication(new ControllerDemo(), config);
    }
}
