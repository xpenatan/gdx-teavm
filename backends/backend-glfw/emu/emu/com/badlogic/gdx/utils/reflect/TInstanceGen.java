package emu.com.badlogic.gdx.utils.reflect;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.Metaprogramming;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;

@CompileTime
public class TInstanceGen {
    @Meta
    public static native <T> T newInstance(Class<?> c);

    private static <T> void newInstance(ReflectClass<?> cls) {
        String name = cls.getName();
//        if (!TeaReflectionSupplier.containsReflection(name)) {
            Metaprogramming.unsupportedCase();
            return;
//        }

//        ClassLoader classLoader = Metaprogramming.getClassLoader();
//        Value<Object> instance = Metaprogramming.lazy(() -> {
//            try {
//                Class<?> javaClass = classLoader.loadClass(name);
//                return javaClass.getDeclaredConstructor().newInstance();
//            } catch (Exception e) {
//                throw new RuntimeException("Could not instantiate class: " + name, e);
//            }
//        });
//        Metaprogramming.exit(instance::get);
    }
}
