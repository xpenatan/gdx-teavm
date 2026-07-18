import com.github.xpenatan.gdx.teavm.backends.android.AndroidApplication;
import com.github.xpenatan.gdx.teavm.backends.android.AndroidApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.examples.freetype.FreetypeDemo;

public class FreetypeAndroidLauncher {
    public static void main(String[] args) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        new AndroidApplication(new FreetypeDemo(), config);
    }
}
