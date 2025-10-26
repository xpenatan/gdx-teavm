package emulate.java.lang;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(value = Throwable.class, updateCode = true)
public class ThrowableEmu {

    @Emulate
    public void printStackTrace() {
        TeaApplication.printErrorStack(this);
    }
}