package emu.java.lang;

import com.github.xpenatan.gdx.backends.web.emu.Emulate;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Emulate(value = Class.class, updateCode = true)
public class ClassEmu {

    @Emulate
    public boolean isAnonymousClass() {
        return false;
    }

    @Emulate
    public Method getEnclosingMethod() {
        return null;
    }

    @Emulate
    public Constructor<?> getEnclosingConstructor() {
        return null;
    }
}