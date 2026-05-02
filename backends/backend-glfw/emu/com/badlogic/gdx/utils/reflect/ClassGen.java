package com.badlogic.gdx.utils.reflect;

import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.Metaprogramming;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import static org.teavm.metaprogramming.Metaprogramming.exit;
import java.util.List;

@CompileTime
public class ClassGen {

    @Meta
    public static native Class forName(String name);
    private static void forName(Value<String> name) {
        List<String> reflectionClasses = TeaReflectionSupplier.getReflectionClasses();
        Value<Class> result = Metaprogramming.lazy(() -> null);
        for(String className : reflectionClasses) {
            ReflectClass<?> aClass = Metaprogramming.findClass(className);
            Value<Class> existing = result;
            result = Metaprogramming.lazy(() -> {
                if(className.equals(name.get())) {
                    return aClass.asJavaClass();
                }
                return existing.get();
            });
        }
        Value<Class> type = result;
        exit(() -> type.get());
    }
}
