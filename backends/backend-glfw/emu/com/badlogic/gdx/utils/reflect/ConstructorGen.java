package com.badlogic.gdx.utils.reflect;

import static org.teavm.metaprogramming.Metaprogramming.exit;
import static org.teavm.metaprogramming.Metaprogramming.unsupportedCase;
import emu.com.badlogic.gdx.utils.reflect.TConstructor;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import com.github.xpenatan.gdx.teavm.backends.shared.config.reflection.GenericTypeProvider;
import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.Metaprogramming;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;
import org.teavm.metaprogramming.reflect.ReflectMethod;
import java.lang.reflect.Constructor;
import java.util.Arrays;

@CompileTime
public class ConstructorGen {


    @Meta
    public static native Object newInstance(Class<?> cls, Object... args);
    private static void newInstance(ReflectClass<?> cls, Value<Object[]> args) {
        String name = cls.getName();
        if (!TeaReflectionSupplier.containsReflection(name)) {
            unsupportedCase();
            return;
        }

        ReflectMethod[] methods = cls.getDeclaredMethods();
        Value<Object> result = Metaprogramming.lazy(() -> null);
        boolean foundConstructor = false;
        for (ReflectMethod method : methods) {
            if (method.isConstructor()) {
                foundConstructor = true;
                int parameterCount = method.getParameterCount();
                ReflectMethod constructor = method;
                Value<Object> existing = result;
                result = Metaprogramming.lazy(() -> {
                    Object[] argArray = args.get();
                    if(argArray.length == parameterCount) {
                        return constructor.construct(argArray);
                    }
                    return existing.get();
                });
            }
        }

        if(!foundConstructor) {
            unsupportedCase();
            return;
        }

        Value<Object> finalResult = result;
        exit(() -> {
            Object object = finalResult.get();
            if (object == null) {
                throw new RuntimeException("No constructor found for " + name + " with " + args.get().length + " parameters");
            }
            return object;
        });
    }


    @Meta
    public static native Constructor getConstructor(Class<?> cls, Class[] parameterTypes);
    private static void getConstructor(ReflectClass<?> cls, Value<Class[]> parameterTypes) {
        String name = cls.getName();

        if (!TeaReflectionSupplier.containsReflection(name)) {
            unsupportedCase();
            return;
        }

        ReflectMethod[] methods = cls.getDeclaredMethods();
        ReflectMethod constructor = null;

        for (ReflectMethod method : methods) {
            if (method.isConstructor()) {
                ReflectClass<?>[] methodParams = method.getParameterTypes();
                if (methodParams.length == parameterTypes.get().length) {
                    boolean matches = true;
                    for (int i = 0; i < methodParams.length; i++) {
                        if (!methodParams[i].getName().equals(parameterTypes.get()[i].getName())) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        constructor = method;
                        break;
                    }
                }
            }
        }

        if (constructor == null) {
            unsupportedCase();
            return;
        }

        final ReflectMethod finalConstructor = constructor;
        exit(() -> {
            ClassLoader classLoader = Metaprogramming.getClassLoader();
            GenericTypeProvider genericTypeProvider = new GenericTypeProvider(classLoader);
            java.lang.reflect.Constructor<?> javaConstructor = genericTypeProvider.findConstructor(finalConstructor);
            Class<?> declaringClass = javaConstructor.getDeclaringClass();
            return new TConstructor(declaringClass, javaConstructor);
        });
    }
}
