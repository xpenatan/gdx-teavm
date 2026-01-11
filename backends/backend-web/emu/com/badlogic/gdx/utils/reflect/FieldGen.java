package com.badlogic.gdx.utils.reflect;

import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.backends.teavm.utils.GenericTypeProvider;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.Metaprogramming;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;
import org.teavm.metaprogramming.reflect.ReflectField;

@CompileTime
public class FieldGen {
    private static Class<?> getActualType(Type actualType) {
        if(actualType instanceof Class)
            return (Class)actualType;
        else if(actualType instanceof ParameterizedType)
            return (Class)((ParameterizedType)actualType).getRawType();
        else if(actualType instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)actualType).getGenericComponentType();
            if(componentType instanceof Class)
                return ArrayReflection.newInstance((Class)componentType, 0).getClass();
        }
        return null;
    }

    @Meta
    public static native Class getElementType(Class<?> type, String fieldName, int index);

    private static void getElementType(ReflectClass<?> cls, Value<String> fieldNameValue, Value<Integer> indexValue) {
        String name = cls.getName();
        if(!TeaReflectionSupplier.containsReflection(name)) {
            Metaprogramming.unsupportedCase();
            return;
        }
        ClassLoader classLoader = Metaprogramming.getClassLoader();
        GenericTypeProvider genericTypeProvider = new GenericTypeProvider(classLoader);

        boolean found = false;
        Value<Class> result = Metaprogramming.lazy(() -> null);
        for(ReflectField field : cls.getDeclaredFields()) {
            java.lang.reflect.Field javaField = genericTypeProvider.findField(field);
            if(javaField != null) {
                Type genericType = javaField.getGenericType();
                if(genericType instanceof ParameterizedType) {
                    Type[] actualTypes = ((ParameterizedType)genericType).getActualTypeArguments();

                    if(actualTypes != null) {
                        for(int i = 0; i < actualTypes.length; i++) {
                            Class actualType = getActualType(actualTypes[i]);
                            if(actualType == null)
                                continue;
                            found = true;
                            final int index = i;

                            String fieldName = field.getName();
                            Value<Class> existing = result;
                            result = Metaprogramming.lazy(() -> {
                                if(index == indexValue.get()) {
                                    if(fieldName.equals(fieldNameValue.get())) {
                                        return actualType;
                                    }
                                }
                                return existing.get();
                            });
                        }
                    }
                }
            }
        }
        if(!found) {
            Metaprogramming.unsupportedCase();
            return;
        }
        Value<Class> type = result;
        Metaprogramming.exit(() -> type.get());
    }
}