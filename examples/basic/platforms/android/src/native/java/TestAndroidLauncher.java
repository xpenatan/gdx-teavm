import com.github.xpenatan.gdx.teavm.backends.android.AndroidApplication;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.UITest;

public class TestAndroidLauncher {
    public static void main(String[] args) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        new AndroidApplication(new UITest(), config);
    }
}
