package emulate.java.lang;

import com.github.xpenatan.gdx.teavm.backends.web.TeaWebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.gen.Emulate;

@Emulate(value = Throwable.class, updateCode = true)
public class ThrowableEmu {

    @Emulate
    public void printStackTrace() {
        TeaWebApplication.printErrorStack(this);
    }
}