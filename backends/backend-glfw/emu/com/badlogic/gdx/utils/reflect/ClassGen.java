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
        Value<Class> result = forName(name, reflectionClasses, 0, reflectionClasses.size());
        Value<Class> type = result;
        exit(() -> {
            String className = name.get();
            if(String.class.getName().equals(className)) return String.class;
            if(Boolean.class.getName().equals(className)) return Boolean.class;
            if(Byte.class.getName().equals(className)) return Byte.class;
            if(Character.class.getName().equals(className)) return Character.class;
            if(Short.class.getName().equals(className)) return Short.class;
            if(Integer.class.getName().equals(className)) return Integer.class;
            if(Long.class.getName().equals(className)) return Long.class;
            if(Float.class.getName().equals(className)) return Float.class;
            if(Double.class.getName().equals(className)) return Double.class;
            if(java.util.HashMap.class.getName().equals(className)) return java.util.HashMap.class;
            if(java.util.LinkedHashMap.class.getName().equals(className)) return java.util.LinkedHashMap.class;
            if(java.util.ArrayList.class.getName().equals(className)) return java.util.ArrayList.class;
            return type.get();
        });
    }

    private static Value<Class> forName(Value<String> name, List<String> reflectionClasses, int start, int end) {
        if(start >= end) {
            return Metaprogramming.lazy(() -> null);
        }
        if(end - start == 1) {
            String className = reflectionClasses.get(start);
            ReflectClass<?> aClass = Metaprogramming.findClass(className);
            if(aClass == null) {
                return Metaprogramming.lazy(() -> null);
            }
            return Metaprogramming.lazy(() -> {
                if(className.equals(name.get())) {
                    return aClass.asJavaClass();
                }
                return null;
            });
        }
        int middle = start + (end - start) / 2;
        Value<Class> left = forName(name, reflectionClasses, start, middle);
        Value<Class> right = forName(name, reflectionClasses, middle, end);
        return Metaprogramming.lazy(() -> {
            Class type = left.get();
            if(type != null) {
                return type;
            }
            return right.get();
        });
    }
}
