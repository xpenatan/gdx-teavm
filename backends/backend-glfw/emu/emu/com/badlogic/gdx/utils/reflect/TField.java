package emu.com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.FieldGen;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.lang.reflect.Modifier;

public final class TField {

    private final java.lang.reflect.Field field;

    public TField(java.lang.reflect.Field field) {
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
        return Modifier.isFinal(field.getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(field.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(field.getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(field.getModifiers());
    }

    public boolean isSynthetic() {
        return field.isSynthetic();
    }

    public Class getElementType(int index) {
        Class<?> declaringClass = field.getDeclaringClass();
        return FieldGen.getElementType(declaringClass, field.getName(), index);
    }

    public boolean isAnnotationPresent(Class<? extends java.lang.annotation.Annotation> annotationType) {
//        return field.isAnnotationPresent(annotationType);
        return false; // TODO not supported in TeaVM C
    }

    public TAnnotation[] getDeclaredAnnotations() {
        java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        TAnnotation[] result = new TAnnotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new TAnnotation(annotations[i]);
        }
        return result;
    }

    public TAnnotation getDeclaredAnnotation(Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = field.getDeclaredAnnotations();
        if(annotations == null) {
            return null;
        }
        for(java.lang.annotation.Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) {
                return new TAnnotation(annotation);
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
