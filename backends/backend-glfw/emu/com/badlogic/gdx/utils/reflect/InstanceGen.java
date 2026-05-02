package com.badlogic.gdx.utils.reflect;

import static org.teavm.metaprogramming.Metaprogramming.exit;
import static org.teavm.metaprogramming.Metaprogramming.unsupportedCase;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.reflect.ReflectMethod;

@CompileTime
public class InstanceGen {

    @Meta
    public static native <T> T newInstance(Class<?> c);
    private static <T> void newInstance(ReflectClass<?> cls) {
        String name = cls.getName();

        if (!TeaReflectionSupplier.containsReflection(name)) {
            unsupportedCase();
            return;
        }

        ReflectMethod[] methods = cls.getDeclaredMethods();
        ReflectMethod constructor = null;

        for (ReflectMethod method : methods) {
            if (method.isConstructor() && method.getParameterCount() == 0) {
                constructor = method;
                break;
            }
        }

        if (constructor == null) {
            unsupportedCase();
            return;
        }

        final ReflectMethod finalConstructor = constructor;
        exit(() -> (T) finalConstructor.construct());
    }
}