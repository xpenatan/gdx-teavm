package emulate.java.lang;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.gen.Emulate;

@Emulate(value = Throwable.class, updateCode = true)
public class ThrowableEmu {

    @Emulate
    public void printStackTrace() {
        WebApplication.printErrorStack(this);
    }
}