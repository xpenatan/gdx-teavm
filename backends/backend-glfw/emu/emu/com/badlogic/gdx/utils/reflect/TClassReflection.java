package emu.com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.lang.reflect.Modifier;

public final class TClassReflection {

    static public Class forName(String name) throws ReflectionException {
        try {
            return Class.forName(name);
        }
        catch(ClassNotFoundException e) {
            throw new ReflectionException("Class not found: " + name, e);
        }
    }

    static public String getSimpleName(Class c) {
        return c.getSimpleName();
    }

    static public boolean isInstance(Class c, Object obj) {
        return obj != null && isAssignableFrom(c, obj.getClass());
    }

    static public boolean isAssignableFrom(Class c1, Class c2) {
        return c1.isAssignableFrom(c2);
    }

    static public boolean isMemberClass(Class c) {
        return c.isMemberClass();
    }

    static public boolean isStaticClass(Class c) {
        if(c.isInterface())
            return true;
        return Modifier.isStatic(c.getModifiers());
    }

    static public boolean isArray(Class c) {
        return c.isArray();
    }

    static public boolean isPrimitive(Class c) {
        return c.isPrimitive();
    }

    static public boolean isEnum(Class c) {
        return c.isEnum();
    }

    /**
     * Determines if the supplied Class object represents an annotation type.
     */
    static public boolean isAnnotation(Class c) {
        // TODO TeaVM does not have isAnnotation implemented
        return false;
//		return c.isAnnotation();
    }

    static public boolean isInterface(Class c) {
        return c.isInterface();
    }

    static public boolean isAbstract(Class c) {
        if(c.isPrimitive() || c.isArray() || c.isInterface())
            return true;
        return Modifier.isAbstract(c.getModifiers());
    }

    static public <T> T newInstance(Class<T> c) throws ReflectionException {
        try {
            return c.newInstance();
        }
        catch(InstantiationException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e);
        }
        catch(IllegalAccessException e) {
            throw new ReflectionException("Could not instantiate instance of class: " + c.getName(), e);
        }
    }

    static public Class getComponentType(Class c) {
        return c.getComponentType();
    }

    static public TConstructor[] getConstructors(Class c) {
        java.lang.reflect.Constructor[] constructors = c.getConstructors();
        TConstructor[] result = new TConstructor[constructors.length];
        for(int i = 0, j = constructors.length; i < j; i++) {
            result[i] = new TConstructor(constructors[i]);
        }
        return result;
    }

    static private TConstructor getNoArgPublicConstructor(Class c) {
        java.lang.reflect.Constructor[] constructors = c.getConstructors();
        if(constructors.length > 0)
            return new TConstructor(constructors[0]);
        return null;
    }

    static public TConstructor getConstructor(Class c, Class... parameterTypes) throws ReflectionException {

        if(parameterTypes == null || parameterTypes.length == 0) {
            //Teavm does not accept null parameter to get public no args constructor. Need to do it manually
            return getNoArgPublicConstructor(c);
        }

        try {
            java.lang.reflect.Constructor constructor = c.getConstructor(parameterTypes);
            return new TConstructor(constructor);
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation occurred while getting constructor for class: '" + c.getName() + "'.",
                    e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e);
        }
    }

    static public TConstructor getDeclaredConstructor(Class c, Class... parameterTypes) throws ReflectionException {
        try {
            java.lang.reflect.Constructor declaredConstructor = c.getDeclaredConstructor(parameterTypes);
            return new TConstructor(declaredConstructor);
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation while getting constructor for class: " + c.getName(), e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e);
        }
    }

    static public Object[] getEnumConstants(Class c) {
        return c.getEnumConstants();
    }

    static public TMethod[] getMethods(Class c) {
        java.lang.reflect.Method[] methods = c.getMethods();
        TMethod[] result = new TMethod[methods.length];
        for(int i = 0, j = methods.length; i < j; i++) {
            result[i] = new TMethod(methods[i]);
        }
        return result;
    }

    static public TMethod getMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
        try {
            return new TMethod(c.getMethod(name, parameterTypes));
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    static public TMethod[] getDeclaredMethods(Class c) {
        java.lang.reflect.Method[] methods = c.getDeclaredMethods();
        TMethod[] result = new TMethod[methods.length];
        for(int i = 0, j = methods.length; i < j; i++) {
            result[i] = new TMethod(methods[i]);
        }
        return result;
    }

    static public TMethod getDeclaredMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
        try {
            return new TMethod(c.getDeclaredMethod(name, parameterTypes));
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    static public TField[] getFields(Class c) {
        // there is a bug in teavm that using just getFields the fields are not generated.
        c.getDeclaredFields();

        java.lang.reflect.Field[] fields = c.getFields();
        TField[] result = new TField[fields.length];
        for(int i = 0, j = fields.length; i < j; i++) {
            result[i] = new TField(fields[i]);
        }
        return result;
    }

    static public TField getField(Class c, String name) throws ReflectionException {
        try {
            java.lang.reflect.Field field = c.getField(name);
            return new TField(field);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    static public TField[] getDeclaredFields(Class c) {
        java.lang.reflect.Field[] fields = c.getDeclaredFields();
        TField[] result = new TField[fields.length];
        for(int i = 0, j = fields.length; i < j; i++) {
            result[i] = new TField(fields[i]);
        }
        return result;
    }

    static public TField getDeclaredField(Class c, String name) throws ReflectionException {
        try {
            java.lang.reflect.Field declaredField = c.getDeclaredField(name);
            return new TField(declaredField);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    static public boolean isAnnotationPresent(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return c.isAnnotationPresent(annotationType);
    }

    static public TAnnotation[] getAnnotations(Class c) {
        java.lang.annotation.Annotation[] annotations = c.getAnnotations();
        TAnnotation[] result = new TAnnotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new TAnnotation(annotations[i]);
        }
        return result;
    }

    static public TAnnotation getAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation annotation = c.getAnnotation(annotationType);
        if(annotation != null) return new TAnnotation(annotation);
        return null;
    }

    static public TAnnotation[] getDeclaredAnnotations(Class c) {
        java.lang.annotation.Annotation[] annotations = c.getDeclaredAnnotations();
        TAnnotation[] result = new TAnnotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new TAnnotation(annotations[i]);
        }
        return result;
    }

    static public TAnnotation getDeclaredAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = c.getDeclaredAnnotations();
        for(java.lang.annotation.Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) return new TAnnotation(annotation);
        }
        return null;
    }

    static public Class[] getInterfaces(Class c) {
        return c.getInterfaces();
    }
}
