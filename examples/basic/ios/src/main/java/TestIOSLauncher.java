import com.badlogic.gdx.ApplicationAdapter;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplication;
import com.github.xpenatan.gdx.teavm.backends.ios.IOSApplicationConfiguration;

public class TestIOSLauncher {
    public static void main(String[] args) {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        new IOSApplication(new ApplicationAdapter() {
            private int frames;

            @Override
            public void create() {
                System.out.println("gdx-teavm iOS spike create");
            }

            @Override
            public void resize(int width, int height) {
                System.out.println("gdx-teavm iOS spike resize " + width + "x" + height);
            }

            @Override
            public void render() {
                frames++;
                if(frames % 60 == 0) {
                    System.out.println("gdx-teavm iOS spike frame " + frames);
                }
            }

            @Override
            public void pause() {
                System.out.println("gdx-teavm iOS spike pause");
            }

            @Override
            public void resume() {
                System.out.println("gdx-teavm iOS spike resume");
            }

            @Override
            public void dispose() {
                System.out.println("gdx-teavm iOS spike dispose");
            }
        }, config);
    }
}
