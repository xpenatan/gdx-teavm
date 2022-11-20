package com.badlogic.gdx.utils.reflect;

import com.github.xpenatan.gdx.backends.teavm.util.GenericTypeProvider;
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
public final class Field {

    private final java.lang.reflect.Field field;

    Field(java.lang.reflect.Field field) {
        this.field = field;
    }

    /**
     * Returns the name of the field.
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Returns a Class object that identifies the declared type for the field.
     */
    public Class getType() {
        return field.getType();
    }

    /**
     * Returns the Class object representing the class or interface that declares the field.
     */
    public Class getDeclaringClass() {
        return field.getDeclaringClass();
    }

    public boolean isAccessible() {
        return true;
    }

    public void setAccessible(boolean accessible) {
    }

    /**
     * Return true if the field does not include any of the {@code private}, {@code protected}, or {@code public} modifiers.
     */
    public boolean isDefaultAccess() {
        return !isPrivate() && !isProtected() && !isPublic();
    }

    /**
     * Return true if the field includes the {@code final} modifier.
     */
    public boolean isFinal() {
        // TODO
//		return Modifier.isFinal(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field includes the {@code private} modifier.
     */
    public boolean isPrivate() {
        // TODO
//		return Modifier.isPrivate(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field includes the {@code protected} modifier.
     */
    public boolean isProtected() {
        // TODO
//		return Modifier.isProtected(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field includes the {@code public} modifier.
     */
    public boolean isPublic() {
        // TODO
//		return Modifier.isPublic(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field includes the {@code static} modifier.
     */
    public boolean isStatic() {
        // TODO
//		return Modifier.isStatic(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field includes the {@code transient} modifier.
     */
    public boolean isTransient() {
        // TODO
//		return Modifier.isTransient(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field includes the {@code volatile} modifier.
     */
    public boolean isVolatile() {
        // TODO
//		return Modifier.isVolatile(field.getModifiers());
        return false;
    }

    /**
     * Return true if the field is a synthetic field.
     */
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
        //TODO fixme
//        if(!TeaReflectionSupplier.containsReflection(name)) {
//            Metaprogramming.unsupportedCase();
//            return;
//        }
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

    /**
     * If the type of the field is parameterized, returns the Class object representing the parameter type at the specified index,
     * null otherwise.
     */
    public Class getElementType(int index) {
        Class<?> declaringClass = field.getDeclaringClass();
        return getElementType(declaringClass, field.getName(), index);
    }

    /**
     * Returns true if the field includes an annotation of the provided class type.
     */
    public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }

    /**
     * Returns an array of {@link Annotation} objects reflecting all annotations declared by this field,
     * or an empty array if there are none. Does not include inherited annotations.
     */
    public Annotation[] getDeclaredAnnotations() {
        java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    /**
     * Returns an {@link Annotation} object reflecting the annotation provided, or null of this field doesn't
     * have such an annotation. This is a convenience function if the caller knows already which annotation
     * type he's looking for.
     */
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

    /**
     * Returns the value of the field on the supplied object.
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        return field.get(obj);
    }

    /**
     * Sets the value of the field on the supplied object.
     *
     * @throws ReflectionException
     */
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
