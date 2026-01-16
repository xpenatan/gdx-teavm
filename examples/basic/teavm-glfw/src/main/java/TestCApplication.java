import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL32;

public class TestCApplication implements ApplicationListener {

    @Override
    public void create() {
        System.out.println("CREATE");
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("RESIZE: " + width + ", " + height);
    }

    @Override
    public void render() {
        System.out.println("Loop");
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void pause() {
        System.out.println("PAUSE");
    }

    @Override
    public void resume() {
        System.out.println("RESUME");
    }

    @Override
    public void dispose() {
        System.out.println("DISPOSE");
    }
}