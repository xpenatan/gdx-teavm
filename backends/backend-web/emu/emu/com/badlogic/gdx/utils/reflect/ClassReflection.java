package emu.com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.lang.reflect.Modifier;

public final class ClassReflection {

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

    static public Constructor[] getConstructors(Class c) {
        java.lang.reflect.Constructor[] constructors = c.getConstructors();
        Constructor[] result = new Constructor[constructors.length];
        for(int i = 0, j = constructors.length; i < j; i++) {
            result[i] = new Constructor(constructors[i]);
        }
        return result;
    }

    static private Constructor getNoArgPublicConstructor(Class c) {
        java.lang.reflect.Constructor[] constructors = c.getConstructors();
        if(constructors.length > 0)
            return new Constructor(constructors[0]);
        return null;
    }

    static public Constructor getConstructor(Class c, Class... parameterTypes) throws ReflectionException {

        if(parameterTypes == null || parameterTypes.length == 0) {
            //Teavm does not accept null parameter to get public no args constructor. Need to do it manually
            return getNoArgPublicConstructor(c);
        }

        try {
            java.lang.reflect.Constructor constructor = c.getConstructor(parameterTypes);
            return new Constructor(constructor);
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation occurred while getting constructor for class: '" + c.getName() + "'.",
                    e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Constructor not found for class: " + c.getName(), e);
        }
    }

    static public Constructor getDeclaredConstructor(Class c, Class... parameterTypes) throws ReflectionException {
        try {
            java.lang.reflect.Constructor declaredConstructor = c.getDeclaredConstructor(parameterTypes);
            return new Constructor(declaredConstructor);
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

    static public Method[] getMethods(Class c) {
        java.lang.reflect.Method[] methods = c.getMethods();
        Method[] result = new Method[methods.length];
        for(int i = 0, j = methods.length; i < j; i++) {
            result[i] = new Method(methods[i]);
        }
        return result;
    }

    static public Method getMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
        try {
            return new Method(c.getMethod(name, parameterTypes));
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    static public Method[] getDeclaredMethods(Class c) {
        java.lang.reflect.Method[] methods = c.getDeclaredMethods();
        Method[] result = new Method[methods.length];
        for(int i = 0, j = methods.length; i < j; i++) {
            result[i] = new Method(methods[i]);
        }
        return result;
    }

    static public Method getDeclaredMethod(Class c, String name, Class... parameterTypes) throws ReflectionException {
        try {
            return new Method(c.getDeclaredMethod(name, parameterTypes));
        }
        catch(SecurityException e) {
            throw new ReflectionException("Security violation while getting method: " + name + ", for class: " + c.getName(), e);
        }
        catch(NoSuchMethodException e) {
            throw new ReflectionException("Method not found: " + name + ", for class: " + c.getName(), e);
        }
    }

    static public Field[] getFields(Class c) {
        // there is a bug in teavm that using just getFields the fields are not generated.
        c.getDeclaredFields();

        java.lang.reflect.Field[] fields = c.getFields();
        Field[] result = new Field[fields.length];
        for(int i = 0, j = fields.length; i < j; i++) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }

    static public Field getField(Class c, String name) throws ReflectionException {
        try {
            java.lang.reflect.Field field = c.getField(name);
            return new Field(field);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    static public Field[] getDeclaredFields(Class c) {
        java.lang.reflect.Field[] fields = c.getDeclaredFields();
        Field[] result = new Field[fields.length];
        for(int i = 0, j = fields.length; i < j; i++) {
            result[i] = new Field(fields[i]);
        }
        return result;
    }

    static public Field getDeclaredField(Class c, String name) throws ReflectionException {
        try {
            java.lang.reflect.Field declaredField = c.getDeclaredField(name);
            return new Field(declaredField);
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    static public boolean isAnnotationPresent(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return c.isAnnotationPresent(annotationType);
    }

    static public Annotation[] getAnnotations(Class c) {
        java.lang.annotation.Annotation[] annotations = c.getAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    static public Annotation getAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation annotation = c.getAnnotation(annotationType);
        if(annotation != null) return new Annotation(annotation);
        return null;
    }

    static public Annotation[] getDeclaredAnnotations(Class c) {
        java.lang.annotation.Annotation[] annotations = c.getDeclaredAnnotations();
        Annotation[] result = new Annotation[annotations.length];
        for(int i = 0; i < annotations.length; i++) {
            result[i] = new Annotation(annotations[i]);
        }
        return result;
    }

    static public Annotation getDeclaredAnnotation(Class c, Class<? extends java.lang.annotation.Annotation> annotationType) {
        java.lang.annotation.Annotation[] annotations = c.getDeclaredAnnotations();
        for(java.lang.annotation.Annotation annotation : annotations) {
            if(annotation.annotationType().equals(annotationType)) return new Annotation(annotation);
        }
        return null;
    }

    static public Class[] getInterfaces(Class c) {
        return c.getInterfaces();
    }
}
