package com.badlogic.gdx.utils.reflect;

import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier;
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

    private final java.lang.reflect.Field field;

    public FieldGen(java.lang.reflect.Field field) {
        this.field = field;
    }

    public String getName() {
        return field.getName();
    }

    public Class getType() {
        return field.getType();
    }

    public Class getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public boolean isAccessible() {
        return true;
    }

    public void setAccessible(boolean accessible) {
    }

    public boolean isDefaultAccess() {
        return !isPrivate() && !isProtected() && !isPublic();
    }

    public boolean isFinal() {
        // TODO
//		return Modifier.isFinal(field.getModifiers());
        return false;
    }

    public boolean isPrivate() {
        // TODO
//		return Modifier.isPrivate(field.getModifiers());
        return false;
    }

    public boolean isProtected() {
        // TODO
//		return Modifier.isProtected(field.getModifiers());
        return false;
    }

    public boolean isPublic() {
        // TODO
//		return Modifier.isPublic(field.getModifiers());
        return false;
    }

    public boolean isStatic() {
        // TODO
//		return Modifier.isStatic(field.getModifiers());
        return false;
    }

    public boolean isTransient() {
        // TODO
//		return Modifier.isTransient(field.getModifiers());
        return false;
    }

    public boolean isVolatile() {
        // TODO
//		return Modifier.isVolatile(field.getModifiers());
        return false;
    }

    public boolean isSynthetic() {
        return field.isSynthetic();
    }

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

    public Class getElementType(int index) {
        Class<?> declaringClass = field.getDeclaringClass();
        return getElementType(declaringClass, field.getName(), index);
    }

    public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    public Annotation[] getDeclaredAnnotations() {
        java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    public Annotation getDeclaredAnnotation(Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        if(annotations == null) {
            return null;
        }
        for(java.lang.annotation.Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) {
                return new Annotation(annotation);
            }
        }
        return null;
    }

    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return field.get(obj);
    }

    public void set(Object obj, Object value) throws ReflectionException {
        try {
            field.set(obj, value);
        }
        catch(IllegalArgumentException e) {
            throw new ReflectionException("Argument not valid for field: " + getName(), e);
        }
        catch(IllegalAccessException e) {
            throw new ReflectionException("Illegal access to field: " + getName(), e);
        }
    }
}
