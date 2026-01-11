package emulate.java.lang;

import com.github.xpenatan.gdx.teavm.backend.web.TeaApplication;
import com.github.xpenatan.gdx.teavm.backend.web.gen.Emulate;

@Emulate(value = Throwable.class, updateCode = true)
public class ThrowableEmu {

    @Emulate
    public void printStackTrace() {
        TeaApplication.printErrorStack(this);
    }
}