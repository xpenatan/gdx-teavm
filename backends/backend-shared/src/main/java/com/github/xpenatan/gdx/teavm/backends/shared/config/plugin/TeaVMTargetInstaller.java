package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.lang.reflect.Field;
import java.util.function.Function;
import org.teavm.vm.TeaVMTarget;
import org.teavm.vm.spi.TeaVMHost;

public class TeaVMTargetInstaller {

    public static void install(TeaVMHost host, Class<?> wrapperType, Function<TeaVMTarget, TeaVMTarget> wrapper) {
        try {
            Field targetField = findTargetField(host.getClass());
            targetField.setAccessible(true);
            Object target = targetField.get(host);
            if(wrapperType.isInstance(target)) {
                return;
            }
            if(!(target instanceof TeaVMTarget)) {
                throw new IllegalStateException("TeaVM target field is not a TeaVMTarget: " + target);
            }
            targetField.set(host, wrapper.apply((TeaVMTarget)target));
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException("Unable to install gdx-teavm target hook", e);
        }
    }

    private static Field findTargetField(Class<?> type) throws NoSuchFieldException {
        Class<?> current = type;
        while(current != null) {
            try {
                return current.getDeclaredField("target");
            } catch(NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("target");
    }
}
