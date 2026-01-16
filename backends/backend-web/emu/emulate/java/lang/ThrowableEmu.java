package emulate.java.lang;

import com.github.xpenatan.gdx.teavm.backends.web.TeaApplication;
import com.github.xpenatan.gdx.teavm.backends.web.gen.Emulate;

@Emulate(value = Throwable.class, updateCode = true)
public class ThrowableEmu {

    @Emulate
    public void printStackTrace() {
        TeaApplication.printErrorStack(this);
    }
}